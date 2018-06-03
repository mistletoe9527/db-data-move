package com.potlid.handler;

import com.potlid.common.define.Condition;
import com.potlid.common.define.Transfer;
import com.potlid.common.define.data.FieldData;
import com.potlid.common.define.data.TransferConditionData;
import com.potlid.common.define.job.TransferDescribe;
import com.potlid.common.util.StringConvertUtil;
import com.potlid.manager.DataSourceManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by styb on 2018/4/3.
 */
public class DefaultAnnotationHandler implements AnnotationHandler{


    public TransferDescribe handleAnnotation(Object bean) throws Exception {
        TransferDescribe transferJob = new TransferDescribe();
        List<String> selectFields = new ArrayList<String>();
        Map<String,FieldData> insertFieldMap = new HashMap<String, FieldData>();
        List<String> conditionFields = new ArrayList<String>();
        Map<String,String> conditionNameMap = new HashMap<String, String>();
        Map<String, String> fieldOpratorMap = new HashMap<String, String>();
        Map<String,Object> insertValue=new HashMap<String, Object>();
        List<String> uniqueFields=new ArrayList<String>();
        Transfer transfer = bean.getClass().getAnnotation(Transfer.class);
        if (transfer != null) {
            if (StringUtils.isNotEmpty(transfer.after())) {
                transferJob.setAfterTableName(transfer.after());
            } else {
                transferJob.setAfterTableName(StringConvertUtil.camel2Underline(StringConvertUtil.convertClassName(bean.getClass())));
            }
            if (StringUtils.isNotEmpty(transfer.before())) {
                transferJob.setBeforeTableName(transfer.before());
            } else {
                transferJob.setBeforeTableName(StringConvertUtil.camel2Underline(StringConvertUtil.convertClassName(bean.getClass())));
            }
            transferJob.setAfterDataSource(transfer.afterDataSource());
            transferJob.setBeforeDataSource(transfer.beforeDataSource());

            if(StringUtils.isBlank(transferJob.getAfterDataSource()) || StringUtils.isBlank(transferJob.getBeforeDataSource())){
                throw new Exception("no datasource in transfer annotation!");
            }

            if(DataSourceManager.getDataSourceMap().get(transferJob.getAfterDataSource()) ==null || DataSourceManager.getDataSourceMap().get(transferJob.getBeforeDataSource())==null){
                throw new Exception("no valid datasources!");
            }
            transferJob.setTransferMode(transfer.transferMode());
        }
        Field[] fields = bean.getClass().getDeclaredFields();
        if (ArrayUtils.isNotEmpty(fields)) {
            for (Field f : fields) {
                f.setAccessible(true);
                com.potlid.common.define.Field field = f.getAnnotation(com.potlid.common.define.Field.class);
                if (field != null) {

                    if(field.select()){
                        if (StringUtils.isNotEmpty(field.before())) {
                            selectFields.add(field.before());
                        } else {
                            selectFields.add(StringConvertUtil.camel2Underline(f.getName()));
                        }
                    }
                    if(field.insert()){
                        String selectField=null;
                        if(field.select()){
                            selectField=StringUtils.isNotEmpty(field.before())?field.before():StringConvertUtil.camel2Underline(f.getName());
                        }
                        if (StringUtils.isNotEmpty(field.after())) {
                            insertFieldMap.put(field.after(),new FieldData().setSelectField(selectField).setField(f.getName()));
                        } else {
                            insertFieldMap.put(StringConvertUtil.camel2Underline(f.getName()), new FieldData().setSelectField(selectField).setField(f.getName()));
                        }
                    }
                    if(StringUtils.isNotEmpty(field.insertValue())){
                        insertValue.put(StringUtils.isNotEmpty(field.after())?field.after():StringConvertUtil.camel2Underline(f.getName()),field.insertValue());
                    }

                    if(field.unique() && field.insert() && field.select()){
                        uniqueFields.add(StringUtils.isNotEmpty(field.after())?field.after():StringConvertUtil.camel2Underline(f.getName()));
                    }

                }
                transferJob.setInsertValue(insertValue);
                Condition condition = f.getAnnotation(Condition.class);
                if (condition != null) {
                    if (condition.select()) {
                        if (StringUtils.isNotEmpty(condition.before())) {
                            selectFields.add(condition.before());
                        } else {
                            selectFields.add(StringConvertUtil.camel2Underline(f.getName()));
                        }
                    }
                    if(condition.insert()){
                        String selectField=null;
                        if(condition.select()){
                            selectField=StringUtils.isNotEmpty(condition.before())?condition.before():StringConvertUtil.camel2Underline(f.getName());
                        }
                        if (StringUtils.isNotEmpty(condition.after())) {
                            insertFieldMap.put(condition.after(), new FieldData().setSelectField(selectField).setField(f.getName()));
                        } else {
                            insertFieldMap.put(StringConvertUtil.camel2Underline(f.getName()), new FieldData().setSelectField(selectField).setField(f.getName()));
                        }
                    }
                    conditionFields.add(StringUtils.isNotEmpty(condition.before()) ? condition.before() : StringConvertUtil.camel2Underline(f.getName()));
                    conditionNameMap.put(f.getName(), StringUtils.isNotEmpty(condition.before()) ? condition.before() : StringConvertUtil.camel2Underline(f.getName()));
                    fieldOpratorMap.put(StringUtils.isNotEmpty(condition.before())?condition.before():StringConvertUtil.camel2Underline(f.getName()), condition.operator());


                    if(condition.unique() && condition.insert() && condition.select()){
                        uniqueFields.add(StringUtils.isNotEmpty(condition.after()) ? condition.after() : StringConvertUtil.camel2Underline(f.getName()));
                    }

                }
            }
        }

        if (!CollectionUtils.isEmpty(conditionFields)) {
            if (CollectionUtils.isEmpty(fieldOpratorMap) || fieldOpratorMap.size() != conditionFields.size()) {
                throw new Exception("the condition annotation is error!");
            }
            if (!(bean instanceof ConditionDataHandler)) {
                throw new Exception("no implement ConditionDataHandler!");
            }
        }

        if (!CollectionUtils.isEmpty(selectFields) && !CollectionUtils.isEmpty(insertFieldMap.keySet())) {
            transferJob.setConditionFields(conditionFields);
            transferJob.setFieldOpratorMap(fieldOpratorMap);
            transferJob.setInsertFieldMap(insertFieldMap);
            transferJob.setSelectFields(selectFields);
            transferJob.setUniqueFields(uniqueFields);
            if (bean instanceof ConditionDataHandler) {
                Method method = ReflectionUtils.findMethod(bean.getClass(), "handle");
                List<TransferConditionData> transferConditionDataList = (List<TransferConditionData>) method.invoke(bean, null);
                if (!CollectionUtils.isEmpty(transferConditionDataList)) {
                    for(TransferConditionData transferConditionData:transferConditionDataList){
                        for (String conditionNameField : conditionNameMap.keySet()) {
                            if (!transferConditionData.getConditionDataMap().keySet().contains(conditionNameField)) {
                                throw new Exception("condition field data is null");
                            }
                        }
                    }

                }else{
                    if(!CollectionUtils.isEmpty(conditionFields)){
                        throw new Exception("method handler return List<TransferConditionDataList> is null !");
                    }
                }
                if(!CollectionUtils.isEmpty(transferConditionDataList)){
                    for(TransferConditionData transferConditionData:transferConditionDataList){
                        if(!CollectionUtils.isEmpty(transferConditionData.getConditionDataMap())){
                            Map<String,Object> finalConditionMap=new HashMap<String, Object>();
                            Set<String> keySet=transferConditionData.getConditionDataMap().keySet();
                            for(String key:keySet){
                                finalConditionMap.put(conditionNameMap.get(key), transferConditionData.getConditionDataMap().get(key));
                                finalConditionMap.put(key, transferConditionData.getConditionDataMap().get(key));
                            }
                            transferConditionData.setConditionDataMap(finalConditionMap);
                        }
                    }
                    transferJob.setTransferConditionDataList(transferConditionDataList);
                }
            }
            transferJob.setBean(bean);
        }else{
            throw new Exception("no field annotation !");
        }
        return transferJob;
    }

    public TransferDescribe handleAnnotation(Object bean, TransferDescribe transferJob) throws Exception {
        return handleAnnotation(bean);
    }
}

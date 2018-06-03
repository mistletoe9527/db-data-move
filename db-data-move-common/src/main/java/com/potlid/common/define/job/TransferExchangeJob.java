package com.potlid.common.define.job;



import com.potlid.common.define.TransferMode;
import com.potlid.common.define.data.FieldData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by styb on 2018/3/31.
 */

/**
 * transferJob 转移后的 job
 */
public class TransferExchangeJob {

    private Map<String,String> conditionFieldMap;

    private Map<String,FieldData> insertFieldMap;

    private List<ExecuteSqlJob> executeSqlJobList;

    private String beforeDataSource;

    private String afterDataSource;

    private Object bean;


    private TransferMode transferMode;

    private Map<String,Object> insertValue=new HashMap<String, Object>();

    private List<String> uniqueFields;

    public List<String> getUniqueFields() {
        return uniqueFields;
    }

    public void setUniqueFields(List<String> uniqueFields) {
        this.uniqueFields = uniqueFields;
    }

    public Map<String, Object> getInsertValue() {
        return insertValue;
    }

    public void setInsertValue(Map<String, Object> insertValue) {
        this.insertValue = insertValue;
    }

    public TransferMode getTransferMode() {
        return transferMode;
    }

    public void setTransferMode(TransferMode transferMode) {
        this.transferMode = transferMode;
    }


    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public String getBeforeDataSource() {
        return beforeDataSource;
    }

    public void setBeforeDataSource(String beforeDataSource) {
        this.beforeDataSource = beforeDataSource;
    }

    public String getAfterDataSource() {
        return afterDataSource;
    }

    public void setAfterDataSource(String afterDataSource) {
        this.afterDataSource = afterDataSource;
    }

    public Map<String, FieldData> getInsertFieldMap() {
        return insertFieldMap;
    }

    public void setInsertFieldMap(Map<String, FieldData> insertFieldMap) {
        this.insertFieldMap = insertFieldMap;
    }

    public List<ExecuteSqlJob> getExecuteSqlJobList() {
        return executeSqlJobList;
    }

    public void setExecuteSqlJobList(List<ExecuteSqlJob> executeSqlJobList) {
        this.executeSqlJobList = executeSqlJobList;
    }

    public Map<String, String> getConditionFieldMap() {
        return conditionFieldMap;
    }

    public void setConditionFieldMap(Map<String, String> conditionFieldMap) {
        this.conditionFieldMap = conditionFieldMap;
    }
}

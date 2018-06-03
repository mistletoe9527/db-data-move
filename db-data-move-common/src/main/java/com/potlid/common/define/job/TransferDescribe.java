package com.potlid.common.define.job;


import com.potlid.common.define.TransferMode;
import com.potlid.common.define.data.FieldData;
import com.potlid.common.define.data.TransferConditionData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by styb on 2018/3/31.
 */

/**
 * 数据转化类
 */
public class TransferDescribe {

    private String beforeTableName;

    private String afterTableName;

    private Map<String,FieldData> insertFieldMap;

    private List<String> selectFields;

    private List<String> conditionFields;

    private String beforeDataSource;

    private String afterDataSource;

    private Map<String,String> fieldOpratorMap;


    private List<TransferConditionData> transferConditionDataList;

    private Map<String,String> conditionFieldMap;

    private Object bean;

    private TransferMode transferMode;

    private Map<String,Object> insertValue=new HashMap<String,Object>();

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

    public Map<String, String> getConditionFieldMap() {
        return conditionFieldMap;
    }

    public void setConditionFieldMap(Map<String, String> conditionFieldMap) {
        this.conditionFieldMap = conditionFieldMap;
    }

    public List<TransferConditionData> getTransferConditionDataList() {
        return transferConditionDataList;
    }

    public void setTransferConditionDataList(List<TransferConditionData> transferConditionDataList) {
        this.transferConditionDataList = transferConditionDataList;
    }

    public Map<String, FieldData> getInsertFieldMap() {
        return insertFieldMap;
    }

    public void setInsertFieldMap(Map<String, FieldData> insertFieldMap) {
        this.insertFieldMap = insertFieldMap;
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


    public Map<String, String> getFieldOpratorMap() {
        return fieldOpratorMap;
    }

    public void setFieldOpratorMap(Map<String, String> fieldOpratorMap) {
        this.fieldOpratorMap = fieldOpratorMap;
    }

    public String getBeforeTableName() {
        return beforeTableName;
    }

    public void setBeforeTableName(String beforeTableName) {
        this.beforeTableName = beforeTableName;
    }

    public String getAfterTableName() {
        return afterTableName;
    }

    public void setAfterTableName(String afterTableName) {
        this.afterTableName = afterTableName;
    }

    public List<String> getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(List<String> selectFields) {
        this.selectFields = selectFields;
    }

    public List<String> getConditionFields() {
        return conditionFields;
    }

    public void setConditionFields(List<String> conditionFields) {
        this.conditionFields = conditionFields;
    }
}

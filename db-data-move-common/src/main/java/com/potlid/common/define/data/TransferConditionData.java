package com.potlid.common.define.data;
import java.util.Map;

/**
 * Created by styb on 2018/4/8.
 */
public class TransferConditionData {

    private String beforeTableName;

    private String afterTableName;

    private Map<String,Object> conditionDataMap;

    public Map<String, Object> getConditionDataMap() {
        return conditionDataMap;
    }

    public TransferConditionData setConditionDataMap(Map<String, Object> conditionDataMap) {
        this.conditionDataMap = conditionDataMap;
        return this;
    }

    public String getBeforeTableName() {
        return beforeTableName;
    }

    public TransferConditionData setBeforeTableName(String beforeTableName) {
        this.beforeTableName = beforeTableName;
        return this;
    }

    public String getAfterTableName() {
        return afterTableName;
    }

    public TransferConditionData setAfterTableName(String afterTableName) {
        this.afterTableName = afterTableName;
        return this;
    }
}

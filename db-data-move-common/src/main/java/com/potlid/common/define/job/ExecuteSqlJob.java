package com.potlid.common.define.job;



import com.potlid.common.define.data.TransferConditionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by styb on 2018/4/5.
 */
public class ExecuteSqlJob {

    private long minId;

    private long maxId;

    private String selectSql;

    private String insertSql;

    private String afterTable;

    private String maxIdSql;

    private TransferConditionData transferConditionData;

    public TransferConditionData getTransferConditionData() {
        return transferConditionData;
    }

    public void setTransferConditionData(TransferConditionData transferConditionData) {
        this.transferConditionData = transferConditionData;
    }

    public String getMaxIdSql() {
        return maxIdSql;
    }

    public void setMaxIdSql(String maxIdSql) {
        this.maxIdSql = maxIdSql;
    }
    public long getMinId() {
        return minId;
    }

    public void setMinId(long minId) {
        this.minId = minId;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public String getAfterTable() {
        return afterTable;
    }

    public void setAfterTable(String afterTable) {
        this.afterTable = afterTable;
    }

    private List<Map<String,Object>> selectDataList=new ArrayList<Map<String, Object>>();

    public List<Map<String, Object>> getSelectDataList() {
        return selectDataList;
    }

    public void setSelectDataList(List<Map<String, Object>> selectDataList) {
        this.selectDataList = selectDataList;
    }

    public String getSelectSql() {
        return selectSql;
    }

    public void setSelectSql(String selectSql) {
        this.selectSql = selectSql;
    }

    public String getInsertSql() {
        return insertSql;
    }

    public void setInsertSql(String insertSql) {
        this.insertSql = insertSql;
    }


}

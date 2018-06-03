package com.potlid.common.define.data;


/**
 * Created by styb on 2018/4/16.
 */
public class FieldData {

    private String selectField;

    private String field;

    private String insertField;

    public String getSelectField() {
        return selectField;
    }

    public FieldData setSelectField(String selectField) {
        this.selectField = selectField;
        return this;
    }

    public String getField() {
        return field;
    }

    public FieldData setField(String field) {
        this.field = field;
        return this;
    }

    public String getInsertField() {
        return insertField;
    }

    public FieldData setInsertField(String insertField) {
        this.insertField = insertField;
        return this;
    }
}

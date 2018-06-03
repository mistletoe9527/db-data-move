package com.potlid.adapter;


import com.potlid.common.define.data.TransferConditionData;
import com.potlid.handler.ConditionDataHandler;

import java.util.List;

/**
 * Created by styb on 2018/4/14.
 */
public abstract class ConditionDataAdapter implements ConditionDataHandler {

    private boolean isRestart=false;

    public List<TransferConditionData> handle() throws Exception {
        return null;
    }

    public Object convert(String filedName, Object value) throws Exception {
        return value;
    }

    public void afterExecute(TransferConditionData transferConditionData) throws Exception {

    }

    public boolean restart() {
        return isRestart;
    }

    public void restart(boolean isRestart) {
        this.isRestart=isRestart;
    }
}

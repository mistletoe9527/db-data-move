package com.potlid.handler;


import com.potlid.common.define.data.TransferConditionData;

import java.util.List;

/**
 * Created by styb on 2018/4/3.
 */
public interface ConditionDataHandler {

    List<TransferConditionData> handle() throws Exception;

    Object convert(String filedName, Object value) throws Exception;

    void afterExecute(TransferConditionData transferConditionData) throws Exception;

    boolean restart();
}

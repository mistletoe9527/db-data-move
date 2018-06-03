package com.potlid.test;


import com.potlid.adapter.ConditionDataAdapter;
import com.potlid.common.define.Field;
import com.potlid.common.define.Transfer;
import com.potlid.common.define.data.TransferConditionData;

import java.util.*;

/**
 * Created by styb on 2018/4/24.
 */
@Transfer(beforeDataSource = "dataSource1" ,afterDataSource = "dataSource1")
public class Test extends ConditionDataAdapter {

    @Field()
    private String userName;
    @Field
    private Date created;

    @Override
    public List<TransferConditionData> handle() throws Exception {
        // 业务逻辑 构建 TransferConditionData list
        List<TransferConditionData> list=new ArrayList<>();
        Map<String,Object> m=new HashMap<>();
        m.put("userName",null);
        list.add(new TransferConditionData().setConditionDataMap(m).setBeforeTableName("test1").setAfterTableName("test2"));
        return list;
    }


    public static void main(String[] args) {
        System.out.println(2533366486l%200);
    }


}



package com.potlid.manager;
import com.potlid.common.define.data.TransferConditionData;
import com.potlid.common.define.job.ExecuteSqlJob;
import com.potlid.common.define.job.TransferDescribe;
import com.potlid.common.define.job.TransferExchangeJob;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by styb on 2018/4/4.
 */
public class TransferJobManager {

    private BlockingQueue<TransferExchangeJob> transferJobs=new LinkedBlockingQueue<TransferExchangeJob>();
    private TransferJobManager(){}
    public static TransferJobManager getInstance(){
        return Holder.instance;
    }
    private static class Holder{
        private static TransferJobManager instance = new TransferJobManager();
    }

    /**
     * 检查transferjob 是否符合标准
     * @param transferDescribe
     */
    public  void inspect(TransferDescribe transferDescribe) throws Exception{

        //字段不能写错 检验太麻烦 不校验了
        //任务转化为可以执行的任务
        TransferExchangeJob transferExchangeJob=new TransferExchangeJob();
        List<ExecuteSqlJob> executeSqlJobList=generateSelectSql(transferDescribe);
        if(!CollectionUtils.isEmpty(executeSqlJobList)){
            transferExchangeJob.setInsertFieldMap(transferDescribe.getInsertFieldMap());
            transferExchangeJob.setExecuteSqlJobList(executeSqlJobList);
            transferExchangeJob.setConditionFieldMap(transferDescribe.getConditionFieldMap());
            transferExchangeJob.setAfterDataSource(transferDescribe.getAfterDataSource());
            transferExchangeJob.setBeforeDataSource(transferDescribe.getBeforeDataSource());
            transferExchangeJob.setBean(transferDescribe.getBean());
            transferExchangeJob.setTransferMode(transferDescribe.getTransferMode());
            transferExchangeJob.setInsertValue(transferDescribe.getInsertValue());
            transferExchangeJob.setUniqueFields(transferDescribe.getUniqueFields());
            transferJobs.offer(transferExchangeJob);
        }
    }

    /**
     * 任务转化
     * @param transferDescribe
     * @return
     * @throws Exception
     */
    private static List<ExecuteSqlJob> generateSelectSql(TransferDescribe transferDescribe) throws Exception{
        List<ExecuteSqlJob> executeSqlBeans=new ArrayList<ExecuteSqlJob>();
        if(!CollectionUtils.isEmpty(transferDescribe.getTransferConditionDataList())){
            for(TransferConditionData transferConditionData:transferDescribe.getTransferConditionDataList()){
                ExecuteSqlJob executeSqlBean=new ExecuteSqlJob();
                executeSqlBean.setAfterTable(StringUtils.isNotBlank(transferConditionData.getAfterTableName())?transferConditionData.getAfterTableName():transferDescribe.getAfterTableName());
                SqlManager.generateSelectSql(transferDescribe, StringUtils.isNotBlank(transferConditionData.getBeforeTableName())?transferConditionData.getBeforeTableName():transferDescribe.getBeforeTableName(),transferConditionData.getConditionDataMap(),executeSqlBean);
                executeSqlBean.setTransferConditionData(transferConditionData);
                executeSqlBeans.add(executeSqlBean);
            }
        }else{
            ExecuteSqlJob executeSqlBean=new ExecuteSqlJob();
            executeSqlBean.setAfterTable(transferDescribe.getAfterTableName());
            SqlManager.generateSelectSql(transferDescribe, transferDescribe.getBeforeTableName(),null,executeSqlBean);
            executeSqlBeans.add(executeSqlBean);
        }
        return executeSqlBeans;
    }



    public BlockingQueue<TransferExchangeJob> getTransferJobs() {
        return transferJobs;
    }

}

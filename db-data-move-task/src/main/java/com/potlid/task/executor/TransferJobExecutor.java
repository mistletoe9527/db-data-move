package com.potlid.task.executor;

import com.alibaba.fastjson.JSONObject;
import com.potlid.TransferProperties;
import com.potlid.common.define.TransferMode;
import com.potlid.common.define.job.ExecuteSqlJob;
import com.potlid.common.define.job.TransferExchangeJob;
import com.potlid.handler.ConditionDataHandler;
import com.potlid.manager.SqlManager;
import com.potlid.manager.TransferJobManager;
import com.potlid.task.Start;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by styb on 2018/4/9.
 * 任务执行器
 */
public class TransferJobExecutor {

    private static final Logger logger=Logger.getLogger(TransferJobExecutor.class);
    private int THREAD_COUNT=5;
    private int EXECUTE_COUNT=10;
    private final int BATCH_COUNT=2000;
    private ExecutorService jobExecutor;
    public static TransferJobExecutor executor=new TransferJobExecutor();
    public AtomicBoolean isContienue=new AtomicBoolean(true);
    private  ExecutorService subJobExecutor;
    private Semaphore semaphore;
    public  void start() throws Exception{
        init();
        jobExecutor.execute(new Runnable() {
            public void run() {
                for(;;){
                    BlockingQueue<TransferExchangeJob> blockingQueue= TransferJobManager.getInstance().getTransferJobs();
                    try{
                        TransferExchangeJob transferExchangeJob=blockingQueue.poll();
                        if(transferExchangeJob!=null){
                            handlerTransferExchangeJob(transferExchangeJob);
                            //处理完成是否需要restart()
                            if(transferExchangeJob.getBean() instanceof ConditionDataHandler){
                                if(((ConditionDataHandler) transferExchangeJob.getBean()).restart()){
                                    Start.getInstance().restart();
                                }
                            }
                        }else{
                            Thread.sleep(1000);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });

    }
    public void shutDown(){
        if(jobExecutor!=null) jobExecutor.shutdown();
        if(subJobExecutor!=null) subJobExecutor.shutdown();
    }

    private void handlerTransferExchangeJob(final TransferExchangeJob transferExchangeJob) throws Exception{
        if(!CollectionUtils.isEmpty(transferExchangeJob.getExecuteSqlJobList())){

            Queue<ExecuteSqlJob> executeSqlJobQueue=new LinkedBlockingQueue<ExecuteSqlJob>(transferExchangeJob.getExecuteSqlJobList().size());
            executeSqlJobQueue.addAll(transferExchangeJob.getExecuteSqlJobList());
            ExecuteSqlJob executeSqlJob=null;
            final ExecuteSqlJob runExecuteSqlJob=new ExecuteSqlJob();
            while ((executeSqlJob=executeSqlJobQueue.poll())!=null){
                if(!isContienue.get()){
                    throw new Exception("transfer error please check the data is correct!");
                }
                BeanUtils.copyProperties(executeSqlJob,runExecuteSqlJob);
                semaphore.acquire();
                subJobExecutor.execute(new Runnable() {
                    public void run() {
                        try{
                            String replaceSql = null;
                            replaceSql = handlerExecuteSqlJob(runExecuteSqlJob, transferExchangeJob, replaceSql);
                            if (transferExchangeJob.getBean() != null && transferExchangeJob.getBean() instanceof ConditionDataHandler) {
                                ((ConditionDataHandler) transferExchangeJob.getBean()).afterExecute(runExecuteSqlJob.getTransferConditionData());
                            }
                            //回调之后再看下是否数据已经全部迁移完成
                            runExecuteSqlJob.setMinId(runExecuteSqlJob.getMaxId() + 1);
                            SqlManager.resetMaxId(runExecuteSqlJob, transferExchangeJob);
                            handlerExecuteSqlJob(runExecuteSqlJob, transferExchangeJob, replaceSql);
                            logger.info("executeSqlJob finish ==" + JSONObject.toJSONString(runExecuteSqlJob.getTransferConditionData()));
                        }catch (Exception e){
                            isContienue.set(false);
                            e.printStackTrace();
                        }finally {
                            semaphore.release();
                        }
                    }
                });
            }
        }
    }

    /**
     * 初始化
     */
    private void init(){
        Object value=TransferProperties.getInstance().getProperties().get(TransferProperties.THREAD_COUNT);
        Object executeValue=TransferProperties.getInstance().getProperties().get(TransferProperties.EXECUTE_COUNT);
        if(value!=null){
            THREAD_COUNT=(Integer)value;
            logger.info("get init thread count == "+value);
        }
        if(executeValue!=null){
            EXECUTE_COUNT=(Integer)executeValue;
            logger.info("get init execute thread count == "+executeValue);
        }

        jobExecutor=Executors.newFixedThreadPool(THREAD_COUNT);
        subJobExecutor=Executors.newFixedThreadPool(EXECUTE_COUNT);
        semaphore=new Semaphore(EXECUTE_COUNT);
    }

    /**
     * 执行迁移任务
     * @param executeSqlJob
     * @param transferExchangeJob
     * @param replaceSql
     * @return
     * @throws Exception
     */
    private String handlerExecuteSqlJob(ExecuteSqlJob executeSqlJob,TransferExchangeJob transferExchangeJob,String replaceSql) throws Exception{

        while(executeSqlJob.getMaxId()>=executeSqlJob.getMinId()){
            replaceSql=SqlManager.exchangeSelectSql(executeSqlJob,BATCH_COUNT,replaceSql);
            SqlManager.executeSelectSql(transferExchangeJob,executeSqlJob);
            SqlManager.generateInsertSql(transferExchangeJob,executeSqlJob);
            SqlManager.executeInsertSql(transferExchangeJob,executeSqlJob);
            if(transferExchangeJob.getTransferMode() == TransferMode.DELETE){
                SqlManager.executeDeleteSql(transferExchangeJob,executeSqlJob);
            }
            SqlManager.resetMaxId(executeSqlJob,transferExchangeJob);

        }
        return replaceSql;
    }

    public static void main(String[] args) {
        System.out.println(Double.parseDouble(String.valueOf("0.0")));
    }

}

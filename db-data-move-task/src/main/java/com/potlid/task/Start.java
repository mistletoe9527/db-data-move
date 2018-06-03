package com.potlid.task;

import com.potlid.common.define.Transfer;
import com.potlid.manager.DataSourceManager;
import com.potlid.scanner.ClassScanner;
import com.potlid.task.executor.TransferJobExecutor;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 * Created by styb on 2018/4/14.
 */
public class Start extends Thread{

    private static final Logger logger=Logger.getLogger(Start.class);
    private ApplicationContext applicationContext;
    private Start(){}
    private static Start instance=new Start();
    public static Start getInstance(){
        return instance;
    }

    public void restart(){
        try {
            //扫描注解
            ClassScanner.getScanner().scan(applicationContext, Transfer.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void shutDown(){
        TransferJobExecutor.executor.shutDown();
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Start setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    public void run() {
        try {
            //加载数据源
            DataSourceManager.loadDataSource(applicationContext);
            TransferJobExecutor.executor.start();
            //扫描注解
            ClassScanner.getScanner().scan(applicationContext, Transfer.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

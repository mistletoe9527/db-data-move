package com.potlid.task;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by styb on 2018/5/22.
 */
public class DbDataTransferStart implements ApplicationContextAware,InitializingBean{
    private ApplicationContext applicationContext;
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public void afterPropertiesSet() throws Exception {
        Start.getInstance().setApplicationContext(applicationContext).start();
    }
}

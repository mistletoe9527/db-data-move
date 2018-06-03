package com.potlid.task.listener;
import com.potlid.task.Start;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * Created by styb on 2018/3/31.
 */
public class DataTransferListener implements ServletContextListener {


    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Start.getInstance().setApplicationContext(WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext(), WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).start();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Start.shutDown();
    }
}

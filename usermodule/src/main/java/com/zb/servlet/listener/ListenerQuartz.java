package com.zb.servlet.listener;

import com.zb.quartz.job.AddCoupon2RandMJob;
import com.zb.quartz.job.InsertCouponJob;
import com.zb.quartz.job.QuartzObj;
import com.zb.quartz.schedule.QuartzManager;
import com.zb.util.database.redis.PubExpireKey;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.ResourceBundle;

//@WebListener()
public class ListenerQuartz implements ServletContextListener,
        HttpSessionListener, HttpSessionAttributeListener {

    // Public constructor is required by servlet spec
    public ListenerQuartz() {
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("quartz task is running....");
        ResourceBundle config = ResourceBundle.getBundle("config/quartzcron");
        String insertCoupon = config.getString("insertcoupon.cron");
        String addMysqlRedis = config.getString("addmysqlredis.cron");
        QuartzObj quartzObj1 = new InsertCouponJob();
        QuartzObj quartzObj2 = new AddCoupon2RandMJob();
        QuartzManager.run(quartzObj1, "1", insertCoupon);
        QuartzManager.run(quartzObj2, "2", addMysqlRedis);
        new Thread(new PubExpireKey()).start();
    }

    public void contextDestroyed(ServletContextEvent sce) {
      /* This method is invoked when the Servlet Context 
         (the Web application) is undeployed or 
         Application Server shuts down.
      */
    }

    // -------------------------------------------------------
    // HttpSessionListener implementation
    // -------------------------------------------------------
    public void sessionCreated(HttpSessionEvent se) {
        /* Session is created. */
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        /* Session is destroyed. */
    }

    // -------------------------------------------------------
    // HttpSessionAttributeListener implementation
    // -------------------------------------------------------

    public void attributeAdded(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute 
         is added to a session.
      */
    }

    public void attributeRemoved(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is removed from a session.
      */
    }

    public void attributeReplaced(HttpSessionBindingEvent sbe) {
      /* This method is invoked when an attribute
         is replaced in a session.
      */
    }
}

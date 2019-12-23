package com.zb.quartz.schedule;

import com.zb.quartz.job.JobTest;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


public class ScheduleTest {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        // 创建调度器Scheduler
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        // 由工厂来创建调度器
        Scheduler scheduler = schedulerFactory.getScheduler();
        // 创建JobDetail实例，与JobTest
        JobDetail jobDetail = JobBuilder.newJob(JobTest.class).withIdentity("job1", "group1").build();
        // 构建Trigger实例，每隔1s执行一次
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "triggerGroup1")
                .startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1)
                        .repeatForever()).build();
        // 执行
        scheduler.scheduleJob(jobDetail, trigger);
        System.out.println("------------scheduler start!------------");
        scheduler.start();
        Thread.currentThread().sleep(5000);
        scheduler.shutdown();
        System.out.println("------------scheduler shutdown----------");
    }
}

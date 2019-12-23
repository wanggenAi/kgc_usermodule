package com.zb.quartz.schedule;

import com.zb.quartz.job.AddCoupon2RandMJob;
import com.zb.quartz.job.QuartzObj;
import com.zb.quartz.job.InsertCouponJob;
import com.zb.util.database.redis.PubExpireKey;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzManager {

    public static void run(QuartzObj obj, String code, String cron) {
        SchedulerFactory sf = new StdSchedulerFactory();
        try {
            Scheduler scheduler = sf.getScheduler();
            JobDetail jobDetail = JobBuilder.newJob(obj.getClass()).withIdentity("job" + code, "group" + code).build();
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(new TriggerKey("trigger" + code, "group" + code))
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        QuartzObj quartzObj1 = new InsertCouponJob();
        QuartzObj quartzObj2 = new AddCoupon2RandMJob();
        QuartzManager.run(quartzObj1, "1", "10 * * * * ?");
        QuartzManager.run(quartzObj2, "2", "13 * * * * ?");
        new Thread(new PubExpireKey()).start();
    }
}

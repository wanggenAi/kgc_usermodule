package com.zb.quartz.job;

import com.zb.service.imp.CouponServiceImp;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AddCoupon2RandMJob extends QuartzObj {
    private CouponServiceImp couponService = new CouponServiceImp();
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        couponService.addCouToRM();
    }
}

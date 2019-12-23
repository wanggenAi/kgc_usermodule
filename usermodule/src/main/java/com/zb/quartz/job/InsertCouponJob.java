package com.zb.quartz.job;

import com.zb.service.imp.CouponServiceImp;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.servlet.http.HttpServletRequest;

public class InsertCouponJob extends QuartzObj {
    private CouponServiceImp couponService = new CouponServiceImp();
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 先修改优惠券表的最新状态为0
        couponService.updateIsNew();
        // 插入coupon
        couponService.insertCoupon();
    }
}

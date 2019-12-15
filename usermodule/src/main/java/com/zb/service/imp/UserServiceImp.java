package com.zb.service.imp;

import com.zb.dao.imp.KgcUserDaoImp;
import com.zb.dao.inter.KgcUserDao;
import com.zb.service.inter.UserService;

public class UserServiceImp implements UserService {
    KgcUserDao kud = new KgcUserDaoImp();
    
}

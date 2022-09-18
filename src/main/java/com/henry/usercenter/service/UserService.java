package com.henry.usercenter.service;

import com.henry.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;

/**
* @author lenovo
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2022-09-01 16:55:53
*/
public interface UserService extends IService<User> {



    /**
     * 用户注释
     * @param userAccount 账户
     * @param userPassword 密码
     * @param checkPassword 校验密码
     * @Param planetCode 星球编号
     * @return 新用户id
     */
    long uesrRegister(String userAccount  , String userPassword , String checkPassword,String planetCode);


    /**
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount , String userPassword , HttpServletRequest request);

    User getSafetyUser(User originUser);

    /**
     * 请求用户注销
     * @param request
     * @return
     */

    int userLogout(HttpServletRequest request);
}

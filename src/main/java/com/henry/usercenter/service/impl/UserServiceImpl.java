package com.henry.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.henry.usercenter.common.ErrorCode;
import com.henry.usercenter.exception.BusinessException;
import com.henry.usercenter.model.domain.User;
import com.henry.usercenter.service.UserService;
import com.henry.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.henry.usercenter.constant.userConstant.USER_LOGIN_STATE;

/**
* @author lenovo
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2022-09-01 16:55:53
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private  UserMapper userMapper;

    /**
     * 盐值：混淆密码
     */
     private static final  String SALT  = "yupi";




    @Override
    public long uesrRegister(String userAccount, String userPassword, String checkPassword , String planetCode) {

        if(StringUtils.isAnyBlank(userAccount , userPassword , checkPassword , planetCode)){
            //todo  修改自定义异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }

        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账户过短");
        }

        if(userPassword.length() < 8 || checkPassword.length() < 8 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }

        if(planetCode.length() > 5 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号过长" );
        }

        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            return  -1;
        }

        if(!userPassword.equals(checkPassword)){
            return -1;
        }

        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount" , userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户重复");
        }

        //星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode" , planetCode);
         count = userMapper.selectCount(queryWrapper);
        if(count > 0 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编号重复");
        }

        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());


        //插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if(!saveResult){
            return -1;
        }
        return  user.getId();



    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if(StringUtils.isAnyBlank(userAccount , userPassword )){
            return  null;
        }

        if(userAccount.length() < 4){
            return  null;
        }

        if(userPassword.length() < 8  ){
            return null;
        }



        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            return  null;
        }


        //加密

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount" , userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if(user == null){
            log.info("user failed , userAccount cannot match userPassword");
            return null;
        }


        //用户脱敏
        User safetyUser = getSafetyUser(user);
        //3.记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE ,safetyUser);




        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if(originUser == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;

    }


    /**
     * 用户注销
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {


        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;

    }


}





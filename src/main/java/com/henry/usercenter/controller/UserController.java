package com.henry.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.henry.usercenter.common.BaseResponse;
import com.henry.usercenter.common.ErrorCode;
import com.henry.usercenter.common.ResultUtils;
import com.henry.usercenter.exception.BusinessException;
import com.henry.usercenter.model.domain.User;
import com.henry.usercenter.model.domain.request.UserLoginRequest;
import com.henry.usercenter.model.domain.request.UserRegisterRequest;
import com.henry.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.henry.usercenter.constant.userConstant.ADMIN_ROLE;
import static com.henry.usercenter.constant.userConstant.USER_LOGIN_STATE;

/**
 * FileName:     UserController
 * CreateBy:     IntelliJ IDEA
 * Author:       wei
 * Date:         2022-09-02
 * Description :
 */


/**
 * 用户接口
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister (@RequestBody UserRegisterRequest userRegisterRequest){

        if(userRegisterRequest == null){
      //      return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return null;
        }

        long result= userService.uesrRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);


    }




    @PostMapping("/login")
    public  BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest , HttpServletRequest request){

        if(userLoginRequest == null){
            return null;
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }

        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/loginout")
    public BaseResponse<Integer> userLoginout( HttpServletRequest request){

        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        int result = userService.userLogout(request);

        return ResultUtils.success(result);
    }


    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);

        User currentUser = (User) userObj;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = currentUser.getId();
        //todo 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }



    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username , HttpServletRequest request){
        //鉴权
        if(!isAdmin(request)){
          throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper <User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List <User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> {
            user.setUserPassword(null);
            return userService.getSafetyUser(user);
        }).collect(Collectors.toList());


        return  ResultUtils.success(list);


    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request){

        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTO);
        }
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean b = userService.removeById(id);
        return ResultUtils.success(b);


    }



    private boolean isAdmin( HttpServletRequest request){


        //管理员查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if(user == null || user.getUserRole()  != ADMIN_ROLE){
            return false;
        }
        return true;
    }



}

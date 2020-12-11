package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UserVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author allycoding
 * @Date: 2020/6/21 1:22
 */
@RestController
@Api(value = "用户登录的接口",tags = {"登录的controller"})
public class LoginController extends BasicController{

    @Autowired
    private UserService userService;

    /**
     * 设置Token
     * @param users
     * @return
     */
    public UserVO setUserRedisSessionToken(Users users){
        String uniqueToken = UUID.randomUUID().toString();//设置Token
        redis.set(USER_REDIS_SESSION + ":" + users.getId(), uniqueToken, 1000 * 60 * 30);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(users,userVO);
        userVO.setUserToken(uniqueToken);
        return userVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @ApiOperation(value = "用户登录", notes = "用户登录的接口")
    @PostMapping("/login")
    public IMoocJSONResult login(@RequestBody Users user) throws Exception{
        String username = user.getUsername();
        String password = user.getPassword();

        //1.判断用户名和密码必须不能为空
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            return IMoocJSONResult.errorMsg("用户名或密码不能为空...");
        }

        //2.判断用户是否存在
        Users users = userService.queryUserForLogin(username, MD5Utils.getMD5Str(user.getPassword()));

        //3.返回goLoginBtn
        if(users != null){
            users.setPassword("");
            UserVO userVO = setUserRedisSessionToken(users);
            return IMoocJSONResult.ok(userVO);
        }else {
            return IMoocJSONResult.errorMsg("用户或密码不正确，请重试...");
        }
    }

    @ApiOperation(value="用户注销", notes = "用户注销的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType ="query" )
    @PostMapping("/logout")
    public IMoocJSONResult logout(String userId) throws Exception{
        redis.del(USER_REDIS_SESSION + ":" + userId);
        return IMoocJSONResult.ok();
    }


}

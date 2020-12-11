package com.imooc.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;

/**
 * @author allycoding
 * @Date: 2020/6/13 16:20
 */
public interface UserService {
    /**
     * 判断用户名是否存在
     * @return
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 保存用户
     * @param user
     */
    public void saveUser(Users user);

    /**
     * 用户登录，根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    public Users queryUserForLogin(String username, String password);

    /**
     * 用户修改信息
     * @param users
     */
    public void updateUserInfo(Users users);

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);

    /**
     * 关注用户
     * @param userId
     * @param fanId
     */
    public void saveUserFanRelation(String userId, String fanId);

    /**
     * 查询用户和视频发布者之间是否关注过
     * @param userId
     * @param fanId
     * @return
     */
    public boolean selectUserIsFollow(String userId, String fanId);

    /**
     * 删除视频发布者和用户关注的关系
     * @param userId
     * @param fanId
     */
    public void deleteUserFanRelation(String userId, String fanId);

    /**
     * @Description: 举报用户
     * @param usersReport
     */
    public void reportUser(UsersReport usersReport);

}

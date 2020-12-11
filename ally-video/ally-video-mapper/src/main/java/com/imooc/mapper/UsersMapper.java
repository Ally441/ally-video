package com.imooc.mapper;

import com.imooc.pojo.Users;
import com.imooc.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {


    /**
     * 增加关注数量
     * @param userId
     * @Description: userId = fanId
     */
    public void addFollowerCount(String userId);

    /**
     * 增加粉丝数量
     * @param userId
     * @Description: userId = userId
     */
    public void addFansCount(String userId);

    /**
     * 减少关注数量
     * @param userId
     * @Description: userId = fanId
     */
    public void reduceFollowerCount(String userId);

    /**
     * 减少粉丝数量
     * @param userId
     * @Description: userId = userId
     */
    public void reduceFansCount(String userId);

    /**
     * 增加视频发布者点赞数量
     * @param userId
     * @Description: userId = publisherId
     */
    public void addReceiveLikeCount(String userId);

    /**
     * 减少视频发布者点赞数量
     * @param userId
     * @Description: userId = publisherId
     */
    public void reduceReceiveLikeCount(String userId);
}
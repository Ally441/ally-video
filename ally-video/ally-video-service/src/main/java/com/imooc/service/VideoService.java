package com.imooc.service;

import com.imooc.pojo.Comments;
import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideoVO;
import com.imooc.utils.PagedResult;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * @author allycoding
 * @Date: 2020/7/30 22:36
 */
public interface VideoService {

    /**
     * 保存视频
     * @param videos
     */
    public String saveVideo(Videos videos);

    /**
     * 修改视频的封面
     * @param videoId
     * @param coverPath
     * @return
     */
    public void updateVideo(String videoId, String coverPath);

    /**
     * @Desciption: 查询视频列表
     * @return
     */
    public PagedResult getAllVideos(Videos videos, Integer isSaveRecord, Integer page, Integer pageSize);

    /**
     * @Description: 查询喜欢视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

    /**
     * @Description: 查询关注的用户发布的视频
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);


    /**
     * 测试
     * @param page
     * @return
     */
    public PagedResult getAll(Integer page, Integer pageSize);

    /**
     * @Description: 获取热搜词列表
     * @return
     */
    public List<String> getHotWords();

    /**
     * @Description: 根据id查询视频信息
     * @param videoId
     * @return
     */
    public VideoVO queryVideo(String videoId);

    /**
     * @Description: 点赞视频
     * @param userId
     * @param videoId
     * @param publisherId
     */
    public void userLikeVideo(String userId,  String videoId, String publisherId);

    /**
     * @Description:取消点赞视频
     * @param userId
     * @param videoId
     * @param publisherId
     */
    public void userUnLikeVideo(String userId, String videoId, String publisherId);

    /**
     * @Description: 判断是否点赞视频
     * @param userId
     * @param videoId
     * @return
     */
    public boolean isUserLikeVideo(String userId, String videoId);

    /**
     * @Description: 保存留言
     * @param comments
     */
    public void saveComment(Comments comments);

    /**
     * @Description: 获取所有留言并分页
     * @return
     */
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
}

package com.imooc.mapper;

import com.imooc.pojo.vo.VideoVO;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author allycoding
 * @Date: 2020/8/8 21:33
 */
public interface VideoVoMapper extends MyMapper<VideoVO> {

    /**
     * 测试
     * @return
     */
    public List<VideoVO> queryAll();

    /**
     * 通过搜索查询视频和首页展示
     * @param videoDesc
     * @return
     */
    public List<VideoVO> queryAllVideos(@Param("videoDesc") String videoDesc, @Param("userId") String userId);

    /**
     * 查询
     * @param userId
     * @return
     */
    public List<VideoVO> queryMyLikeVideos(@Param("userId") String userId);

    /**
     * 查询关注的用户发布的视频
     * @param userId
     * @return
     */
    public List<VideoVO> queryMyFollowVideos(@Param("userId") String userId);

    /**
     * 查询视频
     * @param videoId
     * @return
     */
    public VideoVO queryVideo(String videoId);

    /**
     * 视频点赞，数量累加
     * @param videoId
     */
    public void addVideoLikeCount(String videoId);

    /**
     * 视频取消点赞，数量累减
     * @param videoId
     */
    public void reduceVideoLikeCount(String videoId);
}

package com.imooc.pojo.vo;

import com.imooc.pojo.Users;


/**
 * @author allycoding
 * @Description:代表用户是否点赞视频和收藏视频
 * @Date: 2020/8/26 16:46
 */
public class UserVideoVo {

    //视频信息
    private VideoVO videoVO;

    //用户是否点赞该视频
    private boolean userLikeVideo;

    //用户是否收藏该视频
    private boolean isKeep;


    public VideoVO getVideoVO() {
        return videoVO;
    }

    public void setVideoVO(VideoVO videoVO) {
        this.videoVO = videoVO;
    }

    public boolean isUserLikeVideo() {
        return userLikeVideo;
    }

    public void setUserLikeVideo(boolean userLikeVideo) {
        this.userLikeVideo = userLikeVideo;
    }

    public boolean isKeep() {
        return isKeep;
    }

    public void setKeep(boolean keep) {
        isKeep = keep;
    }
}

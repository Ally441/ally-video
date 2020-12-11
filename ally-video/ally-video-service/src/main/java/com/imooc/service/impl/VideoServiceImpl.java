package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.*;
import com.imooc.pojo.Comments;
import com.imooc.pojo.SearchRecords;
import com.imooc.pojo.UsersLikeVideos;
import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.pojo.vo.VideoVO;
import com.imooc.service.VideoService;
import com.imooc.utils.PagedResult;
import com.imooc.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.*;

import java.util.Date;
import java.util.List;

/**
 * @author allycoding
 * @Date: 2020/7/30 22:39
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideoVoMapper videoVoMapper;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsVoMapper commentsVoMapper;



    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos videos) {
        String id = sid.nextShort();
        videos.setId(id);
        videosMapper.insertSelective(videos);
        return id;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoId, String coverPath) {
        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(videos);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideos(Videos videos, Integer isSaveRecord, Integer page, Integer pageSize) {

        //保存热搜词
        String desc = videos.getVideoDesc();
        String userId = videos.getUserId();

        if(isSaveRecord != null && isSaveRecord == 1){
            SearchRecords searchRecords = new SearchRecords();
            searchRecords.setContent(desc);
            searchRecords.setId(sid.nextShort());
            searchRecordsMapper.insert(searchRecords);
        }
        //pageSize:每一页显示多少条数据
        //page:当前页数
        PageHelper.startPage(page,pageSize);
        List<VideoVO> list = videoVoMapper.queryAllVideos(desc,userId);
//        List<VideoVO> list = videoVoMapper.queryAllVideos();
        PageInfo<VideoVO> pageList = new PageInfo<>(list);
        PagedResult pagedResult = new PagedResult();
        // 当前页数
        pagedResult.setPage(page);
        // 总页数
        pagedResult.setTotal(pageList.getPages());
        // 每行显示的内容
        pagedResult.setRow(list);
        // 总记录数
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<VideoVO> list = videoVoMapper.queryMyLikeVideos(userId);
        PageInfo<VideoVO> list1 = new PageInfo<>(list);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(list1.getPages());
        pagedResult.setRow(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(list1.getTotal());
        return pagedResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<VideoVO> list = videoVoMapper.queryMyFollowVideos(userId);
        PageInfo<VideoVO> list1 = new PageInfo<>(list);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(list1.getPages());
        pagedResult.setPage(page);
        pagedResult.setRow(list);
        pagedResult.setRecords(list1.getTotal());
        return pagedResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAll(Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<VideoVO> list = videoVoMapper.queryAll();
        PageInfo<VideoVO> pageList = new PageInfo<>(list);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRow(list);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotWords();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public VideoVO queryVideo(String videoId) {
        return videoVoMapper.queryVideo(videoId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String publisherId) {
        //1.保存用户点赞视频信息
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();;
        usersLikeVideos.setId(sid.nextShort());
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideosMapper.insert(usersLikeVideos);
        //2.视频喜欢数量累加
        videoVoMapper.addVideoLikeCount(videoId);
        //3.视频发布者点赞数量累加
        usersMapper.addReceiveLikeCount(publisherId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnLikeVideo(String userId, String videoId, String publisherId){
        //1.删除用户点赞视频信息
        Example example = new Example(UsersLikeVideos.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        usersLikeVideosMapper.deleteByExample(example);
        //2.视频点赞数量累减
        videoVoMapper.reduceVideoLikeCount(videoId);
        //3.用户受喜欢数量的累减
        usersMapper.reduceReceiveLikeCount(publisherId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {
        //查询用户是否点赞该视频
        Example example = new Example(UsersLikeVideos.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);
        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);
        if(list != null && list.size() > 0){
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(Comments comments){
        String id = sid.nextShort();
        comments.setId(id);
        comments.setCreateTime(new Date());
        commentsMapper.insert(comments);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<CommentsVO> list = commentsVoMapper.queryComments(videoId);
        for (CommentsVO commentsVO : list){
            String timeAgo = TimeAgoUtils.format(commentsVO.getCreateTime());
            commentsVO.setTimeAgoStr(timeAgo);
        }
        PageInfo<CommentsVO> pageList = new PageInfo<>(list);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRow(list);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }
}

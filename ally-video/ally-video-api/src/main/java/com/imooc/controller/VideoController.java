package com.imooc.controller;

import com.imooc.pojo.*;
import com.imooc.pojo.vo.UserVideoVo;
import com.imooc.pojo.vo.VideoVO;
import com.imooc.service.BgmService;
import com.imooc.service.UserService;
import com.imooc.service.VideoService;
import com.imooc.utils.*;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author allycoding
 * @Date: 2020/7/7 13:39
 */
@RestController
@Api(value = "视频相关业务的接口", tags = {"视频相关业务的controller"})
@RequestMapping("/video")
public class VideoController extends BasicController{


    @Autowired
    private VideoService videoService;

    @Autowired
    private BgmService bgmService;


//
//    @Autowired
//    private UserService userService;

    @ApiOperation(value = "上传视频", notes = "上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name ="videoSeconds", value = "背景音乐播放长度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    public IMoocJSONResult upload(String userId, String bgmId, double videoSeconds, int videoWidth, int videoHeight, String desc,
                                  @ApiParam(value = "短视频", required = true) MultipartFile file) throws Exception{

        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }
        //文件保存的命名空间
        String fileSpace = "D:/ally-video";
        //保存到数据库中的相对路径
        String uploadPathDB = "/user/" + userId + "/video";
        String coverPathDB = "/user/" + userId + "/video";
        //文件上传的最终保存路径
        String fileVideoPath = null;
        //文件名
        String fileNamePrefix = null;
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try{
            if(file != null) {
                String fileName = file.getOriginalFilename();
                fileNamePrefix = UUID.randomUUID().toString();
                if(StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    fileVideoPath = fileSpace + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    //视频封面相对路径（保存到数据库里面）
                    coverPathDB += ("/" + fileNamePrefix + ".jpg");
                    File outfile = new File(fileVideoPath);
                    if(outfile.getParentFile() != null || !outfile.getParentFile().isDirectory()){
                        //创建父文件夹
                        outfile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outfile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                System.out.println("Hello world");
                return IMoocJSONResult.errorMsg("上传出错...");
            }
        }catch(Exception e){
           e.printStackTrace();
           return IMoocJSONResult.errorMsg("上传错误...");
        }finally {
            if(fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        //对视频和bgm进行合并
        MergeVideoBgm mergeVideoBgm = new MergeVideoBgm(FFMPEG_EXE);
        if(StringUtils.isNotBlank(bgmId)){
            //查询出Bgm
            Bgm bgm = bgmService.queryBgmById(bgmId);
            //查询背景音乐所在虚拟目录位置
            String bgmPath = FILE_SPACE + bgm.getPath();
            //对视频进行清音轨
            String videoClearVoice = mergeVideoBgm.clearCommand(fileVideoPath);
            //bgm和清音轨的视频合并
            mergeVideoBgm.convertor(videoClearVoice,bgmPath,videoSeconds,fileVideoPath);
            //删除清音轨的视频
            File deleteFile = new File(videoClearVoice);
            deleteFile.delete();
        }
        //对视频进行截图
        FetchVideoCover videoCover = new FetchVideoCover(FFMPEG_EXE);
        videoCover.getCover(fileVideoPath,FILE_SPACE + coverPathDB);
        //保存视频信息到数据库
        Videos videos = new Videos();
        videos.setUserId(userId);
        videos.setAudioId(bgmId);
        videos.setVideoSeconds((float)videoSeconds);
        videos.setVideoDesc(desc);
        videos.setCreateTime(new Date());
        videos.setCoverPath(coverPathDB);
        videos.setVideoHeight(videoHeight);
        videos.setVideoWidth(videoWidth);
        videos.setVideoPath(uploadPathDB);
        videos.setStatus(VideoStatusEnum.SUCCESS.value);
        String videoId = videoService.saveVideo(videos);

        return IMoocJSONResult.ok(videoId);
    }

    /**
     * 如果不使用FFmpeg截图，可以调用该api接口
     * @param userId
     * @param videoId
     * @param file
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "上传视频封面", notes = "上传视频封面的接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
        @ApiImplicitParam(name = "videoId", value = "视频主键id", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
    public IMoocJSONResult uploadCover(String userId,String videoId,
                                       @ApiParam(value = "视频封面", required = true)
                                       MultipartFile file) throws Exception {
        if(StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("视频主键id和用户id不能为空...");
        }
        //保存到数据库中的相对路径
        String uploadPathDB = "/user/" + userId + "/video";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try{
            if(file != null) {
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    String finalCoverPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //设置数据库保存路径
                    uploadPathDB += ("/" + fileName);
                    File coverFile = new File(finalCoverPath);
                    if (coverFile.getParentFile() != null || coverFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        coverFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(coverFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                } else {
                    return IMoocJSONResult.errorMsg("上传出错,请重试");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传错误...");
        } finally {
            if(fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        videoService.updateVideo(videoId, uploadPathDB);
        return IMoocJSONResult.ok();
    }

    /**
     * 测试用
     * @param page
     * @return
     */
    @ApiOperation(value = "首页初始化", notes = "首页初始化接口")
    @ApiImplicitParam(name = "page", value = "页数", required = false, dataType = "Integer", paramType = "query")
    @PostMapping(value = "/show")
    public IMoocJSONResult show(Integer page){
        if(page == null){
            page = 1;
        }
        PagedResult pagedResult = videoService.getAll(page,PAGE_SIZE);
        return IMoocJSONResult.ok(pagedResult);
    }

    /**
     *
     * @param videos 传递过来的查询信息
     * @param isSaveRecord 是否保存搜索 1需要保存 0不需要保存
     * @param page
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "通过输入搜索词查询视频列表", notes = "通过输入搜索词查询视频列表接口")
    @PostMapping(value = "/showAll")
    public IMoocJSONResult showAll(@RequestBody Videos videos, Integer isSaveRecord, Integer page, Integer pageSize) throws Exception{

        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = PAGE_SIZE;
        }
        PagedResult pagedResult = videoService.getAllVideos(videos,isSaveRecord,page,pageSize);
        return IMoocJSONResult.ok(pagedResult);
    }

    /**
     * @Description:用户收藏(点赞)过的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value="用户收藏(点赞)过的视频列表", notes = "用户收藏(点赞)过的视频列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", paramType = "body"),
            @ApiImplicitParam(name = "page", value = "视频页数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页视频数量", dataType = "Integer", paramType = "query")
    })
    @PostMapping("/showMyLike")
    public IMoocJSONResult showMyLike(String userId, Integer page, Integer pageSize){
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.ok();
        }
        if(page == null){
            page = 1;
        }
        if(pageSize ==null){
            pageSize = 6;
        }
        PagedResult pagedResult = videoService.queryMyLikeVideos(userId,page,pageSize);
        return IMoocJSONResult.ok(pagedResult);
    }

    /**
     * @Description: 登录用户关注的用户发布的视频
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "查询关注的用户发布的视频", notes = "查询关注的用户发布的视频接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "视频页数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页视频数量", dataType = "Integer", paramType = "query")
    })
    @PostMapping("/showMyFollow")
    public IMoocJSONResult showMyFollow(String userId, Integer page, Integer pageSize){
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.ok();
        }
        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = 6;
        }
        PagedResult pagedResult = videoService.queryMyFollowVideos(userId,page,pageSize);
        return IMoocJSONResult.ok(pagedResult);
    }

    /**
     * 查看别人信息，发现未登录，登录后返回该页面
     * @param videoId
     * @return
     */
    @ApiOperation(value = "查询视频信息", notes = "查询视频信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "查询视频id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/queryVideo")
    public IMoocJSONResult queryVideo(String videoId, String userId) {
        if(StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }
        VideoVO videoVO = videoService.queryVideo(videoId);
        UserVideoVo userVideoVo = new UserVideoVo();
        userVideoVo.setVideoVO(videoVO);
        boolean userLikeVideo = videoService.isUserLikeVideo(userId,videoId);
        userVideoVo.setUserLikeVideo(userLikeVideo);
        return IMoocJSONResult.ok(userVideoVo);
    }

    /**
     * 热搜词查询
     * @return
     */
    @ApiOperation(value = "热搜词搜索", notes = "搜索次数最多的词汇")
    @PostMapping(value = "/hot")
    public IMoocJSONResult hot(){
        return IMoocJSONResult.ok(videoService.getHotWords());
    }

    @ApiOperation(value = "视频点赞", notes = "视频点赞接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "登录用户id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoId", value = "视频id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "publisherId", value = "视频发布者id", dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/userLike")
    public IMoocJSONResult userLike(String userId, String videoId, String publisherId){
        videoService.userLikeVideo(userId, videoId, publisherId);
        return IMoocJSONResult.ok();
    }

    @ApiOperation(value = "视频取消点赞", notes = "视频取消点赞接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "登录用户id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "videoId", value = "视频id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "publisherId", value = "视频发布者id", dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/userUnLike")
    public IMoocJSONResult userUnLike(String userId, String videoId, String publisherId){
        videoService.userUnLikeVideo(userId,videoId,publisherId);
        return IMoocJSONResult.ok();
    }

    @ApiOperation(value = "视频评论", notes="视频评论接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "comments", value = "评论", dataType = "Comments", paramType = "query"),
            @ApiImplicitParam(name = "fatherCommentId", value = "被评论者id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "toUserId", value = "评论者id", dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/saveComment")
    public IMoocJSONResult saveComment(@RequestBody Comments comments, String fatherCommentId, String toUserId){
        comments.setFatherCommentId(fatherCommentId);
        comments.setToUserId(toUserId);
        videoService.saveComment(comments);
        return IMoocJSONResult.ok();
    }

    @ApiOperation(value = "获取视频的所有评论", notes = "获取视频的所有评论接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="videoId", value = "视频id", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "评论页数", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页评论数量", dataType = "Integer", paramType = "query")
    })
    @PostMapping(value = "/getVideoAllComments")
    public IMoocJSONResult getVideoAllComments(String videoId, Integer page, Integer pageSize){
        if(StringUtils.isBlank(videoId)){
            return IMoocJSONResult.ok();
        }
        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }

        PagedResult list = videoService.getAllComments(videoId,page,pageSize);

        return IMoocJSONResult.ok(list);
    }
}

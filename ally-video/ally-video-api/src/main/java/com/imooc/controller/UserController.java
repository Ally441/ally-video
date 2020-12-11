package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;
import com.imooc.pojo.vo.UserVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author allycoding
 * @Date: 2020/6/29 15:39
 */
@RestController
@Api(value = "用户相关业务的接口", tags = {"用户相关业务的controller"})
@RequestMapping("/user")
public class UserController extends BasicController{

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    @PostMapping("/uploadFace")
    public IMoocJSONResult uploadFile(String userId, @RequestParam("file") MultipartFile[] files) throws Exception{

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }

        //文件保存的命名空间
        String fileSpace = "D:/ally-video";
        //保存到数据库中的相对路径
        String uploadPathDB = "/user/" + userId + "/face";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try{
            if(files != null && files.length > 0) {
                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    String finalFacePath = fileSpace + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null && !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                return IMoocJSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e){
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错");
        } finally {
            if(fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        Users user = new Users();
        user.setId(userId);
        user.setFaceImage(uploadPathDB);
        userService.updateUserInfo(user);
        return IMoocJSONResult.ok(uploadPathDB);
    }

    @ApiOperation(value = "查询用户信息", notes = "查询用户信息的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "fanId", value = "粉丝id", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/query")
    public IMoocJSONResult query(String userId, String fanId) throws Exception{
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户Id不能为空");
        }
        Users users = userService.queryUserInfo(userId);
        UserVO userVO = new UserVO();
        //查询用户和视频发布者是否关注
        boolean isFollow = userService.selectUserIsFollow(userId,fanId);
        userVO.setFollow(isFollow);
        BeanUtils.copyProperties(users, userVO);
        return IMoocJSONResult.ok(userVO);
    }

    /**
     * 添加关注，粉丝数增加
     * @param userId
     * @param fanId
     * @return
     */
    @ApiOperation(value = "添加关注，并增加粉丝",notes = "用户关注")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query" ),
            @ApiImplicitParam(name = "fanId", value = "粉丝id", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/userAttention")
    public IMoocJSONResult userAttention(String userId, String fanId){
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }
        userService.saveUserFanRelation(userId,fanId);
        return IMoocJSONResult.ok("关注成功...");
    }

    /**
     * 取消关注，删除粉丝
     * @param userId
     * @param fanId
     * @return
     */
    @ApiOperation(value = "取消关注，并减少粉丝", notes = "用户取消关注")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "fanId", value = "粉丝id", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/userUnsubscribe")
    public IMoocJSONResult userUnsubscribe(String userId, String fanId){
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }
        //取消关注，删除粉丝
        userService.deleteUserFanRelation(userId,fanId);
        return IMoocJSONResult.ok("取消关注成功...");
    }

    @ApiOperation(value = "举报", notes = "举报视频")
    @PostMapping("/reportUser")
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport){
        userService.reportUser(usersReport);
        return IMoocJSONResult.ok("举报成功...");
    }



}

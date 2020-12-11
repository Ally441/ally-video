package com.imooc.controller;

import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author allycoding
 * @Date: 2020/6/26 0:44
 */
@RestController
public class BasicController {

    @Autowired
    public RedisOperator redis;

    //虚拟目录
    public static final String FILE_SPACE = "D:/ally-video";

    //FFMpeg程序的路径
    public static final String FFMPEG_EXE = "E:\\ffmpeg-4.3\\bin\\ffmpeg.exe";

    public static final String USER_REDIS_SESSION = "user-redis-session";

    public static final Integer PAGE_SIZE = 6;
}

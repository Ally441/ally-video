package com.imooc.controller;

import com.imooc.pojo.Bgm;
import com.imooc.service.BgmService;
import com.imooc.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author allycoding
 * @Date: 2020/7/5 20:39
 */
@RestController
@Api(value = "背景音乐业务的接口", tags = {"背景音乐业务的controller"})
@RequestMapping("/bgm")
public class BgmController {

    @Autowired
    private BgmService bgmService;

    @ApiOperation(value = "获取背景音乐列表", notes = "获取背景音乐列表的接口")
    @PostMapping("/list")
    public IMoocJSONResult list(){
        return IMoocJSONResult.ok(bgmService.queryBgmList());
    }

    @ApiOperation(value = "查询背景音乐信息", notes = "查询背景音乐信息的接口")
    @ApiImplicitParam(name = "id", value = "背景音乐Id", dataType = "String", paramType = "query")
    @PostMapping("/queryBgm")
    public IMoocJSONResult queryBgm(String id){
        Bgm bgm = bgmService.queryBgmById(id);
        return IMoocJSONResult.ok(bgm);
    }

}

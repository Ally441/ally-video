package com.imooc.service;

import com.imooc.pojo.Bgm;

import java.util.List;

/**
 * @author allycoding
 * @Date: 2020/7/5 17:48
 */
public interface BgmService {

    /**
     * @Description: 查询背景音乐列表
     */
    public List<Bgm> queryBgmList();

    /**
     *
     * @Desciption:根据Id查询bgm
     * @param bgmId
     * @return
     */
    public Bgm queryBgmById(String bgmId);
}

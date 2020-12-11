package com.imooc.mapper;

import com.imooc.pojo.Comments;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.utils.MyMapper;

import java.util.List;

/**
 * @author allycoding
 * @Date: 2020/10/14 21:49
 */
public interface CommentsVoMapper extends MyMapper<Comments> {

    public List<CommentsVO> queryComments(String videoId);
}

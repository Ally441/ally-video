package com.imooc.service.impl;

import com.imooc.mapper.UsersFansMapper;
import com.imooc.mapper.UsersMapper;
import com.imooc.mapper.UsersReportMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.UsersFans;
import com.imooc.pojo.UsersReport;
import com.imooc.service.UserService;

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
 * @Date: 2020/6/13 16:36
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users users = new Users();
        users.setUsername(username);
        Users result = usersMapper.selectOne(users);
        return result == null ?  false : true;
    }

    /*
        @Transactional(propagation=Propagation.REQUIRED) ：
        如果有事务, 那么加入事务, 没有的话新建一个(默认情况下)
        适合新增，修改，删除
    */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {
        String userId = sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users result = usersMapper.selectOneByExample(userExample);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserInfo(Users users) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", users.getId());
        usersMapper.updateByExampleSelective(users, userExample);
    }

    /*
        @Transactional(propagation=Propagation.SUPPORTS) ：
        如果其他bean调用这个方法,在其他bean中声明事务,那就用事务.如果其他bean没有声明事务,那就不用事务.
    */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id",userId);
        Users user = usersMapper.selectOneByExample(userExample);
        return user;
    }

    /**
     * 关注用户
     * @param userId
     * @param fanId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {
        //关注用户
        //1.添加
        UsersFans usersFans = new UsersFans();
        usersFans.setId(sid.nextShort());
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        usersFansMapper.insert(usersFans);

        //2.添加用户粉丝数和粉丝关注数
        usersMapper.addFollowerCount(fanId);
        usersMapper.addFansCount(userId);
    }

    /**
     * 查询用户和视频发布者之间关系
     * @param userId
     * @param fanId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean selectUserIsFollow(String userId, String fanId) {
        Example userLikeVideoExample = new Example(UsersFans.class);
        Criteria criteria = userLikeVideoExample.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        List<UsersFans> list = usersFansMapper.selectByExample(userLikeVideoExample);
        if(list != null && !list.isEmpty() && list.size() > 0){
            return true;
        }
        return false;
    }

    @Override
    public void deleteUserFanRelation(String userId, String fanId) {
        Example userLikeVideoExample = new Example(UsersFans.class);
        Criteria criteria = userLikeVideoExample.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        usersFansMapper.deleteByExample(userLikeVideoExample);
        //减少视频发布者的粉丝数和减少用户的关注数
        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollowerCount(fanId);
    }

    @Override
    public void reportUser(UsersReport usersReport) {
        String id = sid.nextShort();
        usersReport.setId(id);
        usersReport.setCreateDate(new Date());
        usersReportMapper.insert(usersReport);
    }
}

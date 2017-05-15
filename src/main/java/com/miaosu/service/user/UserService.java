package com.miaosu.service.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.miaosu.mapper.UserMapper;
import com.miaosu.model.User;

/**
 * User Service
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @SuppressWarnings("deprecation")
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    /**
     * 修改用户密码
     *
     * @param userName    用户名
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    public boolean changePassword(String userName, String oldPassword, String newPassword) {
        return userMapper.updatePassword(userName, passwordEncoder.encodePassword(oldPassword, null),
                passwordEncoder.encodePassword(newPassword, null)) ==1;
    }

    /**
     * 判断用户是否存在
     *
     * @param userName 用户名
     * @return 是否存在
     */
    public User userExists(String userName) {
        return userMapper.selectInfo(userName, null);
    }

    /**
     * 创建用户
     *@param user 用户信息
     */
    public void createUser(User user) {
//        插入用户信息
        userMapper.insertInfo(user);
//        TODO 权限信息
    }

    /**
     * 更新用户权限信息
     * @param user
     */
    public void updateAutoInfo(User user){
//TODO  这边暂时不知道怎么弄
    }

    public int delByNames(String[] userNames){
        return userMapper.delByNames(userNames);
    }


    /**
     * 禁用用户
     *
     * @param userNames 用户名数组
     */
    public void disable(String... userNames) {
            userMapper.updateAbled(userNames, false);
    }

    /**
     * 启用用户
     *
     * @param userNames 用户名数组
     */
    public void enable(String... userNames) {
        userMapper.updateAbled(userNames, true);
    }


    /**
     * 获取用户信息
     *
     * @param userName 用户名
     * @return 用户
     */
    public User get(String userName) {
        User user = null;
        try {
            user = userMapper.selectInfo(userName, null);
        } catch (Exception ex) {
            logger.info("Get user from cache failed. userName:{}, exMsg:{}", userName, ex.getMessage());
        }
        return user;
    }

    public User login(String userName ,String password){
        return userMapper.selectInfo(userName, passwordEncoder.encodePassword(password,null));
    }
    
    /**
     * 查询user列表
     * @return
     */
    public List<String> queryUsreList()
    {
    	return userMapper.selectUserList();
    }
}

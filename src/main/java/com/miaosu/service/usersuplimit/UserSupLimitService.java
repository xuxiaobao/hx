package com.miaosu.service.usersuplimit;

import com.miaosu.Page;
import com.miaosu.mapper.UserSupLimitMapper;
import com.miaosu.model.UserSupLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类的描述
 *
 * @author CaoQi
 * @Time 2016/1/10
 */
@Service
public class UserSupLimitService {

    @Autowired
    private UserSupLimitMapper userSupLimitMapper;

    public Page<UserSupLimit> findPage(Page page){
        page.setData(userSupLimitMapper.selectPage(page));
        return page;
    }

    public int create(UserSupLimit userSupLimit){
        return userSupLimitMapper.insertInfo(userSupLimit);
    }

    public int update(UserSupLimit userSupLimit){
        return userSupLimitMapper.updateLimit(userSupLimit);
    }
}

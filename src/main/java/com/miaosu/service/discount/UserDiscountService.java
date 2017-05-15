package com.miaosu.service.discount;

import com.miaosu.Page;
import com.miaosu.mapper.UserDiscountMapper;
import com.miaosu.model.UserDiscount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类的描述
 *
 * @author CaoQi
 * @Time 2016/1/10
 */
@Service
public class UserDiscountService {

    @Autowired
    private UserDiscountMapper userDiscountMapper;

    public Page findPage(Page page){
        page.setData(userDiscountMapper.selectPage(page));
        return page;
    }

    public int create(UserDiscount userDiscount){
        return userDiscountMapper.insertInfo(userDiscount);
    }

    public int update(UserDiscount userDiscount){
        return userDiscountMapper.updateInfo(userDiscount);
    }


}

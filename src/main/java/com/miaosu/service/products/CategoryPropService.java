package com.miaosu.service.products;

import com.miaosu.mapper.PropCategoryMapper;
import com.miaosu.mapper.PropValueMapper;
import com.miaosu.model.PropCategory;
import com.miaosu.model.PropValue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 类目 拥有的属性
 *
 * @author CaoQi
 * @Time 2016/1/7
 */
@Service
public class CategoryPropService {

    @Autowired
    private PropCategoryMapper propCategoryMapper;


    @Autowired
    private PropValueMapper propValueMapper;

    public List<PropCategory> findAllById(String categoryId){
//        获取所有属性
        List<PropCategory> propCategoryList =propCategoryMapper.selectInfoById(categoryId);

        List<String> ids = new ArrayList<>();
        for (PropCategory propCategory : propCategoryList){
            ids.add(propCategory.getPropId());
        }

        List<PropValue> propValueList = propValueMapper.selectByIds(ids);

//        组合数据
        for (PropCategory propCategory : propCategoryList){
            for (PropValue propValue: propValueList){
                if (StringUtils.equals(propCategory.getPropId(),propValue.getPropId())){
                    propCategory.getPropValueList().add(propValue);
                }
            }
        }

        return propCategoryList;
    }
}

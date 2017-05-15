package com.miaosu.service.menus;

import com.miaosu.mapper.MenuMapper;
import com.miaosu.model.Menu;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Menu业务层实现
 * Created by angus on 15/6/15.
 */
@Service
public class MenuService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MenuMapper menuMapper;

    public List<Menu> findAll() {
        return menuMapper.selectAll();
    }

    public int save(Menu menu) {
        return menuMapper.insert(menu);
    }

    public void remove(Long[] ids) {
        if (ArrayUtils.isNotEmpty(ids)){
            menuMapper.delByIds(ids);
        }
    }
}

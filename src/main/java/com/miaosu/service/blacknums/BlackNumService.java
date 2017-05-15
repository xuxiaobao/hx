package com.miaosu.service.blacknums;

import com.miaosu.Page;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ServiceException;
import com.miaosu.mapper.BlackNumMapper;
import com.miaosu.model.BlackNum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 黑名单号码Service
 * Created by angus on 15/10/2.
 */
@Service
public class BlackNumService {

    @Autowired
    private BlackNumMapper blackNumMapper;

    public Page<BlackNum> find(String text, Page page) {
        if (text == null) {
            text = "";
        }

        page.getPageParam().put("number",text);
        page.setData(blackNumMapper.selectByNumberLike(page));

        return page;
    }

    public BlackNum get(String id) {
        return blackNumMapper.selectById(id);
    }


    public void remove(String... ids) {
        blackNumMapper.delByIds(ids);
    }

    public BlackNum create(BlackNum blackNum) {
        String id = blackNum.getNumber();
        if (blackNumMapper.selectById(id)!=null) {
            throw new ServiceException(ResultCode.DATA_EXISTS);
        }

        blackNumMapper.insertInfo(blackNum);
        return blackNum;
    }

    public BlackNum update(BlackNum blackNum) {
        String id = blackNum.getNumber();
        if (blackNumMapper.selectById(id) == null) {
            throw new ServiceException(ResultCode.DATA_NOT_EXISTS);
        }

        blackNumMapper.updateInfo(blackNum);

        return blackNum;
    }

}

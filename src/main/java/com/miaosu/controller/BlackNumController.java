package com.miaosu.controller;

import com.miaosu.Page;
import com.miaosu.base.QueryResult;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.base.ServiceException;
import com.miaosu.service.blacknums.BlackNumService;
import com.miaosu.model.BlackNum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;

/**
 * 黑名单号码Controller
 */
@RestController
@RequestMapping("/api/blacknum")
public class BlackNumController {

    @Autowired
    private BlackNumService blackNumService;

    /**
     * 获取列表
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public QueryResult<List<BlackNum>> list(@RequestParam(value = "start", required = false) Integer start,
            @RequestParam(value = "limit", required = false) Integer size,
            @RequestParam(value = "text", required = false) String text) {

        int current = 0;
        if (start != null) {
            current = (start / size);
        }

        Page<BlackNum> blackNums = blackNumService.find(text, new Page(current, size));
        return new QueryResult<>(true ,(long)blackNums.getTotalCount(), blackNums.getData());
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo remove(@RequestParam("ids") String... ids) {
        blackNumService.remove(ids);
        return new ResultInfo(true ,ResultCode.SUCCESSFUL);
    }

    /**
     * 获取单个
     */
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public QueryResult<BlackNum> get(@PathVariable String id) {
        BlackNum product = blackNumService.get(id);
        if (product != null) {
            return new QueryResult<>(true ,1l, product);
        } else {
            throw new ServiceException(ResultCode.DATA_NOT_EXISTS);
        }
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResultInfo create(@Valid @RequestBody BlackNum blackNum) {
        try {
            blackNumService.create(blackNum);
        } catch (ConstraintViolationException e) {
            throw new ServiceException(ResultCode.DATA_CONSTRAINT_VIOLATION);
        }
        return new ResultInfo(true ,ResultCode.SUCCESSFUL);
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResultInfo update(@Valid @RequestBody BlackNum blackNum) {
        blackNumService.update(blackNum);
        return new ResultInfo(true ,ResultCode.SUCCESSFUL);
    }

}

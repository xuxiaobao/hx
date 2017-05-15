package com.miaosu.controller.openapi;

import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.base.ServiceException;
import com.miaosu.service.members.MemberService;
import com.miaosu.model.Member;
import com.miaosu.model.Product;
import com.miaosu.model.User;
import com.miaosu.service.products.ProductService;
import com.miaosu.service.user.UserService;
import com.miaosu.util.DESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品开放接口
 * Created by angus on 15/10/6.
 */
@RestController
@RequestMapping("/openapi/product")
public class OpenProductController extends OpenBaseController{

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResultInfo<List<Map<String, Object>>> create(@RequestParam(value = "userId") final String userId,
                                                  @RequestParam(value = "sign") final String sign) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);

        // Step.1 签名校验
        Member member = memberService.get(userId);
        User user = userService.get(userId);
        if (member == null || user == null || !user.isEnabled()) {
            // 用户不存在或被禁用
            throw new ServiceException(ResultCode.OPEN_USER_NOT_EXISTS);
        }
        checkSign("/openapi/product/list",paramMap, sign, DESUtil.decryptToString(member.getToken(), userId));

        List<Product> productList = productService.listAll();
        List<Map<String, Object>> data = new ArrayList<>(productList.size());
        for(Product product : productList) {
            if(product.isEnabled()) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", product.getId());
                record.put("name", product.getName());
                BigDecimal price = product.getPrice().multiply(member.getDiscount()).setScale(4, BigDecimal.ROUND_DOWN);
                record.put("price", price);
                record.put("applicableArea", product.getApplicableArea());

                data.add(record);
            }
        }

        return new ResultInfo<>(true, ResultCode.SUCCESSFUL, data);
    }
}

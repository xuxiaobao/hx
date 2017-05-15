package com.miaosu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 类的描述
 *
 * @author CaoQi
 * @Time 2015/12/27
 */
@Controller
public class PageController {

    public static final String LOGIN_KEY="loginUser";

    @RequestMapping(value = "/index")
    public String index(HttpServletRequest request){
        if (WebUtils.getSessionAttribute(request, LOGIN_KEY)==null){
            return "redirect:/login.jsp";
        }
        return "/index";
    }

    @RequestMapping(value = "/welcome")
    public String welcome(HttpServletRequest request){
        return "redirect:/welcome.jsp";
    }
}

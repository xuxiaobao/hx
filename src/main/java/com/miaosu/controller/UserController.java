package com.miaosu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import com.miaosu.base.BaseController;
import com.miaosu.base.ConstantsUtils;
import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.model.Member;
import com.miaosu.model.User;
import com.miaosu.service.members.MemberService;
import com.miaosu.service.user.UserService;

/**
 * User Controller
 */
@Controller
@RequestMapping("/api/system/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    private Logger logger= LoggerFactory.getLogger(getClass());

    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "/login")
    public String login(@RequestParam String username,@RequestParam String password,HttpServletRequest request,RedirectAttributes model){
        User user=userService.login(username, password);
        if (user!=null){
            if (StringUtils.equals(user.getUserName(),"admin")){
                user.setRole("ROLE_ADMIN");
            }

            if (StringUtils.equals(user.getUserName(),"root")){
                user.setRole("ROLE_SYS_ADMIN");
            }
            WebUtils.setSessionAttribute(request, ConstantsUtils.LOGIN_KEY,user);

            String ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
                ip = request.getRemoteAddr();
            }
            Member member = new Member();
            member.setUsername(username);
            member.setLastLoginIp(ip);
            member.setLastLoginTime(new Date());
            memberService.update(member);
            return "redirect:/index";
        }else{
            logger.warn("登陆失败");
            model.addAttribute("error","login_error");
            return "redirect:/login.jsp";
        }
    }

    @RequestMapping(value = "/logout")
    public String logout(HttpServletRequest request){
            WebUtils.setSessionAttribute(request,ConstantsUtils.LOGIN_KEY,null);
       return "redirect:/login.jsp";
    }

    @RequestMapping(value = "/changePwd", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    public ResultInfo changePwd(@RequestParam String oldPwd, @RequestParam String newPwd,HttpServletRequest request) {
        User user = (User) WebUtils.getSessionAttribute(request, ConstantsUtils.LOGIN_KEY);
        if (userService.changePassword(user.getUserName(), oldPwd, newPwd)) {
            return new ResultInfo(true,ResultCode.SUCCESSFUL);
        }else{
            return new ResultInfo(false, ResultCode.CHANGE_PWD_WITH_WORNG_OLD_PWD);
        }
    }

    @RequestMapping(value = "/enabled", method = { RequestMethod.POST, RequestMethod.GET })
    public @ResponseBody ResultInfo enabled(@RequestParam String userName, @RequestParam boolean enabled) {

        User user = new User();
        user.setUserName(userName);
        if(enabled){
            user.setEnabled(true);
            userService.enable(userName);
        }else{
            user.setEnabled(false);
            userService.disable(userName);
        }

        return new ResultInfo(true,ResultCode.SUCCESSFUL);
    }
    
    @RequestMapping(value = "/queryuserlist", method = { RequestMethod.POST, RequestMethod.GET })
    public @ResponseBody ResultInfo queryUserList()
    {
    	List<String> list = userService.queryUsreList();
    	Map<String,String> map = null;
    	List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
    	for(int i = 0; i < list.size(); i++)
    	{
    		map = new HashMap<String,String>();
    		map.put("id", list.get(i));
    		map.put("name", list.get(i));
    		mapList.add(map);
    	}
    	return new ResultInfo<>(true, ResultCode.SUCCESSFUL, mapList);
    }
}

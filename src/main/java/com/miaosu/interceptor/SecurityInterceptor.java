package com.miaosu.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import com.miaosu.annotation.Security;
import com.miaosu.model.User;
import com.miaosu.service.user.UserService;

/**
 * 安全拦截器
 *
 * @author caoqi
 *
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService employeeService;

    public static final String LOGIN_KEY="loginUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getServletPath().contains("login")){
            return true;
        }

        // 判断会话
        User user = (User)WebUtils.getSessionAttribute(request, LOGIN_KEY);
        if (user == null || user.isEnabled()==false) {
            response.sendRedirect("/login.jsp");
            return false;
        }
        // 控制访问
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            Security security = method.getMethodAnnotation(Security.class);
            if (security != null) {
                String url = security.url();

                logger.debug("url:{}", url);
                if (StringUtils.isNotEmpty(url)){
                    if (!StringUtils.equals(user.getUserName(),"admin")){
                        response.sendError(HttpStatus.FORBIDDEN.value());
                        return false;
                    }
                }
                // 获取session中用户的rights
//                List<String> rights = (List<String>) WebUtils.getSessionAttribute(request, "rights");
                // 如果用户拥有该权限
//                if (rights.contains(url)) {
//                    return true;
//                }
                // return 403 page

            }
        }
        return true;
    }

}

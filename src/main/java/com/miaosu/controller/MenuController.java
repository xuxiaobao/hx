package com.miaosu.controller;

import com.miaosu.base.ResultCode;
import com.miaosu.base.ResultInfo;
import com.miaosu.model.User;
import com.miaosu.service.menus.ExtMenu;
import com.miaosu.service.menus.MenuService;
import com.miaosu.model.Menu;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Menu控制层
 */
@Controller
@RequestMapping("/api/system/menu")
public class MenuController {

    private static Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private MenuService menuService;

    public static final String LOGIN_KEY="loginUser";

    @RequestMapping(value = "/list")
    public @ResponseBody List<Menu> findAllMenus() {
        return menuService.findAll();
    }

    @RequestMapping(value = "/tree")
    public @ResponseBody List<Menu> findMenusTree(HttpServletRequest request) {

        User user= (User) WebUtils.getSessionAttribute(request, LOGIN_KEY);

        List<Menu> menus = menuService.findAll();

        Iterator<Menu> iterator = menus.iterator();
        while (iterator.hasNext()){
            Menu menu = iterator.next();
            if (StringUtils.isNotEmpty(menu.getAuthorities())
                    && !StringUtils.contains(menu.getAuthorities(),user.getRole())){
                iterator.remove();
            }
        }

        List<Menu> menuList = new ArrayList<Menu>();

        for (Menu menu : menus) {

            if (menu.getParentId() == null) {
            // 判断用户是否拥有菜单权限
                ExtMenu root = new ExtMenu(menu);
                root.setIdPath(menu.getId());
                root.setTextPath(menu.getText());
                root.setSingleExpand(!menu.isLeaf());

                // 添加子节点
                addChild(request, root, menus);

                menuList.add(root);
            }
        }

        return menuList;
    }

    private boolean hasAuthority(HttpServletRequest request, Menu menu) {
        try {
            SecurityContextImpl securityContextImpl = (SecurityContextImpl) request.getSession().getAttribute(
                    "SPRING_SECURITY_CONTEXT");
            Collection<? extends GrantedAuthority> authorities = securityContextImpl.getAuthentication()
                    .getAuthorities();

            String[] menuAuthorities = menu.getAuthorities().split(",");

            // 如果菜单没有指定权限，则所有角色可以访问
            if (ArrayUtils.isEmpty(menuAuthorities)) {
                return true;
            }

            // 用户的权限列表只要包含菜单中的任何一个权限，都表示用户拥有此菜单的权限
            for (GrantedAuthority authority : authorities) {
                if (ArrayUtils.contains(menuAuthorities,authority.getAuthority())) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("判断用户菜单权限异常；menu：{}", menu, e);
        }

        return false;
    }

    private Menu addChild(HttpServletRequest request, ExtMenu parent, Iterable<Menu> menus) {
        if (parent != null && menus != null) {
            for (Menu menu : menus) {
                // 判断用户是否拥有菜单权限
//                if (hasAuthority(request, menu)) {
                    // 添加子节点
                    if (StringUtils.equals(menu.getParentId(), parent.getId())) {
                        ExtMenu extMenu = new ExtMenu(menu);
                        extMenu.setIdPath(parent.getIdPath() + "/" + menu.getId());
                        extMenu.setTextPath(parent.getTextPath() + "/" + menu.getText());
                        extMenu.setSingleExpand(!menu.isLeaf());

                        parent.addChildren(extMenu);

                        // 如果当前是个目录，递归添加子目录
                        if (!menu.isLeaf()) {
                            addChild(request, extMenu, menus);
                        }
                    }
//                }
            }
        }
        return parent;
    }

    @RequestMapping(value = "/remove", method = { RequestMethod.POST, RequestMethod.DELETE })
    @ResponseBody
    public ResultInfo removeMenus(@RequestParam Long... ids) {
        menuService.remove(ids);
        return new ResultInfo(true,ResultCode.SUCCESSFUL);
    }

    @RequestMapping(value = "/save", method = { RequestMethod.POST })
    @ResponseBody
    public ResultInfo saveMenus(@RequestParam Menu menu) {
        menuService.save(menu);
        return new ResultInfo(true,ResultCode.SUCCESSFUL);
    }
}

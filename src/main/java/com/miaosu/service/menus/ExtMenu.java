package com.miaosu.service.menus;

import com.miaosu.model.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * Ext 菜单
 */
public class ExtMenu extends Menu {

    /**
     * 是否展开
     */
    private boolean expandable;

    /**
     * 单击展开
     */
    private boolean singleExpand;


    /**
     * id path. 全路径
     */
    protected String idPath;

    /**
     * text path. 全路径
     */
    protected String textPath;

    /**
     * 子节点
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<Menu> children = new ArrayList<>();

    public ExtMenu(){}

    public ExtMenu(Menu menu){
        this.id = menu.getId();
        this.text = menu.getText();
        this.iconCls = menu.getIconCls();
        this.parentId = menu.getParentId();
        this.leaf = menu.isLeaf();
        this.alias = menu.getAlias();
        this.url = menu.getUrl();
        this.authorities = menu.getAuthorities();
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public boolean isSingleExpand() {
        return singleExpand;
    }

    public void setSingleExpand(boolean singleExpand) {
        this.singleExpand = singleExpand;
    }

    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    public String getTextPath() {
        return textPath;
    }

    public void setTextPath(String textPath) {
        this.textPath = textPath;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }

    public void addChildren(Menu menu) {
        this.children.add(menu);
    }

}

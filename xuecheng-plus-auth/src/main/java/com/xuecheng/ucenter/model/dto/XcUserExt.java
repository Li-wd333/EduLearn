package com.xuecheng.ucenter.model.dto;

import com.xuecheng.ucenter.model.po.XcUser;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 用户扩展信息
 * @author Mr.M
 * @date 2022/9/30 13:56
 * @version 1.0
 */
public class XcUserExt extends XcUser {
    //用户权限
    List<String> permissions = new ArrayList<>();

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}

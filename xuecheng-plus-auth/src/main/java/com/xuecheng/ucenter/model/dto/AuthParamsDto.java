package com.xuecheng.ucenter.model.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.M
 * @version 1.0
 * @description 认证用户请求参数
 * @date 2022/9/29 10:56
 */
public class AuthParamsDto {

    private String username; //用户名
    private String password; //域  用于扩展
    private String cellphone;//手机号
    private String checkcode;//验证码
    private String checkcodekey;//验证码key
    private String authType; // 认证的类型   password:用户名密码模式类型    sms:短信模式类型
    private Map<String, Object> payload = new HashMap<>();//附加数据，作为扩展，不同认证类型可拥有不同的附加数据。如认证类型为短信时包含smsKey : sms:3d21042d054548b08477142bbca95cfa; 所有情况下都包含clientId

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getCheckcode() {
        return checkcode;
    }

    public void setCheckcode(String checkcode) {
        this.checkcode = checkcode;
    }

    public String getCheckcodekey() {
        return checkcodekey;
    }

    public void setCheckcodekey(String checkcodekey) {
        this.checkcodekey = checkcodekey;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}

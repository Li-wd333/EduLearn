package com.xuecheng.utils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SecurityUtils {
    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);

    public static XcUser getUser() {
        try {
            //拿到当前用户的身份
            Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principalObj instanceof String) {
                //取出用户身份信息
                String principal = principalObj.toString();
                //将json转成对象
                XcUser user = JSON.parseObject(principal, XcUser.class);
                return user;
            }
        } catch (Exception e) {
            log.error("用户身份信息获取失败！");
            e.printStackTrace();
        }

        return null;
    }


    public static class XcUser implements Serializable {

        private static final long serialVersionUID = 1L;

        private String id;

        private String username;

        private String password;

        private String salt;

        private String name;
        private String nickname;
        private String wxUnionid;
        private String companyId;
        /**
         * 头像
         */
        private String userpic;

        private String utype;

        private LocalDateTime birthday;

        private String sex;

        private String email;

        private String cellphone;

        private String qq;

        /**
         * 用户状态
         */
        private String status;

        private LocalDateTime createTime;

        private LocalDateTime updateTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

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

        public String getSalt() {
            return salt;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getWxUnionid() {
            return wxUnionid;
        }

        public void setWxUnionid(String wxUnionid) {
            this.wxUnionid = wxUnionid;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public String getUserpic() {
            return userpic;
        }

        public void setUserpic(String userpic) {
            this.userpic = userpic;
        }

        public String getUtype() {
            return utype;
        }

        public void setUtype(String utype) {
            this.utype = utype;
        }

        public LocalDateTime getBirthday() {
            return birthday;
        }

        public void setBirthday(LocalDateTime birthday) {
            this.birthday = birthday;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCellphone() {
            return cellphone;
        }

        public void setCellphone(String cellphone) {
            this.cellphone = cellphone;
        }

        public String getQq() {
            return qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }

        public LocalDateTime getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
        }
    }
}

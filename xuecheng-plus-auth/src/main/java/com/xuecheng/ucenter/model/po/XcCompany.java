package com.xuecheng.ucenter.model.po;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author itcast
 */
@TableName("xc_company")
public class XcCompany implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 联系人名称
     */
    private String linkname;

    /**
     * 名称
     */
    private String name;

    private String mobile;

    private String email;

    /**
     * 简介
     */
    private String intro;

    /**
     * logo
     */
    private String logo;

    /**
     * 身份证照片
     */
    private String identitypic;

    /**
     * 工具性质
     */
    private String worktype;

    /**
     * 营业执照
     */
    private String businesspic;

    /**
     * 企业状态
     */
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLinkname() {
        return linkname;
    }

    public void setLinkname(String linkname) {
        this.linkname = linkname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getIdentitypic() {
        return identitypic;
    }

    public void setIdentitypic(String identitypic) {
        this.identitypic = identitypic;
    }

    public String getWorktype() {
        return worktype;
    }

    public void setWorktype(String worktype) {
        this.worktype = worktype;
    }

    public String getBusinesspic() {
        return businesspic;
    }

    public void setBusinesspic(String businesspic) {
        this.businesspic = businesspic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

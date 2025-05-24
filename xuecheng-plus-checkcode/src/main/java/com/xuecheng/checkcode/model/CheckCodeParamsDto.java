package com.xuecheng.checkcode.model;

/**
 * @author Mr.M
 * @version 1.0
 * @description 验证码生成参数类
 * @date 2022/9/29 15:48
 */

public class CheckCodeParamsDto {

    /**
     * 验证码类型:pic、sms、email等
     */
    private String checkCodeType;

    /**
     * 业务携带参数
     */
    private String param1;
    private String param2;
    private String param3;

    public String getCheckCodeType() {
        return checkCodeType;
    }

    public void setCheckCodeType(String checkCodeType) {
        this.checkCodeType = checkCodeType;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }
}

package com.xuecheng.controller;

import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.WxAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
public class WxLoginController {
    @Autowired
    private WxAuthService wxAuthService;
    private static final Logger log = LoggerFactory.getLogger(WxLoginController.class);

    /**
     * 微信回调
     * @param code
     * @param state
     * @return
     * @throws IOException
     */
    @RequestMapping("/wxLogin")
    public String wxLogin(String code, String state) throws IOException {
        log.debug("微信扫码回调,code:{},state:{}",code,state);
        //请求微信申请令牌，拿到令牌查询用户信息，将用户信息写入本项目数据库
        //远程调用微信服务 拿到令牌  通过令牌查询用户的基本信息
        XcUser xcUser = wxAuthService.wxAuth(code);
        //暂时硬编写，目的是调试环境
        if(xcUser==null){
            return "redirect:http://www.51xuecheng.cn/error.html";
        }
        String username = xcUser.getUsername();
        return "redirect:http://www.51xuecheng.cn/sign.html?username="+username+"&authType=wx";
    }
}
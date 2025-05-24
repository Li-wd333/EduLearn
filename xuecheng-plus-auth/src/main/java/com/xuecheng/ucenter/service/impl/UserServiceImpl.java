package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    ApplicationContext  applicationContext; //  spring容器
    @Autowired
    private XcUserMapper  xcUserMapper;

    //传入的请求认证的参数是AuthParamsDto
    @Override
    public UserDetails loadUserByUsername(String var1) throws UsernameNotFoundException {
        //将传入的JSON格式参数转为AuthParamsDto
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(var1, AuthParamsDto.class);
        } catch (Exception e) {
            throw new RuntimeException("请求认证参数不符合要求！");
        }
        //认证类型 ，有密码和微信 ...
        String authType = authParamsDto.getAuthType();

        //根据类型认证从spring  容器中获取对应的AuthService(Bean)
        String beanName = authType + "_authService";
        AuthService bean = applicationContext.getBean(beanName, AuthService.class);
        //统一完成认证
        XcUserExt xcUserExt = bean.execute(authParamsDto);
        //封装数据
        UserDetails userDetails =  getUserPrincipal(xcUserExt);
        return userDetails;
    }

    /**
     * 封装返回的数据
     * @param xcUserExt
     * @return
     */
    private UserDetails getUserPrincipal(XcUserExt xcUserExt) {
        String password = xcUserExt.getPassword();
        //权限集合
        String[] authorities = {"test"};
        //将用户的信息转为JSON字符串 用与扩展字段
        String json = JSON.toJSONString(xcUserExt);
        UserDetails userDetails = User.withUsername(json).password(password).authorities(authorities).build();
        return userDetails;
    }
}

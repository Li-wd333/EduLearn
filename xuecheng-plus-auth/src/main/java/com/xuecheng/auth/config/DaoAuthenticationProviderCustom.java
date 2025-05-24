package com.xuecheng.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class DaoAuthenticationProviderCustom extends DaoAuthenticationProvider {

    @Autowired // 注入自定义的UserDetailsService
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }
    @Override // 覆盖父类的方法
    protected void additionalAuthenticationChecks(UserDetails userDetails,UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        //编写自定义的认证逻辑
    }
}

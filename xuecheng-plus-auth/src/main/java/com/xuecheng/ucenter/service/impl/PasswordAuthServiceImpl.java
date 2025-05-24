package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("password_authService")
public class PasswordAuthServiceImpl implements AuthService {
    @Autowired
    private CheckCodeClient checkCodeClient;
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    private PasswordEncoder  passwordEncoder;
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //校验验证码
        String checkcode = authParamsDto.getCheckcode();
        String checkcodekey = authParamsDto.getCheckcodekey();
        if (checkcodekey == null || checkcode == null){
            throw new RuntimeException("请输入验证码");
        }
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (verify == null|| !verify){
            throw new RuntimeException("验证码输入错误");
        }
        //  根据用户名查询用户信息
        String username = authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>()
                .eq(XcUser::getUsername,username)
        );
        //当查询到用户不存在 要返回null即可 spring security会抛出异常
        if (xcUser == null){
            throw new RuntimeException("用户不存在");
        }
        //如果查询到用户信息 就拿到正确的密码 最终封装到UserDetails对象返回给spring security 进行校验
        String DBPassword = xcUser.getPassword();
        //验证密码是否正确
        boolean matches = passwordEncoder.matches(authParamsDto.getPassword(), DBPassword);
        if (!matches){
            throw new RuntimeException("账号或密码错误");
        }
        //封装用户信息 返回
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        return xcUserExt;
    }
}

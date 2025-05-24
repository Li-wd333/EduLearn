package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcRoleMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcRole;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.AuthService;
import com.xuecheng.ucenter.service.WxAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service("wx_authService")
public class WxAuthServiceImpl implements AuthService, WxAuthService {

    @Value("${weixin.appid}")
    String appid;
    @Value("${weixin.secret}")
    String secret;
    @Autowired
    private XcUserRoleMapper xcUserRoleMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private XcUserMapper xcUserMapper;
    @Autowired
    private WxAuthServiceImpl  currentProxy;
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //得到用户账号
        String username = authParamsDto.getUsername();
        //查询用户
        XcUser xcUser = xcUserMapper.selectOne(
                new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username)
        );
        if (xcUser == null){
            throw new RuntimeException("用户不存在");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        return xcUserExt;
    }

    /**
     * 微信扫码登录
     * @param code
     * @return
     */
    @Override
    public XcUser wxAuth(String code) {
        //申请令牌
        Map<String, String> accessToken = getAccessToken(code);
        //获取令牌
        String access_token = accessToken.get("access_token");
        String openid = accessToken.get("openid");
        //携带令牌获取用户信息
        Map<String,String> wxUserInfo = getWxUserInfo(access_token,openid);
        //保存到数据库中
        XcUser xcUser = currentProxy.saveWxUser(wxUserInfo);
        return xcUser;
    }

    /**
     * 通过code获取access_token 和 openid
     * @param code
     * @return
     */
    private Map<String,String> getAccessToken(String code){
        //请求路径
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        String new_url = String.format(url,appid,secret,code);
        //远程调用此接口
        ResponseEntity<String> exchange = restTemplate.exchange(new_url,
                HttpMethod.POST,
                null,
                String.class);
        //获取相应的数据  // 将数据转换为utf-8 防止乱码 ISO_8859_1
        String body = exchange.getBody();
        //将数据转换为map
        Map<String,String> map = JSON.parseObject(body, Map.class);
        //返回
        return map;
    }

    /**
     * 获取微信用户信息
     * @param access_token
     * @param openid
     * @return
     */
    private Map<String,String> getWxUserInfo(String access_token,String openid){
        //请求路径
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String new_url = String.format(url,access_token,openid);
        //远程调用此接口
        ResponseEntity<String> exchange = restTemplate.exchange(new_url,
                HttpMethod.POST,
                null,
                String.class);
        //获取响应数据
        String body = new String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
        //将数据转换为map
        Map<String,String> map = JSON.parseObject(body, Map.class);
        return map;
    }

    /**
     * 保存用户信息到数据库
     * @param wxUserInfo
     * @return
     */
    @Transactional
    public XcUser saveWxUser(Map<String, String> wxUserInfo){
        //根据unionid  查询用户信息
        String unionid = wxUserInfo.get("unionid");
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid));
        if (xcUser != null){
            return xcUser; //老用户直接返回
        }
        //新增用户
        xcUser = new XcUser();
        xcUser.setId(UUID.randomUUID().toString()); //主键
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        xcUser.setWxUnionid(unionid);
        xcUser.setNickname(wxUserInfo.get("nickname"));
        xcUser.setName(wxUserInfo.get("nickname"));
        xcUser.setStatus("1");
        xcUser.setUtype("101001");
        xcUser.setCreateTime(LocalDateTime.now());
        //插入数据
        xcUserMapper.insert(xcUser);

        //添加用户角色
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(xcUser.getId());
        xcUserRole.setRoleId("17");
        xcUserRole.setCreateTime(LocalDateTime.now());
        //插入数据
        xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }
}

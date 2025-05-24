package com.xuecheng.ucenter.feignclient;

import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckCodeClientFactory implements FallbackFactory<CheckCodeClient> {
    private static final Logger log = LoggerFactory.getLogger(CheckCodeClientFactory.class);
    @Override
    public CheckCodeClient create(Throwable throwable) {
        log.error("验证码校验失败 熔断异常:{}",throwable.getMessage());
        return null;
    }
}

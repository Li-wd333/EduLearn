package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 可以拿到异常信息
 */
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {

    @Override
    public MediaServiceClient create(Throwable throwable) { //拿到了异常信息
        return new MediaServiceClient() {
            //发生熔断上传服务调用此方法执行降级
            @Override
            public String upload(MultipartFile filedata, String objectName) {
                System.out.println("熔断方法执行了");
                System.out.println(throwable.getMessage());
                return null;
            }
        };
    }
}

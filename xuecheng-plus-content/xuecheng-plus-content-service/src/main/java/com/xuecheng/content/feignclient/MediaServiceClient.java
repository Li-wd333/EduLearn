package com.xuecheng.content.feignclient;

import com.xuecheng.content.config.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
                                    //Feign本身不支持传输文件，所以需要配置MultipartSupportConfig
                                    //使用fallback指定降级处理类 无法拿到异常
@FeignClient(value = "media-api",configuration = MultipartSupportConfig.class,fallbackFactory = MediaServiceClientFallbackFactory.class) //MultipartSupportConfig 配置文件上传
public interface MediaServiceClient {
    @PostMapping(value = "/media/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String upload(
            @RequestPart("filedata") MultipartFile filedata,
            @RequestParam(value= "objectName",required=false) String objectName);
}

package com.xuecheng.content.feignclient;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 降级方法
 */
//@Component
public class MediaServiceClientFallback implements MediaServiceClient{
    @Override
    public String upload(MultipartFile filedata, String objectName) {
        System.out.println("调用媒资管理服务上传文件失败了！");
        return "";
    }
}

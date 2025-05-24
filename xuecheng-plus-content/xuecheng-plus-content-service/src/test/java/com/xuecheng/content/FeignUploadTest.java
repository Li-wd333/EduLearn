package com.xuecheng.content;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@SpringBootTest
public class FeignUploadTest {

    @Autowired
    private MediaServiceClient mediaServiceClient;
    @Test
    public void testUpload(){
        //将File 转化为 MultipartFile
        File file = new File("D:\\aaavedio\\ww222\\18.html");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        //远程上传
        String upload = mediaServiceClient.upload(multipartFile, "course/18.html");
        if (upload==null){
            System.out.println("程序走了降级服务！");
        }
    }
}

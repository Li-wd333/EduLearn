package com.xuecheng.media;

import com.alibaba.nacos.common.utils.IoUtils;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 测试minio的SDK
 */
public class MinioTest {
    /**
     * 测试上传文件
     */
    MinioClient minioClient = MinioClient.builder()
            .endpoint("http://192.168.101.65:9000")
            .credentials("minioadmin", "minioadmin")
            .build();
    @Test
    public void uploadFile() throws Exception {
        //通过扩展名得到媒体资源类型 mimetype 媒体资源类型
        ContentInfo mp4 = ContentInfoUtil.findExtensionMatch("mp4"); //得到 媒体资源 类型
        String mimeType = mp4.getMimeType();
        System.out.println(mimeType);
        //上传文件的参数信息
        UploadObjectArgs testbucket = UploadObjectArgs.builder()
                .bucket("testbucket") //存储桶名称
                .filename("D:\\王树国演讲.mp4") //本地文件路径
                .object("test/01/王树国演讲.mp4")   //文件(对象)名称
                .contentType("video/mp4")  //文件类型
                .build();
        //上传文件
        minioClient.uploadObject(testbucket);
    }

    @Test
    public void deleteFile() throws Exception {
        //删除文件的参数信息
        RemoveObjectArgs testbucket = RemoveObjectArgs.builder()
                .bucket("testbucket") //存储桶名称
                .object("test/01/王树国演讲.mp4")   //文件(对象)名称
                .build();
        //删除文件
        minioClient.removeObject(testbucket);
    }

    //查询文件 从miniio中获取文件信息
    @Test
    public void queryFile() throws Exception {
        //构建查询参数
        GetObjectArgs testbucket = GetObjectArgs.builder()
                .bucket("testbucket") //存储桶名称
                .object("test/01/王树国演讲.mp4")   //文件(对象)名称
                .build();
        //获取文件流  查询远程服务器获取到的文件流
        FileInputStream inputStream1 = new FileInputStream(new File("D:\\王树国演讲.mp4"));
        String local1 = DigestUtils.md5Hex(inputStream1);
        FilterInputStream inputStream = minioClient.getObject(testbucket);
        //指定输出流
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\王树国a.mp4"));
        //下载到本地
        IoUtils.copy(inputStream, outputStream);
        //校验文件的完整性对文件的内容进行 md5
        String local_md5 = DigestUtils.md5Hex(new FileInputStream(new File("D:\\王树国a.mp4")));
        if (local1.equals(local_md5)){
            System.out.println("文件下载成功");
        }else {
            System.out.println("文件下载失败");
        }
    }
    //将分块文件上传到minio
    @Test
    public void uploadChuck() throws Exception {
        for (int i = 0; i < 14; i++) {
            //上传文件的参数信息
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket") //存储桶名称
                    .filename("D:\\aaavedio\\demo\\"+i) //本地文件路径
                    .object("chuck/"+i)   //文件(对象)名称
                    .build();
            //上传文件
            minioClient.uploadObject(testbucket);
            System.out.println("上传分块文件成功："+i);
        }
    }
    //合并文件
    @Test
    public void mergeFile() throws Exception {
        //创建合并文件参数信息
//        List<ComposeSource> sources = new ArrayList<>();
//        for (int i = 0; i < 67; i++) {
//            ComposeSource testbucket = ComposeSource.builder().bucket("testbucket").object("chuck/" + i).build();
//            sources.add(testbucket);
//        }
        List<ComposeSource> sources = Stream.iterate(0, n -> ++n)//iterate 创建一个无限流
                        .limit(14)
                        .map(i -> ComposeSource
                                .builder()
                                .bucket("testbucket")
                                .object("chuck/" + i)
                                .build()
                        )
                        .collect(Collectors.toList());
        //合并文件参数信息
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge01.mp4")
                .sources(sources) // 分块文件列表
                .build();
        //合并文件
        //source testbucket/chuck/0: size 1048576 must be greater than 5242880  默认分块大小为5M
        minioClient.composeObject(composeObjectArgs);

        //批量删除分块
    }
}

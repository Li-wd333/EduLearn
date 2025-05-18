package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

public class BigFileTest {

    /**
     * 分块上传测试
     * @throws Exception
     */
    @Test
    public void testChunkUpload() throws Exception {
        //源文件
        File file = new File("D:\\王树国演讲.mp4");
        //分块文件目录
        String chunkFileFolderPath = "D:\\aaavedio\\demo\\";
        //分块文件大小
        int chunkSize = 1024 * 1024 * 5; // 5M
        //分块文件个数
        int chunkNum = (int)Math.ceil(file.length() * 1.0 / chunkSize);
        //使用流从源文件中读取数据，写入分块文件
        RandomAccessFile accessFile = new RandomAccessFile(file, "r"); //RandomAccessFile 随机访问文件
        //分块文件
        byte[] bytes = new byte[1024];
        for (int i = 0; i < chunkNum; i++) {
            File file1 = new File(chunkFileFolderPath + i);
            //分块文件写入流
            RandomAccessFile chunkFile = new RandomAccessFile(file1, "rw");
            int len = -1;
            while ((len= accessFile.read(bytes))!= -1){
                chunkFile.write(bytes,0,len); //写入分块文件
                if (chunkFile.length()>=chunkSize){//块文件已满
                    break;
                }
            }
            chunkFile.close();
        }
        accessFile.close();
    }

    /**
     * 分块合并测试
     * @throws Exception
     */
    @Test
    public void testMergeFile() throws Exception {
        //源文件
        File file = new File("D:\\王树国演讲.mp4");
        //分块文件目录
        String chunkFileFolderPath = "D:\\aaavedio\\demo\\";
        File sourceFile = new File(chunkFileFolderPath);
        //合并后的文件
        File mergeFile = new File("D:\\aaavedio\\mergeFile.mp4");
        //获取分块文件
        File[] files = sourceFile.listFiles();
        //转化为集合
        List<File> fileList = Arrays.asList(files);
        //排序
        fileList.sort((o1, o2)->Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName())); //升序排序
        //创建合并文件
        RandomAccessFile accessFile = new RandomAccessFile(mergeFile, "rw");
        //遍历集合
        for (File chunkFile : fileList) {
            RandomAccessFile chunkRandomFile = new RandomAccessFile(chunkFile, "r");
            int len = -1;
            byte[] bytes = new byte[1024];
            while ((len=  chunkRandomFile.read(bytes))!=  -1){
                //将分块文件写入合并文件
                accessFile.write(bytes,0,len);
            }
            chunkRandomFile.close();
        }
        accessFile.close(); //关闭流
        //MD5校验文件
        if (checkFile(file,mergeFile))
            System.out.println("校验成功");
        else
            System.out.println("校验失败");

    }
    //md5校验
    private boolean checkFile(File file, File mergeFile) throws Exception {
        FileInputStream inputStream = new FileInputStream(file);
        FileInputStream mergeInputStream = new FileInputStream(mergeFile);
        String sourceFileMd5 = DigestUtils.md5Hex(inputStream);
        String mergeFileMd5 = DigestUtils.md5Hex(mergeInputStream);
        return sourceFileMd5.equals(mergeFileMd5);
    }

}

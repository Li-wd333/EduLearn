package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * XxlJob开发示例（Bean模式）
 *
 * 开发步骤：
 *      1、任务开发：在Spring Bean实例中，开发Job方法；
 *      2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 *      3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 *      4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
public class VideoTask {

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(VideoTask.class);

    @Autowired
    private MediaFileProcessService  mediaFileProcessService;
    @Autowired
    private MediaFileService mediaFileService;
    //ffmpeg路径
    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;
    /**
     * 2、分片广播任务
     */
    @XxlJob("videoJobHandler")
    public void shardingJobHandler() throws Exception {
        //确定cpu核数
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex(); // 从0开始  执行器的分片序号
        int shardTotal = XxlJobHelper.getShardTotal(); //  分片总数
        //1.查询待处理任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, cpuCoreNum);
        //任务数量
        int size = mediaProcessList.size();
        log.info("查询到待处理任务数：{}",size);
        if (size <= 0){
            return;
        }
        //1.1创建线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(size); //线程池大小为任务数量
        //创建计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        //启动多线程
        mediaProcessList.forEach(mediaProcess -> {
            try {
                //将任务放入线程池中执行
                threadPool.execute(() -> {
                    //任务逻辑
                    //业务id
                    Long id = mediaProcess.getId();
                    //文件id 就是md5值
                    String fileId = mediaProcess.getFileId();
                    //2.开启任务  抢占任务 使用的是数据库乐观锁
                    boolean b = mediaFileProcessService.startTask(id);
                    if (!b){
                        log.debug("抢占失败，任务id:{}",id);
                        return;
                    }
                    //3.执行视频转码
                    //桶
                    String bucket = mediaProcess.getBucket();
                    //文件名称
                    String objectName = mediaProcess.getFilePath();
                    //下载到本地
                    File file = mediaFileService.downloadFileFromMinIO(bucket, objectName);
                    if (file == null){
                        log.error("下载视频出错，任务id: {}, bucket：{}，objectName：{}" ,id,bucket,objectName);
                        //保存失败的结果
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", mediaProcess.getFileId(), null, "下载视频到本地失败");
                        return;
                    }
                    //源avi视频的路径
                    String video_path = file.getAbsolutePath();
                    //转换后mp4文件的名称
                    String mp4_name = fileId+".mp4";
                    //转换后mp4文件的路径
                    //创建一个临时文件
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.error("创建临时文件失败：{}",e.getMessage());
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", mediaProcess.getFileId(), null, "创建临时文件失败");
                        return;
                    }

                    String mp4_path = mp4File.getAbsolutePath();
                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath,video_path,mp4_name,mp4_path);
                    //开始视频转换，成功将返回success
                    String res = videoUtil.generateMp4();

                    if (!res.equals("success")){
                        log.info("视频{}转换失败,原因：{}，bucket{},objectName{}",id,res,bucket,objectName);
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", mediaProcess.getFileId(), null, res);
                        return;
                    }
                    //4.上传到minio
                    boolean b1 = mediaFileService.addMediaFilesToMinio("video/mp4",bucket, objectName,mp4_path);
                    if (!b1){
                        log.error("视频{}上传到minio失败",id);
                        mediaFileProcessService.saveProcessFinishStatus(id, "3", mediaProcess.getFileId(), null, "上传到minio失败");
                        return;
                    }
                    //mp4文件的url
                    String url = getFilePathByMd5(fileId, ".mp4");
                    //5.保存结果到数据库
                    mediaFileProcessService.saveProcessFinishStatus(id, "2", fileId, url, null);
                });
            } finally {
                //线程结束，计数器减一
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(30, TimeUnit.MINUTES);
    }
    /**
     * 得到合并后的文件的地址
     * @param fileMd5 文件id即md5值
     * @param fileExt 文件扩展名
     * @return
     */
    private String getFilePathByMd5(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

}

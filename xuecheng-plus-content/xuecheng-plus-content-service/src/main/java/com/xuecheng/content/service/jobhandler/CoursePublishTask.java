package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.feignclient.SearchServiceClient;
import com.xuecheng.content.feignclient.po.CourseIndex;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CoursePublishTask extends MessageProcessAbstract {
    private static final Logger log = LogManager.getLogger(CoursePublishTask.class);

    @Autowired
    private CoursePublishService coursePublishService;
    @Autowired
    private SearchServiceClient  searchServiceClient;
    @Autowired
    private CoursePublishMapper  coursePublishMapper;
    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() {
        //分片参数
        int shardIndex = XxlJobHelper.getShardIndex(); //分片序号
        int shardTotal = XxlJobHelper.getShardTotal(); //分片总数
        //调用抽象类的方法 执行任务
        process(shardIndex,shardTotal,"course_publish",30,60);
    }

    //执行课程发布任务逻辑
    @Override
    public boolean execute(MqMessage mqMessage) {
        //如果此方法抛出异常,则证明此任务没完成，则消息重试
        //从mqMessage中拿到课程Id
        Long courseId = Long.parseLong(mqMessage.getBusinessKey1());
        //向elasticsearch中保存课程信息
        saveCourseIndex(mqMessage,courseId);
        //向redis中保存课程信息
//        saveCourseToRedis(mqMessage,courseId);
        //课程静态化上传到minio
        generateCourseHtml(mqMessage,courseId);
        //完成
        return true;
    }

    private void saveCourseToRedis(MqMessage mqMessage, Long courseId) {

    }

    private void saveCourseIndex(MqMessage mqMessage, Long courseId) {
        //任务id
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //取出第二个阶段的执行状态
        int stageTwo = mqMessageService.getStageTwo(id);
        //幂等处理
        if(stageTwo>0){
            log.info("课程静态化生成阶段2,该阶段执行过,直接返回,id:{}",id);
            return;
        }
        //查询课程信息 调用搜索服务添加索引接口
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        //调用搜索服务添加索引
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish,courseIndex);
        Boolean add = searchServiceClient.add(courseIndex);
        if(!add){
            XueChengPlusException.cast("添加课程索引失败");
        }
    }

    private void generateCourseHtml(MqMessage mqMessage,  Long courseId){
        //消息id
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //做任务幂等性处理
        //查询数据库取出该阶段执行的状态
        int stageOne = mqMessageService.getStageOne(id);
        if(stageOne>0){
            //如果大于0,说明该阶段执行过,直接返回
            log.info("课程静态化生成阶段1,该阶段执行过,直接返回,id:{}",id);
            return;
        }
        //开始进行静态化生成html文件
        //查询课程信息，调用搜索服务添加索引
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file==null){
            XueChengPlusException.cast("课程静态化生成文件为空");
        }
        //完成本阶段任务
        coursePublishService.uploadCourseHtml(courseId,file);//将html上传到minio
        //开始进行课程静态化
        mqMessageService.completedStageOne(id);
    }
}

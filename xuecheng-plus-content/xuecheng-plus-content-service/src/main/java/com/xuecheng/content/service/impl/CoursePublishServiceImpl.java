package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程发布接口实现类
 */
@Service
public class CoursePublishServiceImpl implements CoursePublishService {
    private static final Logger log = LoggerFactory.getLogger(CoursePublishServiceImpl.class);
    @Autowired
    private CourseBaseInfoService courseBaseInfoService;
    @Autowired
    private TeachplanService teachplanService;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CoursePublishMapper coursePublishMapper;
    @Autowired
    private MqMessageService mqMessageService;
    @Autowired
    private MediaServiceClient mediaServiceClient;
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //课程基本信息 查询
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfo);
        //课程计划信息 查询
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        coursePreviewDto.setTeachplans(teachplanTree);
        //返回结果
        return coursePreviewDto;
    }

    @Override
    @Transactional
    public void commitAudit(Long  companyId,Long courseId) {
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        if(courseBaseInfo == null){
            XueChengPlusException.cast("课程不存在");
        }
        //如果课程的审核状态为 已提交 则不允许提交审核
        if(courseBaseInfo.getAuditStatus().equals("202003")){
            XueChengPlusException.cast("课程已提交审核");
        }
        //只能提交本机构的课程
        if(!courseBaseInfo.getCompanyId().equals(companyId)){
            XueChengPlusException.cast("只能提交本机构的课程");
        }
        //课程的图片 课程计划信息 没有填写也不能提交
        String pic = courseBaseInfo.getPic();
        if(pic == null){
            XueChengPlusException.cast("请上传课程图片");
        }
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if(teachplanTree == null || teachplanTree.size() == 0){
            XueChengPlusException.cast("请填写课程计划");
        }

        CoursePublishPre coursePublishPre = new CoursePublishPre();

        //查询到课程基本信息 营销信息 计划信息等 插入到 课程预发布表中
        BeanUtils.copyProperties(courseBaseInfo,coursePublishPre);
        //营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //转JSON
        String courseMarketString = JSON.toJSONString(courseMarket); //JSON.toJSONString();
        coursePublishPre.setMarket(courseMarketString);
        //课程计划插入
        String teachplanTreeString = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(teachplanTreeString);
        //状态已提交
        coursePublishPre.setStatus("202003");
        //设置机构id
        coursePublishPre.setCompanyId(companyId);
        //提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        //查寻预发布表 如果有记录则更新 没有则插入
        CoursePublishPre coursePublishPre1 = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre1 != null){
            //插入
            coursePublishPreMapper.updateById(coursePublishPre);
        }else{
            //更新
            coursePublishPreMapper.insert(coursePublishPre);
        }
        //更新基本信息表的审核状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setAuditStatus("202003"); //设为已提交
        //更新
        courseBaseMapper.updateById(courseBase);
    }

    /**
     * 课程发布
     * @param companyId
     * @param courseId
     */
    @Override
    public void publish(Long companyId, Long courseId) {
        //查询预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(!companyId.equals(coursePublishPre.getCompanyId())){
            XueChengPlusException.cast("不允许跨机构发布");
        }
        if (coursePublishPre == null){
            XueChengPlusException.cast("无预发布课程！");
        }
        //添加到发布表
        String status = coursePublishPre.getStatus();
        if(!status.equals("202004")){
            XueChengPlusException.cast("课程没有审核通过,不允许发布！");
        }
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        //先查询课程发布表
        CoursePublish coursePublish1 = coursePublishMapper.selectById(courseId);
        if(coursePublish1 == null){
            coursePublishMapper.insert(coursePublish);
        }else {
            coursePublishMapper.updateById(coursePublish);
        }
        //修改课程的状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("202002");
        courseBaseMapper.updateById(courseBase);
        //向消息表写入数据
        saveCoursePublishMessage(courseId);

        //删除预发布表
        int id = coursePublishPreMapper.deleteById(courseId);
        if(id <= 0){
            XueChengPlusException.cast("删除课程预发布表失败");
        }
    }

    /**
     * 生成静态化页面
     * @param courseId  课程id
     * @return
     */
    @Override
    public File generateCourseHtml(Long courseId) {
        File file = null;
        try {
            //Template 模板
            Configuration configuration = new Configuration(Configuration.getVersion());
            //拿到classPath路径
            String path = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(path + "/templates/"));
            //设置字符集
            configuration.setDefaultEncoding("utf-8");
            //加载模板
            Template template = configuration.getTemplate("course_template.ftl");
            //数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(18L);
            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);
            file = new File("D:\\aaavedio\\ww222\\18.html");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            //输入流
            InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
            file = File.createTempFile(""+courseId, ".html");
            //输出文件
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            //使用流将html写入文件
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (Exception e) {
            log.debug("页面静态化失败");
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 上传静态化页面
     * @param courseId
     * @param file  静态化文件
     */
    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        try {
            //将File 转化为 MultipartFile
            MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
            //远程上传
            String upload = mediaServiceClient.upload(multipartFile, "course/"+courseId+".html");
            if (upload==null){
                log.debug("远程调用走降级逻辑得到上传的结果为null,课程id:{}",courseId);
                XueChengPlusException.cast("上传静态文件过程中出现异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            XueChengPlusException.cast("上传静态文件过程中出现异常");
        }
    }

    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish",
                courseId.toString(), null, null);
        if (mqMessage==null){
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }
    }
}

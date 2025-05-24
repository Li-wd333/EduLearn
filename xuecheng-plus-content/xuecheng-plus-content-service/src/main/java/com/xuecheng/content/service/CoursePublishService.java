package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

import java.io.File;

public interface CoursePublishService {
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 提交审核
     * @param courseId
     */
    void commitAudit(Long companyId,Long courseId);

    /**
     * 课程发布
     * @param companyId
     * @param courseId
     */
    void publish(Long companyId, Long courseId);
    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     */
    File generateCourseHtml(Long courseId);
    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     */
    void uploadCourseHtml(Long courseId,File file);
}

package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {

    /**
     * 根据课程id查询课程教师列表
     * @param courseId
     * @return
     */
    List<CourseTeacher> getCourseTeacherList(Long courseId);

    /**
     * 添加/修改课程教师信息
     * @param courseTeacherDto
     */
    CourseTeacher addCourseTeacher(Long courseId,CourseTeacherDto courseTeacherDto);

    /**
     * 删除课程教师信息
     * @param companyId
     * @param courseId
     * @param teacherId
     */
    void deleteCourseTeacher(Long companyId, Long courseId, Long teacherId);
}

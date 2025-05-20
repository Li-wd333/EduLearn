package com.xuecheng.content.model.dto;

import java.util.List;

public class CoursePreviewDto {
    public CourseBaseInfoDto getCourseBase() {
        return courseBase;
    }

    public void setCourseBase(CourseBaseInfoDto courseBase) {
        this.courseBase = courseBase;
    }

    public List<TeachplanDto> getTeachplans() {
        return teachplans;
    }

    public void setTeachplans(List<TeachplanDto> teachplans) {
        this.teachplans = teachplans;
    }

    // 课程基本信息 营销信息
    private CourseBaseInfoDto courseBase;
    // 课程计划信息
    private List<TeachplanDto> teachplans;
    // 课程师资信息
}

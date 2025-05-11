package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@ApiModel(value = "课程教师信息")
public class CourseTeacherDto {
    //主键
    @ApiModelProperty(value = "主键", required = false)
    private Long id;
    //课程标识
    @ApiModelProperty(value = "课程标识", required = true)
    private Long courseId;
    //教师名称
    @ApiModelProperty(value = "教师名称", required = true)
    private String teacherName;
    //教师职位
    @ApiModelProperty(value = "教师职位", required = true)
    private String position;
    //教师简介
    @ApiModelProperty(value = "教师简介", required = false)
    private String introduction;
    //教师图片
    @ApiModelProperty(value = "教师图片", required = false)
    private String photograph;
    //创建时间
    @ApiModelProperty(value = "创建时间", required = false)
    private LocalDateTime createDate;
    public CourseTeacherDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseTeacherDto(Long id,Long courseId, String teacherName, String position, String introduction, String photograph, LocalDateTime createDate) {
        this.courseId = courseId;
        this.id = id;
        this.teacherName = teacherName;
        this.position = position;
        this.introduction = introduction;
        this.photograph = photograph;
        this.createDate = createDate;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPhotograph() {
        return photograph;
    }

    public void setPhotograph(String photograph) {
        this.photograph = photograph;
    }
}

package com.xuecheng.content.model.dto;

public class SaveTeachplanDto {
    /***
     * 教学计划id
     */
    private Long id;

    public Long getId() {
        return id;
    }

    public SaveTeachplanDto() {
    }

    @Override
    public String toString() {
        return "SaveTeachplanDto{" +
                "id=" + id +
                ", pname='" + pname + '\'' +
                ", parentid=" + parentid +
                ", grade=" + grade +
                ", mediaType='" + mediaType + '\'' +
                ", courseId=" + courseId +
                ", coursePubId=" + coursePubId +
                ", isPreview='" + isPreview + '\'' +
                '}';
    }

    public SaveTeachplanDto(Long id, String pname, Long parentid, Integer grade, String mediaType, Long courseId, Long coursePubId, String isPreview) {
        this.id = id;
        this.pname = pname;
        this.parentid = parentid;
        this.grade = grade;
        this.mediaType = mediaType;
        this.courseId = courseId;
        this.coursePubId = coursePubId;
        this.isPreview = isPreview;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public Long getParentid() {
        return parentid;
    }

    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCoursePubId() {
        return coursePubId;
    }

    public void setCoursePubId(Long coursePubId) {
        this.coursePubId = coursePubId;
    }

    public String getIsPreview() {
        return isPreview;
    }

    public void setIsPreview(String isPreview) {
        this.isPreview = isPreview;
    }

    /**
     * 课程计划名称
     */
    private String pname;

    /**
     * 课程计划父级Id
     */
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    private String mediaType;


    /**
     * 课程标识
     */
    private Long courseId;

    /**
     * 课程发布标识
     */
    private Long coursePubId;


    /**
     * 是否支持试学或预览（试看）
     */
    private String isPreview;

}

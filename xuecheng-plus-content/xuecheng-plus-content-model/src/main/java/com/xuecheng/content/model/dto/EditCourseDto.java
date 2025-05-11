package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModelProperty;

public class EditCourseDto extends AddCourseDto{
    @ApiModelProperty(value = "课程id", required = true)
    private Long id;

    public EditCourseDto(Long id) {
        this.id = id;
    }

    public EditCourseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import java.io.Serializable;
import java.util.List;

public class CourseCategoryDto extends CourseCategory implements Serializable {
    /**
     * 子结点
     */
    private List<CourseCategoryDto> childrenTreeNodes;

    public List<CourseCategoryDto> getChildrenTreeNodes() {
        return childrenTreeNodes;
    }

    public void setChildrenTreeNodes(List<CourseCategoryDto> childrenTreeNodes) {
        this.childrenTreeNodes = childrenTreeNodes;
    }
    public CourseCategoryDto() {
    }
    public CourseCategoryDto(List<CourseCategoryDto> childrenTreeNodes) {
        this.childrenTreeNodes = childrenTreeNodes;
    }
}

package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryDto;

import java.util.List;

public interface CourseCategoryService {
    List<CourseCategoryDto> queryTreeNodes(String id);
}

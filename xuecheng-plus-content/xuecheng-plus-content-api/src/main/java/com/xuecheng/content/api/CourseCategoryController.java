package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryDto;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "课程分类相关接口")
public class CourseCategoryController {

    @Autowired
    CourseCategoryService courseCategoryService;
    /**
     * 查询课程分类树形结构
     * @return
     */
    @ApiOperation("查询课程分类树形结构")
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryDto> queryTreeNodes() {
       return courseCategoryService.queryTreeNodes("1");
    }
}

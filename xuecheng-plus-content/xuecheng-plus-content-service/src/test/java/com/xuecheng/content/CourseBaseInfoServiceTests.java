package com.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CourseBaseInfoServiceTests {
    @Autowired
    private CourseBaseInfoService courseBaseInfoService;
    @Test
    public void testCourseBaseMapper() {
        QueryCourseParamsDto dto = new QueryCourseParamsDto();
        dto.setCourseName("java");
        Long pageNo = 1L;
        Long pageSize = 2L;
        PageParams pageParams = new PageParams(pageNo, pageSize);
        PageResult<CourseBase> pageResult = courseBaseInfoService.queryCourseBaseList(pageParams, dto);
        System.out.println(pageResult);
    }
}

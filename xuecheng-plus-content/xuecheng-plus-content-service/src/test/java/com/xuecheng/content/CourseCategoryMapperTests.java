package com.xuecheng.content;


import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

//@SpringBootTest
public class CourseCategoryMapperTests {
    @Autowired
   private CourseCategoryMapper courseCategoryMapper;
    @Test
    public void testCourseCategoryMapper() {
        List<CourseCategoryDto> categoryDtos = courseCategoryMapper.selectTreeNodes("1");
        System.out.println(categoryDtos);
        System.out.println("条数："+categoryDtos.size());
    }
}

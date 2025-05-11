package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //相当于 @Controller + @ResponseBody
@Api(value = "课程教师管理接口",tags = "课程教师管理接口")
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService courseTeacherService;

    /**
     * 查询课程教师信息
     * @param courseId
     * @return
     */
    @ApiOperation("查询课程教师信息")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getCourseTeacherList(@PathVariable Long courseId) {
        return courseTeacherService.getCourseTeacherList(courseId);
    }

    /**
     * 添加/修改课程教师信息
     * @param courseTeacherDto
     * @return
     */
    @ApiOperation("添加/修改课程教师信息")
    @PostMapping("/courseTeacher")
    public CourseTeacher addCourseTeacher(@RequestBody CourseTeacherDto courseTeacherDto) {
        //TODO 获取用户信息 后期获取用户信息
        Long companyId = 1232141425L;
        CourseTeacher courseTeacher = courseTeacherService.addCourseTeacher(companyId, courseTeacherDto);
        return courseTeacher;
    }
    @ApiOperation("删除课程教师信息")
    @DeleteMapping("/courseTeacher/course/{courseId}/{TeacherId}")
    public void deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long TeacherId) {
        //TODO 获取用户信息 后期获取用户信息
        Long companyId = 1232141425L;
        courseTeacherService.deleteCourseTeacher(companyId,courseId, TeacherId);
    }

}

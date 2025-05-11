package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Api(tags = "课程信息管理接口")
@RestController //相当于 @Controller + @ResponseBody
public class CourseBaseController {

    private static final Logger log = LogManager.getLogger(CourseBaseController.class);
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    /**
     * 分页查询课程信息
     * @param pageParams
     * @param queryCourseParamsDto
     * @return
     */
    @PostMapping("/course/list")
    @ApiOperation("分页查询课程信息接口")
    public PageResult<CourseBase> list(
            PageParams pageParams,
            @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
        log.info("分页查询课程信息接口,参数:{}",queryCourseParamsDto);
        PageResult<CourseBase> result = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        return result;
    }

    /**
     * 新增课程信息
     * @param addCourseDto
     * @return
     */
    @PostMapping("/course")
    @ApiOperation("新增课程接口")               //JSR 303校验
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Inser.class) AddCourseDto addCourseDto){
        log.info("新增课程接口,参数:{}",addCourseDto);
        //TODO 获取用户信息 后期获取用户信息
        Long companyId = 1232141425L;
        CourseBaseInfoDto courseBaseInfoDto1 = courseBaseInfoService.createCourseBase(companyId,addCourseDto);
        return courseBaseInfoDto1;
    }

    /**
     * 根据课程id查询课程信息接口
     * @param courseId
     * @return
     */
    @GetMapping("/course/{courseId}")
    @ApiOperation("根据课程id查询课程信息接口")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
        log.info("根据课程id查询课程信息接口,参数:{}",courseId);
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseBaseInfo(courseId);
        return courseBaseInfoDto;
    }
    //修改课程信息
    @PutMapping("/course")
    @ApiOperation("修改课程信息接口")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated(ValidationGroups.Update.class) EditCourseDto editCourseDto){
        //TODO 获取用户信息 后期获取用户信息
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId,editCourseDto);
    }
}

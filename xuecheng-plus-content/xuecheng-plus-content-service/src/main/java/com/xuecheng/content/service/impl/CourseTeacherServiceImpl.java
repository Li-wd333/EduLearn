package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    /**
     * 根据课程id查询课程教师列表
     * @param courseId
     * @return
     */
    @Override
    public List<CourseTeacher> getCourseTeacherList(Long courseId) {
        //构造查询条件
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        //查询课程教师列表
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        return courseTeachers;
    }

    /**
     * 新增教师信息
     * @param courseTeacherDto
     */
    @Override
    public CourseTeacher addCourseTeacher(Long companyId,CourseTeacherDto courseTeacherDto) {
        //判断是新增还是修改
        Long id = courseTeacherDto.getId();
        if (id==null){
            //通过课程id查询课程
            CourseBase courseBase = courseBaseMapper.selectById(courseTeacherDto.getCourseId());
            //只能为本机构的课程添加教师
            if (!courseBase.getCompanyId().equals(companyId)) {
                XueChengPlusException.cast("只能为本机构的课程添加教师");
            }
            CourseTeacher courseTeacher = new CourseTeacher();
            //属性拷贝
            BeanUtils.copyProperties(courseTeacherDto,courseTeacher);
            courseTeacher.setCourseId(courseTeacherDto.getCourseId());
            courseTeacher.setCreateDate(LocalDateTime.now());
            //插入数据库
            int insert = courseTeacherMapper.insert(courseTeacher);
            if (insert<=0) {
                XueChengPlusException.cast("添加教师信息失败");
            }
            return courseTeacher;
        }else {
            //修改教师信息
            CourseTeacher courseTeacher = new CourseTeacher();
            BeanUtils.copyProperties(courseTeacherDto,courseTeacher);
            int update = courseTeacherMapper.updateById(courseTeacher);
            if (update<0) { //修改失败
                XueChengPlusException.cast("修改教师信息失败");
            }
            return courseTeacher;
        }
    }

    /**
     * 删除教师信息
     * @param companyId
     * @param courseId
     * @param teacherId
     */
    @Override
    public void deleteCourseTeacher(Long companyId, Long courseId, Long teacherId) {
        //通过课程id查询课程
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //只能删除本机构的课程教师
        if (!courseBase.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("只能删除本机构的课程教师");
        }

        //构建查询条件
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        queryWrapper.eq(CourseTeacher::getId,teacherId);
        //删除教师信息
        int delete = courseTeacherMapper.delete(queryWrapper);
        if (delete<=0) {
            XueChengPlusException.cast("删除教师信息失败");
        }
    }
}

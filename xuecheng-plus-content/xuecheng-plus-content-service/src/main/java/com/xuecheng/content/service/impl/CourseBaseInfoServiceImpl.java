package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    /**
     * 分页查询课程信息
     * @param pageParams
     * @param queryCourseParamsDto
     * @return
     */
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        //拼装查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());
        //分页参数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        //返回结果
        PageResult<CourseBase> result = new PageResult<CourseBase>(
                pageResult.getRecords(),pageResult.getTotal()
                ,pageParams.getPageNo(),pageParams.getPageSize());
        return result;
    }

    /**
     * 新增课程信息
     * @param companyId
     * @param dto
     * @return
     */
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId,AddCourseDto dto) {
        //1.向课程基本信息表插入数据
        CourseBase courseBase = new CourseBase();
        //将传入的数据拷贝到courseBase对象中
        BeanUtils.copyProperties(dto,courseBase);
        courseBase.setCompanyId(companyId);//机构id
        courseBase.setCreateDate(LocalDateTime.now());//创建时间
        courseBase.setAuditStatus("202002"); //待提交
        courseBase.setStatus("202001"); //待发布
        int insert = courseBaseMapper.insert(courseBase);
        if (insert<=0) {
            throw new XueChengPlusException("保存课程基本信息失败");
        }
        //2.向课程营销表插入数据
        Long courseBaseId = courseBase.getId();
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarket);
        courseMarket.setId(courseBaseId);  //课程id id相同
        //保存营销信息
        saveCourseMarket(courseMarket);
        //查询课程信息 包括两部分
        CourseBaseInfoDto courseBaseInfoDto = getCourseBaseInfo(courseBaseId);
        return courseBaseInfoDto;
    }


    //查询课程信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        //查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        //查询营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        //封装数据
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }
        //课程分类的名称
        CourseCategory mtName = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(mtName.getName());
        CourseCategory stName = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(stName.getName());
        return courseBaseInfoDto;
    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId,EditCourseDto editCourseDto) {
        //拿到课程id
        Long courseId = editCourseDto.getId();
        //查询课程信息   具有一定业务逻辑的要在service层实现
        CourseBaseInfoDto courseBaseInfoDto = getCourseBaseInfo(courseId);
        if (courseBaseInfoDto == null) {
            XueChengPlusException.cast("课程不存在");
        }
        //校验参数的合法性
        //本机构的课程只能修改本机构的课程
        if (!companyId.equals(courseBaseInfoDto.getCompanyId())){
            XueChengPlusException.cast("本机构没有权限修改该课程");
        }
        //封装数据
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(editCourseDto,courseBase);
        //修改时间
        courseBase.setChangeDate(LocalDateTime.now());
        //1.修改课程基本信息
        int id = courseBaseMapper.updateById(courseBase);
        if (id <= 0) {
            XueChengPlusException.cast("修改课程基本信息失败");
        }
        //1.1修改课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto,courseMarket);
        courseMarket.setId(courseId);
        int update = saveCourseMarket(courseMarket);
        if (update <= 0) {
            XueChengPlusException.cast("修改课程营销信息失败");
        }
        //2.查询课程信息
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }

    //单独定义一个方法，用与保存课程营销信息
    private int saveCourseMarket(CourseMarket courseMarket) {
        //校验
        String charge = courseMarket.getCharge();
        if (StringUtils.isEmpty(charge)){
            throw new XueChengPlusException("收费规则为空");
        }
        //如果课程收费，价格不能为空
        if (charge.equals("201001")){
            if (courseMarket.getPrice() == null || courseMarket.getPrice()<=0){
                throw new XueChengPlusException("课程为收费，价格不能为空且不能小于0");
            }
        }
        //查询课程营销表，如果存在记录则更新，否则插入
        Long marketId = courseMarket.getId();
        CourseMarket courseMarketOld = courseMarketMapper.selectById(marketId);
        if (courseMarketOld != null) {
            //更新
            int update = courseMarketMapper.updateById(courseMarket);
            return update;
        }else {
            int insert = courseMarketMapper.insert(courseMarket);
            return insert;
        }
    }
}

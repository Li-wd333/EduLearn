package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachplanMediaMapper  teachplanMediaMapper;

    /**
     *  根据课程id查询课程计划
     * @param courseId
     * @return
     */
    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    /**
     * 新增/修改/保存课程计划
     * @param saveTeachplanDto
     */
    @Override
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //通过课程计划id判断是修改还是新增
        Long teachplanId = saveTeachplanDto.getId();
        if (teachplanId == null) {
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            //确定排序字段 找到同级节点的个数 +1
            teachplan.setOrderby(
                    getTeachplanOrderBy(saveTeachplanDto.getCourseId(),saveTeachplanDto.getParentid()));
            teachplanMapper.insert(teachplan);
        }else{
            //修改
            Teachplan teachplan = teachplanMapper.selectById(teachplanId);
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void deleteTeachplan(Long id) {
        if (id == null){
            XueChengPlusException.cast("课程计划id为空");
        }
        //1.根据id查询出课程计划
        Teachplan teachplan = teachplanMapper.selectById(id);
        //2.判断当前课程计划grade层级
        Integer grade = teachplan.getGrade();
        //3.如果是1级节点 即章节的话
        if (grade == 1){
            //判断是否有子节点
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid,id);
            int count = teachplanMapper.selectCount(queryWrapper);
            if (count > 0){
                //包含子节点则不能删除 提示改章节有子节点，不能删除
                XueChengPlusException.cast("该章节有子章节，不能删除");
            }else {
                teachplanMapper.deleteById(id);
            }
        }else {
            //5.若没有子节点则删除
            teachplanMapper.deleteById(id);
            //删除子章节关联的数据
            //查询该课程计划关联的媒资
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getTeachplanId,id);
            Integer count = teachplanMediaMapper.selectCount(queryWrapper);
            if (count > 0){
                teachplanMediaMapper.delete(queryWrapper);
            }
        }
    }

    /**
     * 课程计划下移
     * @param teachPlanID
     */
    @Override
    public void moveDown(Long teachPlanID) {
        //1.查询当前课程计划的信息
        Teachplan teachplan = teachplanMapper.selectById(teachPlanID);

        //2.查询当前课程计划的同级课程计划
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,teachplan.getCourseId()); //同课程
        queryWrapper.eq(Teachplan::getParentid,teachplan.getParentid()); //同级
        queryWrapper.orderByAsc(Teachplan::getOrderby); //按排序字段升序排序
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        if (teachplans.size() == 1){
            return;
        }
        for (int i = 0; i < teachplans.size(); i++) {
            Teachplan teachplan1 = teachplans.get(i);
            if (teachplan1.getId().equals(teachPlanID)){
                if (i == teachplans.size() - 1){
                    //最后一条
                    break;
                }
                //当前课程计划
                Teachplan teachplan2 = teachplans.get(i + 1);
                //交换排序字段
                Integer orderby1 = teachplan1.getOrderby();
                Integer orderby2 = teachplan2.getOrderby();
               teachplan1.setOrderby(orderby2);
               teachplan2.setOrderby(orderby1);
                //更新数据库
               teachplanMapper.updateById(teachplan1);
               teachplanMapper.updateById(teachplan2);
            }
        }

    }

    /**
     * 课程计划上移
     * @param teachPlanID
     */
    @Override
    public void moveUp(Long teachPlanID) {
        //1.查询当前课程计划的信息
        Teachplan teachplan = teachplanMapper.selectById(teachPlanID);

        //2.查询当前课程计划的同级课程计划
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,teachplan.getCourseId()); //同课程
        queryWrapper.eq(Teachplan::getParentid,teachplan.getParentid()); //同级
        queryWrapper.orderByAsc(Teachplan::getOrderby); //按排序字段升序排序
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        if (teachplans.size() == 1){
            return;
        }
        for (int i = 0; i < teachplans.size(); i++) {
            Teachplan teachplan1 = teachplans.get(i);

            if (teachplan1.getId().equals(teachPlanID)){
                if (i == 0){
                    //第一条
                    break;
                }
                //当前课程计划
                Teachplan teachplan2 = teachplans.get(i -1);
                //交换排序字段
                Integer orderby1 = teachplan1.getOrderby();
                Integer orderby2 = teachplan2.getOrderby();
                teachplan1.setOrderby(orderby2);
                teachplan2.setOrderby(orderby1);
                //更新数据库
                teachplanMapper.updateById(teachplan1);
                teachplanMapper.updateById(teachplan2);
            }
        }
    }

    /**
     * 计算排序字段
     * @param courseId
     * @param parentid
     * @return
     */
    private Integer getTeachplanOrderBy(Long courseId, Long parentid) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId); //课程id
        queryWrapper.eq(Teachplan::getParentid,parentid); //父节点id
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        //查询orderby字段最大值
        Integer max = teachplans.stream().map(Teachplan::getOrderby).max(Integer::compareTo).get();//获取最大值
        return max + 1;
    }
}

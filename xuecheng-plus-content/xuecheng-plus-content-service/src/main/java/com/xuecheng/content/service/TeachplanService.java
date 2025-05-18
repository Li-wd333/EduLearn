package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

public interface TeachplanService {
    /**
     * 查询课程计划树形结构
     * @param courseId
     * @return
     */
    public List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 新增/修改/保存课程计划
     * @param saveTeachplanDto
     */
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 删除课程计划
     * @param id
     */
    void deleteTeachplan(Long id);

    /**
     * 课程计划下移
     * @param teachPlanID
     */
    void moveDown(Long teachPlanID);

    /**
     * 课程计划上移
     * @param teachPlanID
     */
    void moveUp(Long teachPlanID);

    /**
     * 添加媒资关联
     * @param bindTeachplanMediaDto
     */
    void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
}

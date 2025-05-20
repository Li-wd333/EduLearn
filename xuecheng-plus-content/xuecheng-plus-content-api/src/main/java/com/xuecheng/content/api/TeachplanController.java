package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程计划管理相关接口
 */
@Api(value = "课程计划管理相关接口",tags = "课程计划管理相关接口")
@RestController
public class TeachplanController {

    @Autowired
    private TeachplanService teachplanService;

    /**
     * 查询课程计划树形结构
     * @param courseId
     * @return
     */
    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable long courseId){
        return teachplanService.findTeachplanTree(courseId);
    }

    /**
     * 课程计划创建或修改
     * @param teachplan
     */
    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }

    /**
     * 课程计划删除
     * @param id
     */
    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable Long id){
        teachplanService.deleteTeachplan(id);
    }

    /**
     * 课程计划下移
     * @param teachPlanID
     */
    @ApiOperation("课程计划下移")
    @PostMapping("/teachplan/movedown/{teachPlanID}")
    public void moveDown(@PathVariable Long teachPlanID){
        teachplanService.moveDown(teachPlanID);
    }
    /**
     * 课程计划上移
     * @param teachPlanID
     */
    @ApiOperation("课程计划上移")
    @PostMapping("/teachplan/moveup/{teachPlanID}")
    public void moveUp(@PathVariable Long teachPlanID){
        teachplanService.moveUp(teachPlanID);
    }

    /**
     * 课程计划和媒资信息绑定
     * @param bindTeachplanMediaDto
     */
    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }

    /**
     * 课程计划和媒资信息解绑
     * @param teachPlanId
     * @param mediaId
     */
    @ApiOperation(value = "课程计划和媒资信息解绑")
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public void unassociationMedia(@PathVariable Long teachPlanId,@PathVariable String mediaId){
        teachplanService.unassociationMedia(teachPlanId,mediaId);
    }
}

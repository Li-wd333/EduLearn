package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.time.LocalDateTime;
import java.util.List;

public class TeachplanDto extends Teachplan {

    public TeachplanDto() {
        super();
    }

    public TeachplanDto(List<TeachplanDto> teachPlanTreeNodes, TeachplanMedia teachplanMedia) {
        this.teachPlanTreeNodes = teachPlanTreeNodes;
        this.teachplanMedia = teachplanMedia;
    }

    public TeachplanDto(Long id, String pname, Long parentid, Integer grade, String mediaType, LocalDateTime startTime, LocalDateTime endTime, String description, String timelength, Integer orderby, Long courseId, Long coursePubId, Integer status, String isPreview, LocalDateTime createDate, LocalDateTime changeDate, List<TeachplanDto> teachPlanTreeNodes, TeachplanMedia teachplanMedia) {
        super(id, pname, parentid, grade, mediaType, startTime, endTime, description, timelength, orderby, courseId, coursePubId, status, isPreview, createDate, changeDate);
        this.teachPlanTreeNodes = teachPlanTreeNodes;
        this.teachplanMedia = teachplanMedia;
    }

    public List<TeachplanDto> getTeachPlanTreeNodes() {
        return teachPlanTreeNodes;
    }

    public void setTeachPlanTreeNodes(List<TeachplanDto> teachPlanTreeNodes) {
        this.teachPlanTreeNodes = teachPlanTreeNodes;
    }

    public TeachplanMedia getTeachplanMedia() {
        return teachplanMedia;
    }

    public void setTeachplanMedia(TeachplanMedia teachplanMedia) {
        this.teachplanMedia = teachplanMedia;
    }

    //小章节list
    private List<TeachplanDto> teachPlanTreeNodes;

    //与媒资关联的信息
    private TeachplanMedia teachplanMedia;
}

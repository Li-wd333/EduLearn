package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ToString
public class QueryCourseParamsDto implements Serializable {
 public QueryCourseParamsDto(String auditStatus, String courseName, String publishStatus) {
  this.auditStatus = auditStatus;
  this.courseName = courseName;
  this.publishStatus = publishStatus;
 }

 public QueryCourseParamsDto() {
 }

 //审核状态
 private String auditStatus;
 //课程名称
 private String courseName;
 //发布状态
 private String publishStatus;

 public String getAuditStatus() {
  return auditStatus;
 }

 public void setAuditStatus(String auditStatus) {
  this.auditStatus = auditStatus;
 }

 public String getCourseName() {
  return courseName;
 }

 public void setCourseName(String courseName) {
  this.courseName = courseName;
 }

 public String getPublishStatus() {
  return publishStatus;
 }

 public void setPublishStatus(String publishStatus) {
  this.publishStatus = publishStatus;
 }
}
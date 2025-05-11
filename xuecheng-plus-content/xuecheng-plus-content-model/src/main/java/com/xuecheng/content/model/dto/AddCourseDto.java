package com.xuecheng.content.model.dto;

import com.xuecheng.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * @description 添加课程dto
 * @author Mr.M
 * @date 2022/9/7 17:40
 * @version 1.0
 */

@ApiModel(value="AddCourseDto", description="新增课程基本信息")
public class AddCourseDto {
 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 public String getUsers() {
  return users;
 }

 public void setUsers(String users) {
  this.users = users;
 }

 public String getTags() {
  return tags;
 }

 public void setTags(String tags) {
  this.tags = tags;
 }

 public String getMt() {
  return mt;
 }

 public void setMt(String mt) {
  this.mt = mt;
 }

 public String getSt() {
  return st;
 }

 public void setSt(String st) {
  this.st = st;
 }

 public String getGrade() {
  return grade;
 }

 public void setGrade(String grade) {
  this.grade = grade;
 }

 public String getTeachmode() {
  return teachmode;
 }

 public void setTeachmode(String teachmode) {
  this.teachmode = teachmode;
 }

 public String getDescription() {
  return description;
 }

 public void setDescription(String description) {
  this.description = description;
 }

 public String getPic() {
  return pic;
 }

 public void setPic(String pic) {
  this.pic = pic;
 }

 public String getCharge() {
  return charge;
 }

 public void setCharge(String charge) {
  this.charge = charge;
 }

 public Float getPrice() {
  return price;
 }

 public void setPrice(Float price) {
  this.price = price;
 }

 public Float getOriginalPrice() {
  return originalPrice;
 }

 public void setOriginalPrice(Float originalPrice) {
  this.originalPrice = originalPrice;
 }

 public String getQq() {
  return qq;
 }

 public void setQq(String qq) {
  this.qq = qq;
 }

 public String getWechat() {
  return wechat;
 }

 public void setWechat(String wechat) {
  this.wechat = wechat;
 }

 public String getPhone() {
  return phone;
 }

 public void setPhone(String phone) {
  this.phone = phone;
 }

 public Integer getValidDays() {
  return validDays;
 }

 public void setValidDays(Integer validDays) {
  this.validDays = validDays;
 }
                                     //分组
 @NotEmpty(message = "新增课程名称不能为空",groups = {ValidationGroups.Inser.class}) // 分组
 @NotEmpty(message = "修改课程名称不能为空", groups = {ValidationGroups.Update.class})
 @ApiModelProperty(value = "课程名称", required = true)
 private String name;

 @NotEmpty(message = "适用人群不能为空")
 @Size(message = "适用人群内容过少",min = 10)
 @ApiModelProperty(value = "适用人群", required = true)
 private String users;

 @ApiModelProperty(value = "课程标签")
 private String tags;

 @NotEmpty(message = "课程分类不能为空")
 @ApiModelProperty(value = "大分类", required = true)
 private String mt;

 @NotEmpty(message = "课程分类不能为空")
 @ApiModelProperty(value = "小分类", required = true)
 private String st;

 @NotEmpty(message = "课程等级不能为空")
 @ApiModelProperty(value = "课程等级", required = true)
 private String grade;

 @ApiModelProperty(value = "教学模式（普通，录播，直播等）", required = true)
 private String teachmode;

 @ApiModelProperty(value = "课程介绍")
 private String description;

 @ApiModelProperty(value = "课程图片", required = true)
 private String pic;

 @NotEmpty(message = "收费规则不能为空")
 @ApiModelProperty(value = "收费规则，对应数据字典", required = true)
 private String charge;

 @ApiModelProperty(value = "价格")
 private Float price;
 @ApiModelProperty(value = "原价")
 private Float originalPrice;


 @ApiModelProperty(value = "qq")
 private String qq;

 @ApiModelProperty(value = "微信")
 private String wechat;
 @ApiModelProperty(value = "电话")
 private String phone;

 @ApiModelProperty(value = "有效期")
 private Integer validDays;
}

package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description 课程基本信息dto
 * @author Mr.M
 * @date 2022/9/7 17:44
 * @version 1.0
 */
public class CourseBaseInfoDto extends CourseBase {

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

 public String getMtName() {
  return mtName;
 }

 public void setMtName(String mtName) {
  this.mtName = mtName;
 }

 public String getStName() {
  return stName;
 }

 public void setStName(String stName) {
  this.stName = stName;
 }

 /**
  * 收费规则，对应数据字典
  */
 private String charge;

 /**
  * 价格
  */
 private Float price;


 /**
  * 原价
  */
 private Float originalPrice;

 /**
  * 咨询qq
  */
 private String qq;

 /**
  * 微信
  */
 private String wechat;

 /**
  * 电话
  */
 private String phone;

 /**
  * 有效期天数
  */
 private Integer validDays;

 /**
  * 大分类名称
  */
 private String mtName;

 /**
  * 小分类名称
  */
 private String stName;

}

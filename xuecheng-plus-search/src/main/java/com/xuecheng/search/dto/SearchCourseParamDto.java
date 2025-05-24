package com.xuecheng.search.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @description 搜索课程参数dtl
 * @author Mr.M
 * @date 2022/9/24 22:36
 * @version 1.0
 */

public class SearchCourseParamDto {
    public String getGrade() {
        return grade;
    }

    @Override
    public String toString() {
        return "SearchCourseParamDto{" +
                "keywords='" + keywords + '\'' +
                ", mt='" + mt + '\'' +
                ", st='" + st + '\'' +
                ", grade='" + grade + '\'' +
                '}';
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getMt() {
        return mt;
    }

    public void setMt(String mt) {
        this.mt = mt;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    //关键字
  private String keywords;

  //大分类
  private String mt;

  //小分类
  private String st;
  //难度等级
  private String grade;




}

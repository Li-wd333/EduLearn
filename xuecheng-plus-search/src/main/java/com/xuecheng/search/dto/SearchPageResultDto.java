package com.xuecheng.search.dto;

import com.xuecheng.base.model.PageResult;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/25 17:51
 */
public class SearchPageResultDto<T> extends PageResult {
    public List<String> getMtList() {
        return mtList;
    }

    @Override
    public String toString() {
        return "SearchPageResultDto{" +
                "mtList=" + mtList +
                ", stList=" + stList +
                '}';
    }

    public void setMtList(List<String> mtList) {
        this.mtList = mtList;
    }

    public List<String> getStList() {
        return stList;
    }

    public void setStList(List<String> stList) {
        this.stList = stList;
    }

    //大分类列表
    List<String> mtList;
    //小分类列表
    List<String> stList;

    public SearchPageResultDto(List<T> items, long counts, long page, long pageSize) {
        super(items, counts, page, pageSize);
    }

}

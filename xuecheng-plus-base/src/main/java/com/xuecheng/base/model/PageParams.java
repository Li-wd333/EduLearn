package com.xuecheng.base.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 分页查询分页参数
 * @author zcy
 */
@ApiModel(value = "PageParams", description = "分页查询参数")
@ToString
public class PageParams implements Serializable {
    //当前页码
    @ApiModelProperty("当前页码")
    private Long pageNo = 1L;
    //每页记录数
    @ApiModelProperty("每页记录数")
    private Long pageSize = 30L;

    public Long getPageNo() {
        return pageNo;
    }

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public PageParams() {
    }

    public PageParams(Long pageSize, Long pageNo) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }
}

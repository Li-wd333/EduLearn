package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.context.annotation.Bean;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

   /**
    * @description 媒资文件查询方法
    * @param pageParams 分页参数
    * @param queryMediaParamsDto 查询条件
    * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
    * @author Mr.M
    * @date 2022/9/10 8:57
   */
   public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件
     * @return
     */
     UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);

    /**
     * 查询分块
     * @param fileMd5
     * @param chunkIndex
     * @return
     */
     public RestResponse<Boolean> checkChunk(String fileMd5,int chunkIndex);

    /**
     * 查询文件
     * @param fileMd5
     * @return
     */
    public RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * 上传分块文件
     * @param fileMd5
     * @param chunk
     * @param localChunkFilePath
     * @return
     */
    public RestResponse uploadChunk(String fileMd5,int chunk,String localChunkFilePath);

    /**
     * 合并分块文件
     * @param companyId
     * @param fileMd5
     * @param chunkTotal
     * @param uploadFileParamsDto
     * @return
     */
    public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);
}

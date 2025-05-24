package com.xuecheng.media.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @description 媒资文件管理接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
 @Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
 @RestController
public class MediaFilesController {
      @Autowired
      MediaFileService mediaFileService;

     @ApiOperation("媒资列表查询接口")
     @PostMapping("/files")
     public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
      Long companyId = 1232141425L;
      return mediaFileService.queryMediaFiels(companyId,pageParams,queryMediaParamsDto);
     }

    @ApiOperation("上传文件")   // @RequestPart 获取上传的文件
    @PostMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile filedata,
    @RequestParam(value= "objectName",required=false) String objectName) throws IOException {
        Long companyId = 1232141425L;
         //准备文件上传的信息
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        //原始文件名称
        String filename = filedata.getOriginalFilename();  // 获取原始文件名称
        uploadFileParamsDto.setFilename(filename);
        //文件大小
        uploadFileParamsDto.setFileSize(filedata.getSize());
        //文件类型
        uploadFileParamsDto.setFileType("001001");
        //接收到一个文件
        //创建一个临时文件
        File tempFile = File.createTempFile("minio", ".temp");
        filedata.transferTo(tempFile); // 将文件写入临时文件
        //取他的文件路径
        String localFilePath = tempFile.getAbsolutePath();  // 获取临时文件的绝对路径
        //上传图片到minio
        UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId,uploadFileParamsDto,localFilePath,objectName);
        //返回值
        return uploadFileResultDto;
    }
}

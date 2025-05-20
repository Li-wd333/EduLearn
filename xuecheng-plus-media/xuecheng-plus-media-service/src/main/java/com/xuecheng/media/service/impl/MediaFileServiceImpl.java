package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr.M
 * @version 1.0
 */
 @Service
public class MediaFileServiceImpl implements MediaFileService {

 private static final Logger log = LogManager.getLogger(MediaFileServiceImpl.class);
 @Autowired
 MediaFilesMapper mediaFilesMapper;
 @Autowired
 private MinioClient minioClient;
 @Autowired
 private MediaProcessMapper mediaProcessMapper;
 @Autowired
 private MediaFileServiceImpl currentProxy;
    @Value("${minio.bucket.files}")
    private String files;
    @Value("${minio.bucket.videofiles}")
    private String videofiles;
 @Override
 public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

  //构建查询条件对象
  LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
  
  //分页对象
  Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
  // 查询数据内容获得结果
  Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
  // 获取数据列表
  List<MediaFiles> list = pageResult.getRecords();
  // 获取数据总数
  long total = pageResult.getTotal();
  // 构建结果集
  PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
  return mediaListResult;

 }
 //根据扩展名来获取媒体资源类型
    private String getMimeType(String extension) {
        if (extension == null)
            extension = "";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }
  //将文件上传到minio
    @Override
    public boolean addMediaFilesToMinio(String mimeType, String bucket, String objectName, String localFilePath){
  //上传文件的参数信息
     UploadObjectArgs testbucket = null;
     try {
         testbucket = UploadObjectArgs.builder()
                 .bucket(bucket) //存储桶名称
                 .filename(localFilePath) //本地文件路径
                 .object(objectName)   //文件(对象)名称
                 .contentType(mimeType)  //文件类型
                 .build();
         //上传文件
         minioClient.uploadObject(testbucket);
         log.debug("上传文件成功,bucket:{},objectName:{}",  bucket,objectName);
         return true;
     } catch (Exception e) {
         e.printStackTrace();
         log.error("上传文件失败,bucket:{},objectName:{},错误信息：{}",  bucket,objectName,e.getMessage());

     }
  return false;

 }
    //获取文件默认存储目录路径 年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date()).replace("-", "/")+"/";
    }
 @Override
 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
     //上传
     //获取文件名
     String filename = uploadFileParamsDto.getFilename();
     //获取文件后缀名
     String extension = filename.substring(filename.lastIndexOf("."));
     //获取媒体资源类型
     String mediaType = getMimeType(extension);
     String defaultFolderPath = getDefaultFolderPath(); //默认目录
     //文件的md5值
     String fileMd5 = getFileMd5(new File(localFilePath));
     String objectName = defaultFolderPath+fileMd5+extension;
     //文件上传到minio
     boolean result = addMediaFilesToMinio(mediaType, files, objectName, localFilePath);
     if (!result){
         XueChengPlusException.cast("上传文件失败");
     }
     //将文件信息保存到数据库中
     MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, files, objectName);
     if (mediaFiles == null){
         XueChengPlusException.cast("文件上传后保存文件失败！");
     }
     //准备返回对象
     UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
     //属性拷贝
     BeanUtils.copyProperties(mediaFiles,uploadFileResultDto);
     //返回结果
     return uploadFileResultDto;
 }

    /**
     * 查询分块
     * @param fileMd5
     * @param chunkIndex
     * @return
     */
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        //分块文件的存储路径 md5值的前两位为其的二级目录 + md5值
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5)+chunkIndex;

            //数据库存在该文件
            //构建查询参数
            GetObjectArgs testbucket = GetObjectArgs.builder()
                    .bucket(videofiles) //存储桶名称
                    .object(chunkFileFolderPath)   //文件(对象)名称
                    .build();
            //获取文件流  查询远程服务器获取到的文件流
            try {
                FilterInputStream inputStream = minioClient.getObject(testbucket);
                if(inputStream!=null){
                    //文件存在
                    return RestResponse.success(true);
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        //文件不存在
        return RestResponse.success(false);
    }
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        //查询文件信息
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //桶
            String bucket = mediaFiles.getBucket();
            //存储目录
            String filePath = mediaFiles.getFilePath();
            //文件流
            InputStream stream = null;
            try {
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build());

                if (stream != null) {
                    //文件已存在
                    return RestResponse.success(true);
                }
            } catch (Exception e) {

            }
        }
        //文件不存在
        return RestResponse.success(false);
    }
    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {

        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;
        //mimeType
        String mimeType = getMimeType(null);
        //将文件存储至minIO
        boolean b = addMediaFilesToMinio(mimeType, videofiles,chunkFilePath,localChunkFilePath);
        if (!b) {
            log.debug("上传分块文件失败:{}", chunkFilePath);
            return RestResponse.validfail(false, "上传分块失败");
        }
        log.debug("上传分块文件成功:{}",chunkFilePath);
        return RestResponse.success(true);

    }

    /**
     * 合并分块文件
     * @param companyId
     * @param fileMd5
     * @param chunkTotal
     * @param uploadFileParamsDto
     * @return
     */
    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        //=====获取分块文件路径=====
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //组成将分块文件路径组成 List<ComposeSource>
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i ->
                        ComposeSource.builder()
                        .bucket(videofiles)
                        .object(chunkFileFolderPath+i) //分块文件路径
                        .build())
                .collect(Collectors.toList());
        //=====合并=====
        //文件名称
        String fileName = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extName = fileName.substring(fileName.lastIndexOf("."));
        //合并文件路径
        String mergeFilePath = getFilePathByMd5(fileMd5, extName);
        try {
            //合并文件
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(videofiles) //存储桶名称
                            .object(mergeFilePath) //合并后的文件名称
                            .sources(sourceObjectList) //分块文件路径集合
                            .build());
            log.debug("合并文件成功:{}",mergeFilePath);
        } catch (Exception e) {
            log.debug("合并文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage());
            return RestResponse.validfail(false, "合并文件失败。");
        }

        // ====验证md5====
        //下载合并后的文件
        File minioFile = downloadFileFromMinIO(videofiles,mergeFilePath);
        if(minioFile == null){
            log.debug("下载合并后文件失败,mergeFilePath:{}",mergeFilePath);
            return RestResponse.validfail(false, "下载合并后文件失败。");
        }

        try (
                InputStream newFileInputStream = new FileInputStream(minioFile)
        ) {
            //minio上文件的md5值
            String md5Hex = DigestUtils.md5DigestAsHex(newFileInputStream);
            //比较md5值，不一致则说明文件不完整
            if(!fileMd5.equals(md5Hex)){
                return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
            }
            //文件大小
            uploadFileParamsDto.setFileSize(minioFile.length());
        }catch (Exception e){
            log.debug("校验文件失败,fileMd5:{},异常:{}",fileMd5,e.getMessage(),e);
            return RestResponse.validfail(false, "文件合并校验失败，最终上传失败。");
        }finally {
            if(minioFile != null){
                //删除临时文件
                minioFile.delete();
            }
        }

        //文件入库   currentProxy 是代理对象，调用代理对象的方法，实际调用的是currentProxy.addMediaFilesToDb()
        currentProxy.addMediaFilesToDb(companyId,fileMd5,uploadFileParamsDto,videofiles,mergeFilePath);
        //=====清除分块文件=====
        clearChunkFiles(chunkFileFolderPath,chunkTotal);
        return RestResponse.success(true);
    }
    /**
     * 清除分块文件
     * @param chunkFileFolderPath 分块文件路径
     * @param chunkTotal 分块文件总数
     */
    private void clearChunkFiles(String chunkFileFolderPath,int chunkTotal) {
        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs  //RemoveObjectsArgs
                    .builder()
                    .bucket("video")
                    .objects(deleteObjects)
                    .build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r -> {
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清楚分块文件失败,objectname:{}", deleteError.objectName(), e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清楚分块文件失败,chunkFileFolderPath:{}", chunkFileFolderPath, e);
        }
    }
    /**
     * 从minio下载文件
     * @param bucket 桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    public File downloadFileFromMinIO(String bucket,String objectName){
        //临时文件
        File minioFile = null;
        FileOutputStream outputStream = null;
        try{
            InputStream stream = minioClient.getObject(
                    GetObjectArgs
                            .builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
            //创建临时文件
            minioFile=File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream,outputStream);//将文件内容写入临时文件
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 查询媒资文件信息
     * @param mediaId
     * @return
     */
    @Override
    public MediaFiles getFileById(String mediaId) {
        return mediaFilesMapper.selectById(mediaId);
    }

    /**
     * 得到合并后的文件的地址
     * @param fileMd5 文件id即md5值
     * @param fileExt 文件扩展名
     * @return
     */
    private String getFilePathByMd5(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }
    //得到分块文件的目录
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    /**
     * @description 将文件信息添加到文件表
     * @param companyId  机构id
     * @param fileMd5  文件md5值
     * @param uploadFileParamsDto  上传文件的信息
     * @param bucket  桶
     * @param objectName 对象名称
     * @return com.xuecheng.media.model.po.MediaFiles
     * @author Mr.M
     */
    @Transactional //添加事务 Propagation Propagation.REQUIRES_NEW 新启一个事务 rollbackFor = Exception.class
    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName){
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert <= 0) {
                log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
//                XueChengPlusException.cast("保存文件信息失败");
                return null;
            }
            log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());
            //记录处理任务
            addWaitingTask(mediaFiles);
            return mediaFiles;
        }
        return mediaFiles;
    }
    /**
     * 添加待处理任务
     * @param mediaFiles 媒资文件信息
     */
    private void addWaitingTask(MediaFiles mediaFiles){
        //获取文件的mimetype
        String filename = mediaFiles.getFilename();
        //获取扩展名
        String fileExt = filename.substring(filename.lastIndexOf("."));
        //获取mimeType
        String mimeType = getMimeType(fileExt);
        //通过文件mimetype判断是avi视频还是其他文件 是写入待处理任务
        if("video/x-msvideo".equals(mimeType)){
            MediaProcess mediaProcess = new MediaProcess();
            BeanUtils.copyProperties(mediaFiles,mediaProcess);
            mediaProcess.setStatus("1");
            mediaProcess.setCreateDate(LocalDateTime.now());
            mediaProcess.setUrl(null);
            mediaProcess.setFailCount(0); //失败次数
            //插入到media_process表
            mediaProcessMapper.insert(mediaProcess);
        }
    }

    /**
     * 获取文件md5
     * @param file
     * @return
     */
    private String getFileMd5(File file) {
        FileInputStream  fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            return DigestUtils.md5DigestAsHex(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

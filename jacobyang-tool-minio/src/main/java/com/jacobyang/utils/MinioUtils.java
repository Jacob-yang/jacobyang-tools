package com.jacobyang.utils;/**
 * @Author: JacobYang
 * @Date: 2021/9/3 09:31
 * @Description:
 */

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jacobyang.config.MinioProperties;
import com.jacobyang.domain.MinioFile;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: MinioUtils
 * @Description: TODO
 * @Author: JacobYang
 * @Date: 2021/9/3 09:31
 * @Version: 1.0
 */
public class MinioUtils {
    Logger log = LoggerFactory.getLogger(MinioUtils.class);
    private MinioClient minioClient;
    private String baseBucket;
    private String baseOpenUrl;

    public MinioUtils(MinioProperties minioProperties) {
        try {
            baseBucket = minioProperties.getBucket();
            baseOpenUrl = minioProperties.getOpenUrl();
            initMinioClient(minioProperties);
            checkBucketName(baseBucket);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("MinioClient初始化异常");
        }
    }
    //是否存在库,不存在创建
    private void checkBucketName(String bucketName) {
        try {
            boolean found = minioClient.bucketExists(bucketName);
            if (!found) {
                log.info("Bucket {} doesn't exist, create",bucketName);
                minioClient.makeBucket(bucketName);
            }
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
    /**
     * 初始化客户端
     * @param minioProperties
     */
    private void initMinioClient(MinioProperties minioProperties) throws InvalidPortException, InvalidEndpointException {
        minioClient = new MinioClient(minioProperties.getUrl(), minioProperties.getAccessKey(), minioProperties.getSecretKey());
    }

    public String uploadFile(InputStream stream, String bucket, String objectKey, boolean flag) {
        String bucketName = StrUtil.isEmpty(bucket)?baseBucket:bucket; //桶
        String fileUrl = null;
        try {
            checkBucketName(bucketName);//是否需要创建桶
            String picName = UploadUtil.generateFileName(objectKey);//生成时间命名的图片
            String objectName = UploadUtil.getUploadPath()+"/"+picName;
            PutObjectOptions options = new PutObjectOptions(stream.available(), -1);
            options.setContentType(UploadUtil.getContentType(UploadUtil.getFileExtension(objectKey)));
            minioClient.putObject(bucketName, objectName, stream, options);
            fileUrl = getUrl(bucketName, objectName);
            if(flag){//true 是否需要缩略图
                InputStream miniInputStream = minioClient.getObject(bucketName, objectName);
                String objMiniName = UploadUtil.getUploadPath()+"/mini_"+picName;
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImgUtil.scale(miniInputStream,os,0.5f);
                byte[] bytes = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(bytes);
                PutObjectOptions optionsObj = new PutObjectOptions(bytes.length, -1);
                optionsObj.setContentType(UploadUtil.getContentType(UploadUtil.getFileExtension(picName)));
                minioClient.putObject(bucketName, objMiniName, inputStream, optionsObj);
                inputStream.close();
                os.close();
                miniInputStream.close();
            }
            log.info("fileUrl:{}",fileUrl);
        } catch (ErrorResponseException |InsufficientDataException |InternalException |InvalidBucketNameException
                | InvalidKeyException |InvalidResponseException |IOException |NoSuchAlgorithmException |XmlParserException e) {
            e.printStackTrace();
        }finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileUrl;
    }

    public boolean removeFile(String url) {
        if(StrUtil.isEmpty(url)){
            return false;
        }
        MinioFile minioFile = getMinioFile(url);
        if(ObjectUtil.isNull(minioFile)){
            return false;
        }
        String bucket = minioFile.getBucket();
        String objectKey = minioFile.getObjectName();
        try {
            minioClient.removeObject(StrUtil.isEmpty(bucket)?baseBucket:bucket,objectKey);
            int i2 = objectKey.lastIndexOf("/");
            String miniFront = objectKey.substring(0, i2);
            String miniAfter = objectKey.substring(i2 + 1);
            String miniFile = miniFront + "/mini_" + miniAfter;
            minioClient.removeObject(StrUtil.isEmpty(bucket)?baseBucket:bucket,miniFile);
            return true;
        } catch (ErrorResponseException |InsufficientDataException |InternalException |InvalidBucketNameException
                |InvalidKeyException |InvalidResponseException|IOException|NoSuchAlgorithmException|XmlParserException e) {
            e.printStackTrace();
            log.error("error: {}", e.getMessage(), e);
        }
        return false;
    }

    public void downloadFile(OutputStream responseOutputStream, String url) {
        if(StrUtil.isEmpty(url)){
            log.error("url is null");
            return;
        }
        InputStream inputStream = null;
        MinioFile minioFile = getMinioFile(url);
        try {
            if(ObjectUtil.isNotNull(minioFile)){
                String bucket= minioFile.getBucket();
                inputStream = minioClient.getObject(StrUtil.isEmpty(bucket)?baseBucket:bucket,minioFile.getObjectName());
                IoUtil.copy(inputStream,responseOutputStream);
            }
            log.error("minioFile is null");
        } catch (ErrorResponseException | InsufficientDataException |InternalException|InvalidBucketNameException
                |InvalidKeyException|InvalidResponseException| IOException | NoSuchAlgorithmException |XmlParserException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    //获取上传后的文件
    private String getUrl(String bucketName, String fileName){
        String objectUrl = null;
        try {
            objectUrl = minioClient.getObjectUrl(bucketName, fileName);
            String url = baseOpenUrl; //开放的图片路径
            String file = objectUrl.substring(objectUrl.indexOf(bucketName));
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(url);
            stringBuffer.append("/");
            stringBuffer.append(file);
            objectUrl = stringBuffer.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return objectUrl;
    }
    //从url中获取桶和文件
    private MinioFile getMinioFile(String url){
        //获取http:// 或 https:// 后面第一个“/”后的路径
        int i = url.indexOf("/",8);
        String substring = url.substring(i+1);
        int i1 = substring.indexOf("/");
        //获取桶
        String bucket = substring.substring(0, i1);
        //获取存储的文件
        String objectKey = substring.substring(i1 + 1);
        return new MinioFile(bucket,objectKey);
    }

}

package com.jacobyang.domain;/**
 * @Author: JacobYang
 * @Date: 2021/9/2 09:28
 * @Description:
 */

/**
 * @ClassName: MinioFile
 * @Description: TODO
 * @Author: JacobYang
 * @Date: 2021/9/2 09:28
 * @Version: 1.0
 */
public class MinioFile {

    private String bucket;
    //获取存储的文件
    private String objectName;

    public MinioFile(String bucket, String objectName) {
        this.bucket = bucket;
        this.objectName = objectName;
    }

    public String getBucket() {
        return bucket;
    }

    public String getObjectName() {
        return objectName;
    }

}

package com.jacobyang.utils;/**
 * @Author: JacobYang
 * @Date: 2021/8/30 15:12
 * @Description:
 */

import cn.hutool.core.util.StrUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @ClassName: UploadUtil
 * @Description: TODO
 * @Author: JacobYang
 * @Date: 2021/8/30 15:12
 * @Version: 1.0
 */
public class UploadUtil {
    /**
     * 传入原图名称，，获得一个以时间格式的新名称
     * @param fileName 原图名称
     * @return yyyyMMddHHmmss0000.jpg/png/...
     */

    public static String generateFileName(String fileName) {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String formatDate = format.format(new Date());
        int random = new Random().nextInt(10000);
        int position = fileName.lastIndexOf('.');
        String extension = fileName.substring(position);
        return formatDate + random + extension;
    }

    /**
     * 获取上传路径
     * @return String yyyy/MM
     */
    public static String getUploadPath() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = (calendar.get(Calendar.MONTH) + 1);
        String path = year + "/" + month;
        return path;
    }

    /**
     * 获取文件扩展名(后缀)
     * @param filename
     * @return
     */
    public static String getFileExtension(String filename) {
        if (StrUtil.isNotEmpty(filename)) {
            String string = filename.trim();
            int index = filename.lastIndexOf(".");
            if (index > 0 && index < string.length() - 1) {
                return string.substring(index + 1);
            }
        }
        return null;
    }

    public static String getContentType(String fileExtension) {
        //文件的后缀名
        if ("bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if ("gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if ("jpeg".equalsIgnoreCase(fileExtension) || "jpg".equalsIgnoreCase(fileExtension) || "png".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        }
        if ("html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if ("txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if ("vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if ("ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if ("doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if ("xls".equalsIgnoreCase(fileExtension) || "xlsx".equalsIgnoreCase(fileExtension)) {
            return "application/msexcel";
        }
        if ("csv".equalsIgnoreCase(fileExtension)) {
            return "application/csv";
        }
        if ("xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        if ("mp4".equalsIgnoreCase(fileExtension)) {
            return "video/mp4";
        }
        if ("avi".equalsIgnoreCase(fileExtension)) {
            return "video/x-msvideo";
        }
        if ("mov".equalsIgnoreCase(fileExtension)) {
            return "video/quicktime";
        }
        if ("mpeg".equalsIgnoreCase(fileExtension) || "mpg".equalsIgnoreCase(fileExtension)) {
            return "video/mpeg";
        }
        if ("wm".equalsIgnoreCase(fileExtension)) {
            return "video/x-ms-wmv";
        }
        if ("flv".equalsIgnoreCase(fileExtension)) {
            return "video/x-flv";
        }
        if ("mkv".equalsIgnoreCase(fileExtension)) {
            return "video/x-matroska";
        }
        //默认返回类型
        return "video/x-msvideo";

    }

}

package com.jacobyang.config;/**
 * @Author: JacobYang
 * @Date: 2021/9/3 09:24
 * @Description:
 */

import com.jacobyang.utils.MinioUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName: MinioAutoConfiguration
 * @Description: TODO
 * @Author: JacobYang
 * @Date: 2021/9/3 09:24
 * @Version: 1.0
 */
@EnableConfigurationProperties(MinioProperties.class)
public class MinioAutoConfiguration {

    private MinioProperties minioProperties;
    // 通过构造方法将MinioProperties注入进来
    public MinioAutoConfiguration(MinioProperties minioProperties) {
        notNull(minioProperties.getUrl() == null, "url不能为空");
        notNull(minioProperties.getAccessKey() == null, "accessKey不能为空");
        notNull(minioProperties.getSecretKey() == null, "secretKey不能为空");
        notNull(minioProperties.getOpenUrl() == null, "openUrl不能为空");
        notNull(minioProperties.getBucket() == null, "bucket不能为空");
        this.minioProperties = minioProperties;
    }

    @ConditionalOnMissingBean(MinioUtils.class)
    @Bean
    public MinioUtils minioUtils(){
        return new MinioUtils(minioProperties);
    }

    public static void notNull(boolean expression, String msg){
        if(expression){
            throw new NullPointerException(msg);
        }
    }

}

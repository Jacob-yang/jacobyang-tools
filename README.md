# jacobyang-tools
## jacobyang-tool-minio
> 使用步骤
  1. 在resources下创建lib文件夹
  2. 把jacobyang-tool-minio.jar放入
  3. 在pom.xml中配置 
        ```
            <dependency>
                <groupId>com.jacobyang</groupId>
                <artifactId>jacobyang-tool-minio</artifactId>
                <version>1.0</version>
                <scope>system</scope>
                <systemPath>${project.basedir}/src/main/resources/lib/jacobyang-tool-minio.jar</systemPath>
            </dependency>
        ``` 
  4. application.yml
     ```yml
        minio:
          url: http://127.0.0.1:9000
          accessKey: minioadmin
          secretKey: minioadmin
          openUrl: http://127.0.0.1:9000
          bucket: pic
        ```      
  5. controller使用
     ```java
        @RestController
        @RequestMapping("/api/upload")
        public class MinioApi {
        
            @Autowired
            private MinioUtils minioUtils;
        
            @PostMapping(value = "/img")
            public Object upload(MultipartHttpServletRequest req) {
                Map<String, MultipartFile> fileMap = req.getFileMap();
                // 循环遍历，取出单个文件
                List<String> list = new ArrayList<>();
                for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
                    // 获取单个文件
                    MultipartFile file = entity.getValue();
                    try {
                         //文件流,桶（已经在yml中配置）,文件名,false不生成缩略图/true生成缩略图
                        String s = minioUtils.uploadFile(file.getInputStream(), null, file.getOriginalFilename(), false);
                        list.add(s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return list;
            }     
        
            @PostMapping(value = "/del")
            public Object delFile(String url) {
                return minioUtils.removeFile(url);
            }
        
            @GetMapping(value = "/download")
            public void download(HttpServletResponse response, String url) {
                minioUtils.downloadFile(response,url);
            }
       }
        ```
        
## jacobyang-tool-es
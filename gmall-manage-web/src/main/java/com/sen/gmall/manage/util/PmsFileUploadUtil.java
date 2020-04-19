package com.sen.gmall.manage.util;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Sen
 * @Date: 2019/11/3 14:29
 * @Description: 文件上传FastDFS工具类
 */
public class PmsFileUploadUtil {

    public static String upLoadImage(MultipartFile multipartFile) {

        StringBuilder url = null;

        //获取配置文件的路径
        String path = PmsFileUploadUtil.class.getResource("/tracker.conf").getPath();
        try {
            //加载配置文件
            ClientGlobal.init(path);
            //获取trackerClient
            TrackerClient trackerClient = new TrackerClient();
            //获取trackerServer实例
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storage链接客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);

            //获取上传文件的后缀名
            String originalFilename = multipartFile.getOriginalFilename();
            int index = originalFilename.lastIndexOf(".");
            String extName = originalFilename.substring(index + 1);

            //上传文件
            String[] strings = storageClient.upload_appender_file(
                    multipartFile.getBytes(),
                    extName, null);

            //拼接图片访问url
            url = new StringBuilder("http://192.168.161.141");
            for (String string : strings) {
                url.append("/").append(string);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert url != null;
        return url.toString();
    }
}

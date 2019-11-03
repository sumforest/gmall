package com.sen.gmall.manage;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.IOException;

/**
 * @Auther: Sen
 * @Date: 2019/11/3 13:47
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageWebApplicationTest {

    @Test
    public void fileUploadTest() throws IOException, MyException {
        //获取配置文件的路径
        String path = GmallManageWebApplicationTest.class.getResource("/tracker.conf").getPath();
        //加载配置文件
        ClientGlobal.init(path);
        //获取trackerClient
        TrackerClient trackerClient = new TrackerClient();
        //获取trackerServer实例
        TrackerServer trackerServer = trackerClient.getConnection();
        //获取storage链接客户端
        StorageClient storageClient = new StorageClient(trackerServer, null);

        //上传文件
        String[] strings = storageClient.upload_appender_file(
                "C:\\Users\\Sen\\OneDrive\\图片\\Saved Pictures\\c6824cbcf18d8e02.jpg",
                "jpg", null);

        //拼接图片访问url
        StringBuilder url = new StringBuilder("http://192.168.161.141");
        for (String string : strings) {
            url.append("/").append(string);
        }
        System.out.println(url);
    }
}

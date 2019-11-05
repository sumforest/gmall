package com.sen.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sen.gmall.api.beans.PmsSearchSkuInfo;
import com.sen.gmall.api.beans.PmsSkuInfo;
import com.sen.gmall.api.service.PmsSkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/6 01:27
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchServiceApplicationTest {

    @Reference
    private PmsSkuService skuService;

    @Autowired
    private JestClient jestClient;

    @Test
    public void addDataToES() throws IOException {
        //查询MySQL数据库
        List<PmsSkuInfo> skuInfos = skuService.getAll();

        //用BeanUtil工具包封装ES对象
        List<PmsSearchSkuInfo> searchSkuInfos = new ArrayList<>();
        for (PmsSkuInfo skuInfo : skuInfos) {

            PmsSearchSkuInfo searchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(skuInfo, searchSkuInfo);
            searchSkuInfos.add(searchSkuInfo);
        }

        //导入ES
        for (PmsSearchSkuInfo searchSkuInfo : searchSkuInfos) {
            Index put = new Index.Builder(searchSkuInfo).
                    index("gmall").type("PmsSkuInfo").id(searchSkuInfo.getId()).build();
            jestClient.execute(put);
        }
    }
}

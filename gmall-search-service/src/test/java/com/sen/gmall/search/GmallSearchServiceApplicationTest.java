package com.sen.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sen.gmal.api.beans.PmsSearchSkuInfo;
import com.sen.gmal.api.beans.PmsSkuInfo;
import com.sen.gmal.api.service.PmsSkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
 * @Author: Sen
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
    public void search() throws IOException {
        //用api从es中查询结果
        List<PmsSearchSkuInfo> searchSkuInfos = new ArrayList<>();

        //用DSL语句封装查询语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //先查询后过滤
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", "39");
        boolQueryBuilder.filter(termQueryBuilder);
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", "华为");
        boolQueryBuilder.must(matchQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        //设置分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        String dsl = searchSourceBuilder.toString();
        System.out.println(dsl);

        Search search = new Search.Builder(dsl).addIndex("gmall").addType("PmsSkuInfo").build();

        SearchResult execute = jestClient.execute(search);
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            searchSkuInfos.add(source);
        }

        System.out.println(searchSkuInfos);
    }

    @Test
    public void addDataToES() throws IOException {
        //查询MySQL数据库
        List<PmsSkuInfo> skuInfos = skuService.getAll();

        //用BeanUtil工具包封装ES对象
        List<PmsSearchSkuInfo> searchSkuInfos = new ArrayList<>();
        for (PmsSkuInfo skuInfo : skuInfos) {

            PmsSearchSkuInfo searchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(skuInfo, searchSkuInfo);
            searchSkuInfo.setId(Long.parseLong(skuInfo.getId()));
            searchSkuInfos.add(searchSkuInfo);
        }

        //导入ES
        for (PmsSearchSkuInfo searchSkuInfo : searchSkuInfos) {
            Index put = new Index
                    .Builder(searchSkuInfo)
                    .index("gmall")
                    .type("PmsSkuInfo")
                    .id(searchSkuInfo.getId() + "")
                    .build();
            jestClient.execute(put);
        }
    }
}

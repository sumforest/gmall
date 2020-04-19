package com.sen.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sen.gmal.api.beans.PmsSearchParam;
import com.sen.gmal.api.beans.PmsSearchSkuInfo;
import com.sen.gmal.api.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Sen
 * @Date: 2019/11/6 18:46
 * @Description:
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> searchPmsSkuInfo(PmsSearchParam pmsSearchParam) {

        //用api从es中查询结果
        List<PmsSearchSkuInfo> searchSkuInfos = new ArrayList<>();

        //用DSL语句封装查询语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //先查询后过滤
        //根据平台属性过滤过滤
        String[] skuAttrValueList = pmsSearchParam.getValueId();
        if (skuAttrValueList != null) {

            for (String pmsSkuAttrValue : skuAttrValueList) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", pmsSkuAttrValue);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        //通过三级分类id过滤
        if (StringUtils.isNotBlank(pmsSearchParam.getCatalog3Id())) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", pmsSearchParam.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }

        //搜索
        if (StringUtils.isNotBlank(pmsSearchParam.getKeyword())) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", pmsSearchParam.getKeyword());
            boolQueryBuilder.must(matchQueryBuilder);
        }
        //搜索
        searchSourceBuilder.query(boolQueryBuilder);

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red;'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlight(highlightBuilder);

        //排序
        searchSourceBuilder.sort("id", SortOrder.DESC);

        //设置分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        String dsl = searchSourceBuilder.toString();
        System.out.println(dsl);

        Search search = new Search.Builder(dsl).addIndex("gmall").addType("PmsSkuInfo").build();

        SearchResult execute = null;
        try {
            execute = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (execute != null) {

            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                PmsSearchSkuInfo source = hit.source;
                if (hit.highlight != null) {
                    List<String> list = hit.highlight.get("skuName");
                    source.setSkuName(list.get(0));
                }
                searchSkuInfos.add(source);
            }
        }

        return searchSkuInfos;
    }
}

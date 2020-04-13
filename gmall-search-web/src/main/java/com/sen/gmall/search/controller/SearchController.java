package com.sen.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sen.gmal.api.beans.*;
import com.sen.gmal.api.service.PmsBaseAttrService;
import com.sen.gmal.api.service.SearchService;
import com.sen.gmall.web.annotations.LoginRequire;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/6 17:45
 * @Description: 搜索功能
 */
@Controller
public class SearchController {

    @Reference
    private SearchService searchService;

    @Reference
    private PmsBaseAttrService attrService;

    @LoginRequire(loginSuccess = false)
    @GetMapping(value = {"", "index"})
    public String showIndex() {
        return "index";
    }

    @GetMapping("list.html")
    public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap) {

        //返回搜索到的sku
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.searchPmsSkuInfo(pmsSearchParam);
        modelMap.put("skuLsInfoList", pmsSearchSkuInfos);

        //返回搜索到的sku的平台属性
        List<PmsBaseAttrInfo> skuBaseAttrInfos = attrService.getSearchAttrAndAttrValues(pmsSearchSkuInfos);
        modelMap.put("attrList", skuBaseAttrInfos);

        //删除已选择的的平台属性
        String[] delValueIds = pmsSearchParam.getValueId();
        if (delValueIds != null) {

            //制作面包屑
            List<PmsSearchCrumb> crumbs = new ArrayList<>();
            for (String delValueId : delValueIds) {

                Iterator<PmsBaseAttrInfo> iterator = skuBaseAttrInfos.iterator();
                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                pmsSearchCrumb.setUrlParam(getUrlParam(pmsSearchParam,delValueId));
                pmsSearchCrumb.setValueId(delValueId);

                while (iterator.hasNext()) {
                    PmsBaseAttrInfo next = iterator.next();

                    for (PmsBaseAttrValue attrValue : next.getAttrValueList()) {

                        if (delValueId.equals(attrValue.getId())) {
                            //设置面包屑的平台属性值
                            pmsSearchCrumb.setValueName(attrValue.getValueName());
                            iterator.remove();
                        }
                    }
                }
                crumbs.add(pmsSearchCrumb);
            }
            //返回面包屑
            modelMap.put("attrValueSelectedList", crumbs);

        }
        //返回关键字
        if (StringUtils.isNotBlank(pmsSearchParam.getKeyword())) {
            modelMap.put("keyword", pmsSearchParam.getKeyword());
        }

        //封装urlParam
        String url = getUrlParam(pmsSearchParam);
        modelMap.put("urlParam", url);
        return "list";
    }

    /**
     * 创建面包屑url
     *
     * @param pmsSearchParam 搜索参数
     * @param delValueIds 点击删除的面包屑id
     * @return url
     */
    private String getUrlParam(PmsSearchParam pmsSearchParam,String ...delValueIds) {

        StringBuilder stringBuilder = new StringBuilder();

        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] skuAttrValueList = pmsSearchParam.getValueId();

        if (StringUtils.isNotBlank(catalog3Id)) {
            if (StringUtils.isBlank(stringBuilder.toString())) {
                stringBuilder.append("catalog3Id=").append(catalog3Id);
            } else {

                stringBuilder.append("&catalog3Id=").append(catalog3Id);
            }
        }

        if (StringUtils.isNotBlank(keyword)) {

            if (StringUtils.isBlank(stringBuilder.toString())) {
                stringBuilder.append("keyword=").append(keyword);
            } else {
                stringBuilder.append("&keyword=").append(keyword);
            }
        }

        if (skuAttrValueList != null) {
                for (String s : skuAttrValueList) {
                    if (delValueIds != null && delValueIds.length > 0) {
                        for (String delValueId : delValueIds) {
                            if (!s.equals(delValueId)) {
                                stringBuilder.append("&valueId=").append(s);
                            }
                        }
                    } else {
                        stringBuilder.append("&valueId=").append(s);
                    }
                }
        }

        return stringBuilder.toString();
    }
}

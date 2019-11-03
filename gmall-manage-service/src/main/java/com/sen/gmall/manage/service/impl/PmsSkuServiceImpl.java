package com.sen.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sen.gmall.api.beans.PmsSkuAttrValue;
import com.sen.gmall.api.beans.PmsSkuImage;
import com.sen.gmall.api.beans.PmsSkuInfo;
import com.sen.gmall.api.beans.PmsSkuSaleAttrValue;
import com.sen.gmall.api.service.PmsSkuService;
import com.sen.gmall.manage.mapper.PmsSkuAttrMapper;
import com.sen.gmall.manage.mapper.PmsSkuImageMapper;
import com.sen.gmall.manage.mapper.PmsSkuInfoMapper;
import com.sen.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/3 18:45
 * @Description:
 */
@Service
public class PmsSkuServiceImpl implements PmsSkuService {

    @Autowired
    private PmsSkuInfoMapper skuInfoMapper;

    @Autowired
    private PmsSkuImageMapper skuImageMapper;

    @Autowired
    private PmsSkuAttrMapper skuAttrMapper;

    @Autowired
    private PmsSkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        //保存PmsSkuInfo,获取期Id
        skuInfoMapper.insertSelective(pmsSkuInfo);

        //保存sku图片
        for (PmsSkuImage pmsSkuImage : pmsSkuInfo.getSkuImageList()) {
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            skuImageMapper.insertSelective(pmsSkuImage);
        }

        //保存sku的平台属性
        for (PmsSkuAttrValue pmsSkuAttrValue : pmsSkuInfo.getSkuAttrValueList()) {
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            skuAttrMapper.insertSelective(pmsSkuAttrValue);
        }

        //保存sku的销售属性
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuInfo.getSkuSaleAttrValueList()) {
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            skuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }
    }

    @Override
    public PmsSkuInfo getSkuInfoById(String skuId) {
        //查询商品对象
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = skuInfoMapper.selectOne(pmsSkuInfo);

        //封装商品图片
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> skuImages = skuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(skuImages);
        return skuInfo;
    }
}

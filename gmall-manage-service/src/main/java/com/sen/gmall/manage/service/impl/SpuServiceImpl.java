package com.sen.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sen.gmall.api.beans.PmsProductImage;
import com.sen.gmall.api.beans.PmsProductInfo;
import com.sen.gmall.api.beans.PmsProductSaleAttr;
import com.sen.gmall.api.beans.PmsProductSaleAttrValue;
import com.sen.gmall.api.service.SpuService;
import com.sen.gmall.manage.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/2 22:07
 * @Description:
 */
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private PmsProductInfoMapper productInfoMapper;

    @Autowired
    private PmsSaleAttrInfoMapper saleAttrInfoMapper;

    @Autowired
    private PmsSaleAttrValueMapper saleAttrValueMapper;

    @Autowired
    private PmsProductImageMapper productImageMapper;

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {

        PmsProductInfo productInfo = new PmsProductInfo();
        productInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> productInfos = productInfoMapper.select(productInfo);
        return productInfos;
    }

    @Override
    public void saverSpuInfo(PmsProductInfo pmsProductInfo) {
        //保存商品信息
        productInfoMapper.insertSelective(pmsProductInfo);

        //保存图片信息
        for (PmsProductImage pmsProductImage : pmsProductInfo.getSpuImageList()) {
            pmsProductImage.setProductId(pmsProductInfo.getId());
            productImageMapper.insertSelective(pmsProductImage);
        }

        //保存销售属性
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductInfo.getSpuSaleAttrList()) {
            pmsProductSaleAttr.setProductId(pmsProductInfo.getId());
            saleAttrInfoMapper.insertSelective(pmsProductSaleAttr);

            //保存销售属性值
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : pmsProductSaleAttr.getSpuSaleAttrValueList()) {
                pmsProductSaleAttrValue.setProductId(pmsProductInfo.getId());
                saleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
            }
        }
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        PmsProductSaleAttr productSaleAttr = new PmsProductSaleAttr();
        productSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> productSaleAttrs = saleAttrInfoMapper.select(productSaleAttr);

        //销售设属性值
        for (PmsProductSaleAttr saleAttr : productSaleAttrs) {
            PmsProductSaleAttrValue productSaleAttrValue = new PmsProductSaleAttrValue();
            productSaleAttrValue.setSaleAttrId(saleAttr.getSaleAttrId());
            productSaleAttrValue.setProductId(spuId);
            List<PmsProductSaleAttrValue> saleAttrValues = saleAttrValueMapper.select(productSaleAttrValue);
            saleAttr.setSpuSaleAttrValueList(saleAttrValues);
        }

        return productSaleAttrs;
    }

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {
        PmsProductImage productImage = new PmsProductImage();
        productImage.setProductId(spuId);
        return productImageMapper.select(productImage);
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(String skuId, String spuId) {
        return saleAttrInfoMapper.spuSaleAttrListCheckBySku(skuId,spuId);
    }
}

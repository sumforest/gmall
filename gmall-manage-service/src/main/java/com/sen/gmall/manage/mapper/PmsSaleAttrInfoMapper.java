package com.sen.gmall.manage.mapper;

import com.sen.gmall.api.beans.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/3 15:00
 * @Description:
 */
public interface PmsSaleAttrInfoMapper extends Mapper<PmsProductSaleAttr> {
    List<PmsProductSaleAttr> spuSaleAttrListCheckBySku(@Param("skuId") String skuId, @Param("spuId") String spuId);
}

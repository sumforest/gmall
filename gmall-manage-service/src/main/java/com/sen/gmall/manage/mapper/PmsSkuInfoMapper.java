package com.sen.gmall.manage.mapper;

import com.sen.gmal.api.beans.PmsSkuInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: Sen
 * @Date: 2019/11/3 18:24
 * @Description:
 */
public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo> {
    List<PmsSkuInfo> selectSkuInfoAndSaleAttrValues(@Param("spuId") String spuId);
}

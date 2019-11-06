package com.sen.gmall.manage.mapper;

import com.sen.gmall.api.beans.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/11/2 17:26
 * @Description:
 */
public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {
    List<PmsBaseAttrInfo> selectSearchAttrAndAttrValues(@Param("params") String params);
}

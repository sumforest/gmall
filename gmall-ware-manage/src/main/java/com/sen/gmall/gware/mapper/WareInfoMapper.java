package com.sen.gmall.gware.mapper;

import com.sen.gmall.gware.bean.WmsWareInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @param
 * @return
 */
public interface WareInfoMapper extends Mapper<WmsWareInfo> {


     List<WmsWareInfo> selectWareInfoBySkuid(String skuid);



}

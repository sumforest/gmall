package com.sen.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sen.gmal.api.beans.*;
import com.sen.gmal.api.service.PmsBaseAttrService;
import com.sen.gmall.manage.mapper.PmsBaseSaleAttrMapper;
import com.sen.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.sen.gmall.manage.mapper.PmsBaseAttrValueMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Auther: Sen
 * @Date: 2019/11/2 17:25
 * @Description:
 */
@Service
public class PmsBaseAttrServiceImpl implements PmsBaseAttrService {

    @Autowired
    private PmsBaseAttrInfoMapper infoMapper;

    @Autowired
    private PmsBaseAttrValueMapper valueMapper;

    @Autowired
    PmsBaseSaleAttrMapper saleAttrMapper;

    @Override
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id) {

        PmsBaseAttrInfo info = new PmsBaseAttrInfo();
        info.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> infos = infoMapper.select(info);

        for (PmsBaseAttrInfo pmsBaseAttrInfo : infos) {
            //设置平台属性值
            PmsBaseAttrValue attrValue = new PmsBaseAttrValue();
            attrValue.setAttrId(pmsBaseAttrInfo.getId());
            List<PmsBaseAttrValue> attrValues = valueMapper.select(attrValue);
            pmsBaseAttrInfo.setAttrValueList(attrValues);
        }
        return infos;
    }

    @Override
    public String saveAttrInfo(PmsBaseAttrInfo attrInfo) {
        //如果id为空则为新增平台属性
        if (StringUtils.isBlank(attrInfo.getId())) {

            //保存attrInfo获取主键id
            //insertSelective()只保存值不为null的字段
            infoMapper.insertSelective(attrInfo);
            //设置attrValue的attrInfoId
            for (PmsBaseAttrValue pmsBaseAttrValue : attrInfo.getAttrValueList()) {
                pmsBaseAttrValue.setAttrId(attrInfo.getId());

                //保存attrValue
                valueMapper.insertSelective(pmsBaseAttrValue);
            }

        //修改平台属性
        } else {
            //保存平台属性
            infoMapper.updateByPrimaryKey(attrInfo);

            //删除平台属性值
            Example example = new Example(PmsBaseAttrValue.class);
            example.createCriteria().andEqualTo("attrId", attrInfo.getId());
            valueMapper.deleteByExample(example);

            //新增平台属性
            for (PmsBaseAttrValue attrValue : attrInfo.getAttrValueList()) {
                valueMapper.insert(attrValue);
            }
        }
        return "success";
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {
        PmsBaseAttrValue attrValue = new PmsBaseAttrValue();
        attrValue.setAttrId(attrId);
        return valueMapper.select(attrValue);
    }

    @Override
    public List<PmsBaseSaleAttr> getBaseSaleAttrList() {
        return saleAttrMapper.selectAll();
    }

    @Override
    public List<PmsBaseAttrInfo> getSearchAttrAndAttrValues(List<PmsSearchSkuInfo> pmsSearchSkuInfos) {
        //去掉重复的attrValueId
        Set<String> attrValueIds = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {

            for (PmsSkuAttrValue pmsSkuAttrValue : pmsSearchSkuInfo.getSkuAttrValueList()) {
                attrValueIds.add(pmsSkuAttrValue.getValueId());
            }
        }

        //拼接查询参数
        String params = StringUtils.join(attrValueIds, ",");

        List<PmsBaseAttrInfo> baseAttrInfos = new ArrayList<>();
        if (params.length() > 0) {

            baseAttrInfos  = infoMapper.selectSearchAttrAndAttrValues(params);
        }
        return baseAttrInfos;
    }
}

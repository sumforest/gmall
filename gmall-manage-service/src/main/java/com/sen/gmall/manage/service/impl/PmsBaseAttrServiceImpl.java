package com.sen.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sen.gmall.api.beans.PmsBaseAttrInfo;
import com.sen.gmall.api.beans.PmsBaseAttrValue;
import com.sen.gmall.api.beans.PmsBaseSaleAttr;
import com.sen.gmall.api.service.PmsBaseAttrService;
import com.sen.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.sen.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.sen.gmall.manage.mapper.PmsBaseSaleAttrMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import java.util.List;

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
}

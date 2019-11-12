package com.sen.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.sen.gmal.api.beans.PmsSkuAttrValue;
import com.sen.gmal.api.beans.PmsSkuImage;
import com.sen.gmal.api.beans.PmsSkuInfo;
import com.sen.gmal.api.beans.PmsSkuSaleAttrValue;
import com.sen.gmal.api.service.PmsSkuService;
import com.sen.gmall.manage.mapper.PmsSkuAttrMapper;
import com.sen.gmall.manage.mapper.PmsSkuImageMapper;
import com.sen.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.sen.gmall.manage.mapper.PmsSkuInfoMapper;
import com.sen.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Autowired
    private RedisUtil redisUtil;

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

    private PmsSkuInfo getSkuInfoByIdFromDB(String skuId) {
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

    @Override
    public PmsSkuInfo getSkuInfoById(String skuId){

        PmsSkuInfo pmsSkuInfo ;
        //获取redis客户端
        Jedis jedis = redisUtil.getJedis();
        //设置key
        String skuKey = "sku:" + skuId + ":info";
        String skuJson = jedis.get(skuKey);

        //判断redis中有数据
        if (StringUtils.isNotBlank(skuJson)) {
            //解析
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);

        //redis中不存在数据
        } else {

            //token用于删除锁前比较value的值，防止误删其他线程的锁
            String token = UUID.randomUUID().toString();
            //查询数据前加redis分布式锁
            String ok = jedis.set("sku:" + skuId + "lock", token, "nx", "ex", 10);
            //没有上锁
            if (StringUtils.isNotBlank(ok) && "OK".equals(ok)) {
                //查询数据库
                pmsSkuInfo = getSkuInfoByIdFromDB(skuId);

                //值不为空放进缓存
                if (pmsSkuInfo != null) {
                    jedis.set("sku:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));

                //防止缓存穿透
                } else {
                    jedis.setex("sku:" + skuId + ":info", 300, "");
                }

                //删除锁前判断是否为本线程的锁,再删除
                String lockToken = jedis.get("sku:" + skuId + "lock");
                if (StringUtils.isNotBlank(lockToken) && lockToken.equals(token)) {
                    //由于获取value和删除锁的操作不具有原子性，会出现获取value后的一秒内锁过期，
                    // 误删其他线程的锁的情况，用lua脚本解决
                    // jedis.eval("lua");
                    //操作完后释放锁
                    jedis.del("sku:" + skuId + "lock");
                }

            //已经上锁,自旋
            } else{
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //递归，睡眠后重新执行一遍
                return getSkuInfoById(skuId);
            }

        }
        //释放连接
        jedis.close();
        return pmsSkuInfo;
    }

    @Override
    public Map<String,String> getSkuInfoAndSaleAttrValues(String spuId) {
        List<PmsSkuInfo> pmsSkuInfos = skuInfoMapper.selectSkuInfoAndSaleAttrValues(spuId);

        //封装hash表
        Map<String,String> map = new HashMap<>();

        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {

            String v = pmsSkuInfo.getId();
            StringBuilder k = new StringBuilder();

            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuInfo.getSkuSaleAttrValueList()) {
                k.append(pmsSkuSaleAttrValue.getSaleAttrValueId()).append("|");
            }
            map.put(k.toString(), v);
        }
        return map;
    }

    @Override
    public List<PmsSkuInfo> getAll() {
        List<PmsSkuInfo> pmsSkuInfos = skuInfoMapper.selectAll();

        //封装PmsSkuAttrValue对象
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {

            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> skuAttrValueList = skuAttrMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(skuAttrValueList);

        }
        return pmsSkuInfos;
    }

    @Override
    public boolean checkPrice(String productSkuId, BigDecimal price) {
        boolean isSuccess = false;
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(productSkuId);
        PmsSkuInfo pmsSkuInfoFromDb = skuInfoMapper.selectOne(pmsSkuInfo);
        if (price.compareTo(pmsSkuInfoFromDb.getPrice()) == 0) {
            isSuccess = true;
        }
        return isSuccess;
    }
}

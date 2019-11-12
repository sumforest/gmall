package com.sen.gmall.gware.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.sen.gmall.gware.bean.WmsWareInfo;
import com.sen.gmall.gware.bean.WmsWareOrderTask;
import com.sen.gmall.gware.bean.WmsWareSku;
import com.sen.gmall.gware.enums.TaskStatus;
import com.sen.gmall.gware.service.GwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @return
 */

@Controller
public class GwareController {

    @Autowired
    GwareService gwareService;

    @RequestMapping("/index")
    public String index(){
        return "index";
    }
    @RequestMapping("/wareSkuListPage")
    public String wareSkuListPage(){
        return "wareSkuListPage";
    }

    //根据sku判断是否有库存
    @RequestMapping("/hasStock")
    @ResponseBody
    public ResponseEntity<String> hasStock(@RequestParam Map<String,String> hashMap){
        String numstr = (String) hashMap.get("num");
        Integer num=Integer.parseInt(numstr);
        String skuid =(String)hashMap.get("skuId");
        boolean hasStock = gwareService.hasStockBySkuId( skuid , num);
        if(hasStock){
            return   ResponseEntity.ok("1");
        }
        return  ResponseEntity.ok("0");
    }


    //根据skuid 返回 仓库
    @RequestMapping(value = "/skuWareInfo")
    @ResponseBody
    public  ResponseEntity<String> getWareInfoBySkuid(String skuid){
        if(skuid==null){
            return     ResponseEntity.noContent().build();
        }
        List<WmsWareInfo> wmsWareInfos = gwareService.getWareInfoBySkuid( skuid );
        String jsonString = JSON.toJSONString(wmsWareInfos);
        return ResponseEntity.ok(jsonString);
    }


    @RequestMapping(value = "/wareInfo")
    @ResponseBody
    public void addWareInfo(){
          gwareService.addWareInfo();
    }

    //根据skuid 返回 仓库
    @RequestMapping(value = "/wareSkuMap"  )
    @ResponseBody
    public ResponseEntity<String> getWareSkuMap(@RequestParam("skuid") List<String> skuidsList){
       // List<String> skuidsList = JSON.parseArray(skuids, String.class) ;
        Map<String, List<String>> wareSkuMap = gwareService.getWareSkuMap(skuidsList);
        String jsonString = JSON.toJSONString(wareSkuMap);
        return ResponseEntity.ok(jsonString);
    }



    @RequestMapping(value = "/saveWareSku" ,method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> addWareSku( WmsWareSku wmsWareSku){
         gwareService.addWareSku(wmsWareSku);
         return ResponseEntity.ok().build();
    }


    @RequestMapping(value = "/wareSkuList" ,method = RequestMethod.GET,produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<WmsWareSku> getWareSkuList(HttpServletResponse response){
        List<WmsWareSku> wmsWareSkuList = gwareService.getWareSkuList();
        return wmsWareSkuList;
    }

    @RequestMapping(value = "/wareInfoList" ,method = RequestMethod.GET,produces="application/json;charset=UTF-8")
    @ResponseBody
    public List<WmsWareInfo> getWareInfoList(){
        List<WmsWareInfo> wmsWareInfoList = gwareService.getWareInfoList();
        return wmsWareInfoList;
    }


    /***
     * 出库
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value="/delivery",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> deliveryStock(HttpServletRequest httpServletRequest){
        String id = httpServletRequest.getParameter("id");
        String trackingNo = httpServletRequest.getParameter("trackingNo");
        WmsWareOrderTask wmsWareOrderTask =new WmsWareOrderTask();
        wmsWareOrderTask.setId(id);
        wmsWareOrderTask.setTrackingNo(trackingNo);
        gwareService.deliveryStock(wmsWareOrderTask);
        return  ResponseEntity.ok().build();
    }


    @RequestMapping(value="/taskList",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getWareOrderTaskList(HttpServletRequest httpServletRequest){
        List<WmsWareOrderTask> wmsWareOrderTaskList = gwareService.getWareOrderTaskList(null);
        SerializeConfig config = new SerializeConfig();
        config.configEnumAsJavaBean(TaskStatus.class);
        String jsonString = JSON.toJSONString(wmsWareOrderTaskList);
        return ResponseEntity.ok().body(jsonString);
    }



}

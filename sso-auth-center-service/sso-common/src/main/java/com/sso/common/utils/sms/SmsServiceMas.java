package com.sso.common.utils.sms;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.util.DigestUtils;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于移动云MAS平台发送短信
 *
 * @author 小道仙
 * @date 2020/10/11
 */
public class SmsServiceMas {


    /**
     * 一对一发送消息
     *
     * @param mobiles    手机号
     * @param content   内容
     * @return  {"msgGroup":"0927152506001000833076","rspcod":"success","success":true}
     * 如果发送失败，msgGroup为空
     */
    public static JsonNode singleShot(String url ,String mobiles, String content){
        Map<String, String> map = beasParams(mobiles, content, "",1);
        Base64.Encoder encoder = Base64.getEncoder();
        JsonNode jsonNode = null;
        System.out.println(JsonUtils.objectToJson(map));
        try {
            String params = encoder.encodeToString(JsonUtils.objectToJson(map).getBytes("UTF-8"));
            System.out.println(params);
            String json = HttpUtils.sendPost(url, params, "");
            //String json = HttpUtils.sendPost("http://112.33.46.17:37891/sms/norsubmit", params, "");
            //String json = HttpUtils.sendPost("http://10.147.124.204/sms/norsubmit", params, "");
            jsonNode = JsonUtils.stringToJsonNode(json);
        }catch (Exception e){
            throw new RuntimeException("MAS消息发送失败");
        }
        return jsonNode;
    }

    /**
     * 一对多发送消息
     *
     * @param mobiles    手机号
     * @param content   内容
     */
    public static JsonNode multiple(String url ,List<String> mobiles, String content){
        if (mobiles == null || mobiles.isEmpty() || mobiles.size() > 5000){
            throw new RuntimeException("电话号码异常");
        }
        String tmp = "";
        for (String item : mobiles){
            tmp += ","+item;
        }
        tmp = tmp.substring(1);
        return singleShot(url, tmp, content);
    }

    /**
     * 发送模板消息
     *
     * @param mobiles 手机号
     * @param templateId 模板Id
     * @param params 模板参数,格式["123","456"]
     */
    public static JsonNode templateMsg(String url ,String mobiles, String templateId, String[] params){
        JsonNode jsonNode = null;
        Map<String, String> map = beasParams(mobiles, JsonUtils.objectToJson(params), templateId,2);
        map.put("templateId",templateId);
        Base64.Encoder encoder = Base64.getEncoder();
        try {
            System.out.println(JsonUtils.objectToJson(map));
            String tmpParams = encoder.encodeToString(JsonUtils.objectToJson(map).getBytes("UTF-8"));
            System.out.println(tmpParams);
            //String json = HttpUtils.sendPost("http://112.33.46.17:37891/sms/tmpsubmit", tmpParams, "");
            //String json = HttpUtils.sendPost("http://10.147.124.204/sms/template", tmpParams, "");
            String json = HttpUtils.sendPost(url, tmpParams, "");
            jsonNode = JsonUtils.stringToJsonNode(json);
            System.out.println("MAS模板消息: "+jsonNode);

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("MAS模板消息发送失败");
        }
        return jsonNode;
    }

    /**
     * 基本参数封装
     *
     * type == 1,  传递的是短信消息
     * type == 2,  传递的模板参数
     */
    private static Map<String,String> beasParams(String mobiles,String tmp,String templateId,int type){
        String secretKey = "Szzd_2022";//Szzd_2022 Zjsdb_1234
        String apId = "zjsdbj";//zjsdbj szzd
        String sign = "iFSMpansM";//iFSMpansM  eIXn7yYio
        String ecName = "浙江省担保集团有限公司1";//浙江省担保集团有限公司 浙江省担保集团
        String addSerial = "";

        String mac = "";
        Map<String,String> map = new HashMap();
        map.put("ecName",ecName);
        map.put("apId",apId);
        map.put("secretKey", secretKey);
        map.put("sign",sign);
        map.put("mobiles",mobiles);
        if (type == 1){
            mac = ecName+apId+secretKey+mobiles+tmp+sign+addSerial;
            map.put("content",tmp);
        }else{
            mac = ecName+apId+secretKey+templateId+mobiles+tmp+sign+addSerial;
            map.put("params",tmp);
        }
        mac = DigestUtils.md5DigestAsHex(mac.getBytes()).toLowerCase();
        map.put("addSerial",addSerial);
        map.put("mac",mac);
        return map;
    }
}



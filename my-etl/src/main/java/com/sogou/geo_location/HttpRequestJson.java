package com.sogou.geo_location;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuqin212173 on 2016/12/8.
 */

//http://api.go2map.com/engine/api/search/json?what=classid:C_35&&range=center:116.32775631760649,39.99362914164339:300:0
public class HttpRequestJson {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestJson.class);    //日志记录
    private static Map<String,String> typeMap = new HashMap<String, String>();
    static {
        typeMap.put("1299", "一般幼儿园");
        typeMap.put("1298", "知名幼儿园");
        typeMap.put("1319", "一般小学");
        typeMap.put("1317", "知名小学");
        typeMap.put("1318", "一般中学");
        typeMap.put("1316", "知名中学");
    }
    private static Map<String,Integer> resultMap = new HashMap<String,Integer>();

    /**
     * 发送get请求
     * @param lon
     * lat
     * @return
     */
    public static JSONObject httpGet(double lon, double lat){
        //get请求返回结果
        JSONObject jsonResult = null;
        String url = "";
        String host = "http://api.go2map.com/engine/api/search/json";
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            //发送get请求
            for (String key : typeMap.keySet()) {
                url = host+"?what=classid:"+key+"&range=center:"+lon+","+lat+":50:1&pageinfo=1,10&classsort=1299:30,1298:30,1319:20,1317:20,1318:10,1316:10&locationsort=1";
                HttpGet request = new HttpGet(url);
                HttpResponse response = client.execute(request);
                /**请求发送成功，并得到响应**/
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    /**读取服务器返回过来的json字符串数据**/
                    String strResult = EntityUtils.toString(response.getEntity());
                    /**把json字符串转换成json对象**/
                    jsonResult = JSONObject.parseObject(strResult);

                    if(jsonResult!=null&&jsonResult.getString("status").equals("ok")){
                        String  subcategory = jsonResult.getJSONObject("response").getJSONObject("data").getJSONArray("feature").getJSONObject(0).getJSONObject("detail").getString("subcategory");
                       String caption = jsonResult.getJSONObject("response").getJSONObject("data").getJSONArray("feature").getJSONObject(0).getString("caption");
                        if(resultMap.containsKey(caption)){
                            resultMap.put(caption,resultMap.get(caption)+1);
                        }else {
                            resultMap.put(caption,1);
                        }
                    }
                } else {
                    logger.error("get请求提交失败:" + url);
                }
                request.releaseConnection();
            }


        } catch (IOException e) {
            logger.error("get请求提交失败:" + url, e);
        }

        return jsonResult;
    }

    public static void getMain()
    {
        if(resultMap.size()>0){
                int max = 0;
                String main = "";
                for (String key : resultMap.keySet()) {
                    if(resultMap.get(key)>max){
                        max = resultMap.get(key);
                        main = key;
                    }
                    System.out.println(main);
                }
        }
    }
    public static void main(String[] args) {
        HttpRequestJson hrj = new HttpRequestJson();
        hrj.httpGet(116.44258853563004,39.91703387225365);
    }
}
package com.sogou.location_log.lib;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sogou.util.DataUtil;
import com.sogou.util.ExtractUtil;
import com.sogou.util.TimeUtil;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LocationLogMapper extends
        Mapper<Object, Text, Text, Text> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractUtil.class);


    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        String line = value.toString();
        try {
            if (!line.contains("com.sogou.upd.timo.service.impl.CurrentGeoServiceImpl")) {
                return;
            }
            String babyId = "";
            String timoId = "";
            String ctime = ExtractUtil.getTimeValue(line);
            if (line.contains("locationWifiGSM")) {
                //System.out.println(line);
                timoId = ExtractUtil.getJsonValue("deviceid", line);
                if (timoId.contains("timo")) {
                    timoId = timoId.split("-")[1];
                }
                String locationId = TimeUtil.nowTimestamp().getTime() + timoId + DataUtil.generateSmsCode();
                JSONArray arr = ExtractUtil.getJsonArrayValue("gsm", line);
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject tmp = arr.getJSONObject(i);
                    context.write(new Text("gsm$$$\t" + tmp.getString("cid") + "\t" + tmp.getString("lac") + "\tconnect\t" + locationId + "\t" + timoId + "\t" + ctime), new Text("1"));
                }
                arr = ExtractUtil.getJsonArrayValue("cell", line);
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject tmp = arr.getJSONObject(i);
                    context.write(new Text("gsm$$$\t" + tmp.getString("cid") + "\t" + tmp.getString("lac") + "\tneighbors\t" + locationId + "\t" + timoId + "\t" + ctime), new Text("1"));
                }
                arr = ExtractUtil.getJsonArrayValue("wifi", line);
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject tmp = arr.getJSONObject(i);
                    context.write(new Text("wifi$$$\t" + tmp.getString("bssid") + "\t" + tmp.getString("rssi") + "\t" + locationId + "\t" + timoId + "\t" + ctime), new Text("1"));
                }
                context.write(new Text("location$$$\t\t" + timoId + "\twifigsm\t" + ExtractUtil.getJsonValue("range", line) + "\t" + ExtractUtil.getJsonValue("x", line) + "\t" + ExtractUtil.getJsonValue("y", line) + "\t" + ExtractUtil.getJsonValue("l", line) + "\t" + ExtractUtil.getJsonValue("wnum", line) + "\t" + locationId + "\t" + ctime), new Text("1"));
                return;
            } else if (line.contains("parseGeoSequence") && line.contains("mode\":\"gps\"")) {
                //System.out.println(line);
                babyId = ExtractUtil.getEqualValue("userId", line);
                context.write(new Text("location$$$\t" + babyId + "\t\tgps\t" + ExtractUtil.getJsonValue("range", line) + "\t" + ExtractUtil.getJsonValue("latitude", line) + "\t" + ExtractUtil.getJsonValue("longitude", line) + "\t\t" + ExtractUtil.getJsonValue("signal", line) + "\t\t" + ctime), new Text("1"));
                return;
            }
        } catch (Exception e) {
            LOGGER.error("extract log error", e);
            return;
        }
    }


}
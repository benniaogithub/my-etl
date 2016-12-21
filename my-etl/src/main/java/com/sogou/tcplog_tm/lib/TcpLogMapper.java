package com.sogou.tcplog_tm.lib;


import com.google.common.base.Strings;
import com.sogou.util.DataUtil;
import com.sogou.util.ExtractUtil;
import com.sogou.util.TimeUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TcpLogMapper extends
        Mapper<Object, Text, Text, Text> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractUtil.class);
    String tmpline = "";

    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        String line = value.toString();
        boolean clear = true;
        try {
            String type = ExtractUtil.getEqualValue("type", line);
            if (Strings.isNullOrEmpty(type)) {
                return;
            }
            LogType logType = ExtractUtil.getLogType(line);
            if (logType == null) {
                return;
            }
            String udid = ExtractUtil.getEqualValue("udid", line);
            String platform = UDFSNToModelT1.evaluate(udid);
            if (Strings.isNullOrEmpty(udid) || !ExtractUtil.isTmUdid(udid)) {
                return;
            }
            String baby_id = ExtractUtil.getEqualValue("babyId", line);
            String timo_id = ExtractUtil.getEqualValue("userId", line);

            String ctime = ExtractUtil.getTimeValue(line);
            String res = "";
            String sessionId = TimeUtil.nowTimestamp().getTime() + timo_id + DataUtil.generateSmsCode();

            if (logType == LogType.RECEIVE_FROM_TIMO) {
            if ("120".equals(type)) {
                    String alarmNum = "", touchNum = "", alarmPreNum = "", touchPedometer = "", playNum = "", friend = "";
                    String camera_enter_cnt="",camera_capture_cnt="",use_sticker_cnt="",vibration_enter_cnt="",stopwatch_enter_cnt="",theme_enter_cnt="",swatch_enter_cnt1="",swatch_enter_cnt2="";

                    int size = 0;
                    size = ExtractUtil.getJsonArrayValue("camera_effect_cnts", line).size();
                    long sum = 0;
                    int temp = 0;
                    for(int i=0;i<size;i++){
                        temp = Integer.parseInt(ExtractUtil.getJsonArrayValue("camera_effect_cnts", line).get(i).toString());
                        if(temp>0){
                            context.write(new Text("photo$$$camera_effect_cnts\t" + i + "\t" + baby_id + "\t" +platform+"\t" + temp+"\t" + "0"+"\t" + "0"), new Text("1"));
                            sum += temp;
                        }
                    }
                    if(sum>0) {
                        context.write(new Text("localfunc$$$" + timo_id + "\tcamera_effect_cnts\t" + sum + "\t" + ctime + "\t" + baby_id + "\t" + udid), new Text("1"));
                    }

                    size = ExtractUtil.getJsonArrayValue("sticker_detail_cnt", line).size();
                    sum = 0;
                    temp = 0;
                    for(int i=0;i<size;i++){
                        temp = Integer.parseInt(ExtractUtil.getJsonArrayValue("sticker_detail_cnt", line).get(i).toString());
                        if(temp>0){
                            context.write(new Text("photo$$$sticker_detail_cnt\t" + i + "\t" + baby_id +  "\t" +platform+"\t" + temp+"\t" + "0"+"\t" + "0"), new Text("1"));
                            sum += temp;
                        }
                    }
                    if(sum>0) {
                        context.write(new Text("localfunc$$$" + timo_id + "\tsticker_detail_cnt\t" + sum + "\t" + ctime + "\t" + baby_id + "\t" + udid), new Text("1"));
                    }


                }
            } else {
                return;
            }

        } catch (Exception e) {
            if (clear) {
                tmpline = "";
            }
            LOGGER.error("extract log error", e);
            return;
        }
    }
}
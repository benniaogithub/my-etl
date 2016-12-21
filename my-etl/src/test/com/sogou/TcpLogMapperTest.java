package com.sogou;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.sogou.tcplog_tm.lib.LogType;
import com.sogou.util.DataUtil;
import com.sogou.util.ExtractUtil;
import com.sogou.util.TimeUtil;
import org.apache.hadoop.io.Text;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by liuqin212173 on 2016/10/26.
 */
public class TcpLogMapperTest {


    public void map(String value)
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
            if (Strings.isNullOrEmpty(udid) || !ExtractUtil.isTmUdid(udid)) {
                return;
            }
            String baby_id = ExtractUtil.getEqualValue("babyId", line);
            String timo_id = ExtractUtil.getEqualValue("userId", line);

            String ctime = ExtractUtil.getTimeValue(line);
            String res = "";
            String sessionId = TimeUtil.nowTimestamp().getTime() + timo_id + DataUtil.generateSmsCode();

            if (logType == LogType.RECEIVE_FROM_TIMO) {
                if ("30".equals(type)) {
                    res = "chat$$$" + timo_id + "\tall\t\t\tvoice\t" + ctime + "\tsend\t" + ExtractUtil.getJsonValue("length", line) + "\t" + ExtractUtil.getEqualValue("data.len", line) + "\t" + ExtractUtil.getJsonValue("id", line) + "\t" + baby_id + "\t" + udid + "\t";
                } else if ("3".equals(type)) {
                    res = "chat$$$" + timo_id + "\tall\t\t\tvoice\t" + ctime + "\treceived\t\t" + ExtractUtil.getEqualValue("data.len", line) + "\t" + ExtractUtil.getJsonValue("id", line).split("_")[1] + "\t" + baby_id + "\t" + udid + "\t";
                } else if ("69".equals(type)) {
                    String action = ExtractUtil.getJsonValue("action", line);
                    if ("1".equals(action)) {
                        action = "play";
                    } else if ("2".equals(action)) {
                        action = "pause";
                    } else if ("3".equals(action)) {
                        action = "interrupt";
                    } else if ("4".equals(action)) {
                        action = "over";
                    } else if ("5".equals(action)) {
                        action = "received";
                    } else if ("6".equals(action)) {
                        action = "downloaded";
                    } else if ("7".equals(action)) {
                        action = "beep";
                    }
                    if (line.contains("\"reply_type\":30") || line.contains("\"reply_type\":122")) {
                        res = "chat$$$" + timo_id + "\tall\t\t\t\t" + ctime + "\t" + action + "\t\t\t" + ExtractUtil.getJsonValue("id", line) + "\t" + baby_id + "\t" + udid + "\t";
                    } else if (line.contains("\"reply_type\":67") && line.contains("\"msg_type\":1")) {
                        String subType = ExtractUtil.getJsonValue("msg_sub_type", line);
                        if ("1".equals(subType)) {
                            subType = "morning";
                        } else if ("2".equals(subType)) {
                            subType = "story";
                        } else {
                            return;
                        }
                        res = "messagebox$$$" + timo_id + "\t" + subType + "\t" + action + "\t" + ctime + "\t\t\t" + ExtractUtil.getJsonValue("id", line) + "\t" + baby_id + "\t" + udid;
                    } else if (line.contains("\"reply_type\":16")) {
                        action = ExtractUtil.getJsonValue("action", line);
                        if ("".equals(action)) action = "1";
                        String id = ExtractUtil.getJsonValue("theme_id", line);
                        res = "displaysetting$$$" + timo_id + "\ttheme\t" + id + "\t" + action + "\t" + ctime + "\t" + baby_id + "\t" + udid + "\t";
                    } else if (line.contains("\"reply_type\":17")) {
                        action = ExtractUtil.getJsonValue("action", line);
                        if ("".equals(action)) action = "1";
                        String id = ExtractUtil.getJsonValue("swatch_id", line);
                        res = "displaysetting$$$" + timo_id + "\twatch\t" + id + "\t" + action + "\t" + ctime + "\t" + baby_id + "\t" + udid + "\t";
                    } else {
                        return;
                    }
                } else if ("27".equals(type)) {
                    res = "game$$$" + timo_id + "\t" + ExtractUtil.getJsonValue("game", line) + "\t" + ExtractUtil.getJsonValue("op", line) + "\t" + ctime + "\t" + baby_id + "\t" + udid + "\t" + sessionId + "\t0";
                } else if ("55".equals(type)) {
                    res = "story$$$" + timo_id + "\t" + ExtractUtil.getJsonValue("op", line) + "\t\t" + ctime + "\t" + ExtractUtil.getJsonValue("id", line) + "\t" + baby_id + "\t" + udid;
                } else if ("56".equals(type)) {
                    res = "story$$$" + timo_id + "\t" + ExtractUtil.getJsonValue("op", line) + "\t" + ExtractUtil.getJsonValue("time", line) + "\t" + ctime + "\t" + ExtractUtil.getJsonValue("id", line) + "\t" + baby_id + "\t" + udid;
                } else if ("120".equals(type)) {
                    String alarmNum = "", touchNum = "", alarmPreNum = "", touchPedometer = "", playNum = "", friend = "";
                    String camera_enter_cnt = "", camera_capture_cnt = "", use_sticker_cnt = "", vibration_enter_cnt = "", stopwatch_enter_cnt = "", theme_enter_cnt = "", swatch_enter_cnt1 = "", swatch_enter_cnt2 = "";
                    if (ExtractUtil.isOldUdid(udid)) {
                        alarmNum = ExtractUtil.getT120Value("AlarmNum", line);
                        touchNum = ExtractUtil.getT120Value("TouchNum", line);
                        alarmPreNum = ExtractUtil.getT120Value("AlarmPreNum", line);
                        touchPedometer = ExtractUtil.getT120Value("TouchPedometer", line);
                        playNum = ExtractUtil.getT120Value("PlayNum", line);
                    } else {
                        int size = 0;
                        alarmPreNum = ExtractUtil.getJsonValue("alarm_ack_cnt", line);
                        touchPedometer = ExtractUtil.getJsonValue("sport_access_cnt", line);
                        friend = ExtractUtil.getJsonValue("mf_access_cnt", line);
                        playNum = ExtractUtil.getJsonValue("vmsg_play_cnt", line);
                        camera_enter_cnt = ExtractUtil.getJsonValue("camera_enter_cnt", line);
                        camera_capture_cnt = ExtractUtil.getJsonValue("camera_capture_cnt", line);
                        use_sticker_cnt = ExtractUtil.getJsonValue("use_sticker_cnt", line);
                        size = ExtractUtil.getJsonArrayValue("camera_effect_cnts", line).size();
                        vibration_enter_cnt = ExtractUtil.getJsonValue("vibration_enter_cnt", line);
                        stopwatch_enter_cnt = ExtractUtil.getJsonValue("stopwatch_enter_cnt", line);
                        theme_enter_cnt = ExtractUtil.getJsonValue("theme_enter_cnt", line);
                        swatch_enter_cnt1 = ExtractUtil.getJsonValue("swatch_enter_cnt1", line);
                        swatch_enter_cnt2 = ExtractUtil.getJsonValue("swatch_enter_cnt2", line);

                        long sum = 0;
                        int temp = 0;
                        for(int i=0;i<size;i++){
                            temp = Integer.parseInt(ExtractUtil.getJsonArrayValue("camera_effect_cnts", line).get(i).toString());
                            if(temp>0){
                                System.out.println(i);
                                System.out.println(temp);
                                sum += temp;
                            }
                        }
                        if(sum>0) System.out.println(sum);

                        size = ExtractUtil.getJsonArrayValue("sticker_detail_cnt", line).size();
                        sum = 0;
                        temp = 0;
                        for(int i=0;i<size;i++){
                            temp = Integer.parseInt(ExtractUtil.getJsonArrayValue("sticker_detail_cnt", line).get(i).toString());
                            if(temp>0){
                                System.out.println(i);
                                System.out.println(temp);
                                sum += temp;
                            }
                        }
                        if(sum>0) System.out.println(sum);
                    }


                    if (!Strings.isNullOrEmpty(alarmNum) && !"0".equals(alarmNum)) {
                        res = new Text("localfunc$$$" + timo_id + "\ttime\t" + alarmNum + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }

                    if (!Strings.isNullOrEmpty(touchNum) && !"0".equals(touchNum)) {
                        res = new Text("localfunc$$$" + timo_id + "\ttouch\t" + touchNum + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }
                    if (!Strings.isNullOrEmpty(alarmPreNum) && !"0".equals(alarmPreNum)) {
                        res = new Text("localfunc$$$" + timo_id + "\talarm\t" + alarmPreNum + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }
                    if (!Strings.isNullOrEmpty(touchPedometer) && !"0".equals(touchPedometer)) {
                        res = new Text("localfunc$$$" + timo_id + "\tpedometer\t" + touchPedometer + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }
                    if (!Strings.isNullOrEmpty(playNum) && !"0".equals(playNum)) {
                        res = new Text("localfunc$$$" + timo_id + "\tplaymessage\t" + playNum + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }
                    if (!Strings.isNullOrEmpty(friend) && !"0".equals(friend)) {
                        res = new Text("localfunc$$$" + timo_id + "\tfriend\t" + friend + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }
                    if (!Strings.isNullOrEmpty(camera_enter_cnt) && !"0".equals(camera_enter_cnt)) {
                        res = new Text("localfunc$$$" + timo_id + "\tcamera_enter_cnt\t" + camera_enter_cnt + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }

                    if (!Strings.isNullOrEmpty(camera_capture_cnt) && !"0".equals(camera_capture_cnt)) {
                        res = new Text("localfunc$$$" + timo_id + "\tcamera_capture_cnt\t" + camera_capture_cnt + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }
                    if (!Strings.isNullOrEmpty(use_sticker_cnt) && !"0".equals(use_sticker_cnt)) {
                        res = new Text("localfunc$$$" + timo_id + "\tuse_sticker_cnt\t" + use_sticker_cnt + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }

                    if (!Strings.isNullOrEmpty(vibration_enter_cnt) && !"0".equals(vibration_enter_cnt)) {
                        res = new Text("localfunc$$$" + timo_id + "\tvibration_enter_cnt\t" + vibration_enter_cnt + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }
                    if (!Strings.isNullOrEmpty(stopwatch_enter_cnt) && !"0".equals(stopwatch_enter_cnt)) {
                        res = new Text("localfunc$$$" + timo_id + "\tstopwatch_enter_cnt\t" + stopwatch_enter_cnt + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }

                    if (!Strings.isNullOrEmpty(theme_enter_cnt) && !"0".equals(theme_enter_cnt)) {
                        res = new Text("localfunc$$$" + timo_id + "\ttheme_enter_cnt\t" + theme_enter_cnt + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }
                    if (!Strings.isNullOrEmpty(swatch_enter_cnt1) && !"0".equals(swatch_enter_cnt1)) {
                        res = new Text("localfunc$$$" + timo_id + "\tswatch_enter_cnt1\t" + swatch_enter_cnt1 + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }
                    if (!Strings.isNullOrEmpty(swatch_enter_cnt2) && !"0".equals(swatch_enter_cnt2)) {
                        res = new Text("localfunc$$$" + timo_id + "\tswatch_enter_cnt2\t" + swatch_enter_cnt2 + "\t" + ctime + "\t" + baby_id + "\t" + udid).toString();
                        System.out.println(res);
                    }
                } else if ("123".equals(type)) {
                    String subType = ExtractUtil.getJsonValue("sub_type", line);
                    if ("3".equals(subType)) {
                        res = "shake$$$" + timo_id + "\tmatch\t" + ExtractUtil.getJsonValue("request_id", line) + "\t\t\t" + ctime + "\t" + baby_id + "\t" + udid;
                    }
                } else if ("122".equals(type)) {
                    String chatType = ExtractUtil.getJsonValue("chat_type", line);
                    if ("1".equals(chatType)) {
                        chatType = "all";
                    } else if ("2".equals(chatType)) {
                        chatType = "single";
                    } else if ("3".equals(chatType)) {
                        chatType = "npc";
                    }
                    String mesType = ExtractUtil.getJsonValue("content_type", line);
                    if ("1".equals(mesType)) {
                        mesType = "voice";
                    } else if ("2".equals(mesType)) {
                        mesType = "text";
                    } else if ("3".equals(mesType)) {
                        mesType = "pic";
                    } else if ("4".equals(mesType)) {
                        mesType = "gif";
                    }
                    String message = ExtractUtil.getPacketValue(line.replaceAll("[ |\t]", ""));
                    JSONObject json = JSON.parseObject(message);
                    String messageId = json.getString("id");
                    if ("gif".equals(mesType)) {
                        String content = json.getJSONObject("content").getString("id");
                        res = "chat$$$" + timo_id + "\t" + chatType + "\t\t" + ExtractUtil.getJsonValue("to_id", line) + "\t" + mesType + "\t" + ctime + "\tsend\t" + ExtractUtil.getJsonValue("voice_length", line) + "\t" + ExtractUtil.getEqualValue("data.len", line) + "\t" + messageId + "\t" + baby_id + "\t" + udid + "\t" + content;
                    } else if ("text".equals(mesType)) {
                        String content = json.getJSONObject("content").getString("text");
                        res = "chat$$$" + timo_id + "\t" + chatType + "\t\t" + ExtractUtil.getJsonValue("to_id", line) + "\t" + mesType + "\t" + ctime + "\tsend\t" + ExtractUtil.getJsonValue("voice_length", line) + "\t" + ExtractUtil.getEqualValue("data.len", line) + "\t" + messageId + "\t" + baby_id + "\t" + udid + "\t" + content;
                    } else if ("voice".equals(mesType)) {
                        res = "chat$$$" + timo_id + "\t" + chatType + "\t\t" + ExtractUtil.getJsonValue("to_id", line) + "\t" + mesType + "\t" + ctime + "\tsend\t" + ExtractUtil.getJsonValue("voice_length", line) + "\t" + ExtractUtil.getEqualValue("data.len", line) + "\t" + messageId + "\t" + baby_id + "\t" + udid + "\t";
                    } else if ("pic".equals(mesType)) {
                        String content = json.getJSONObject("content").getString("image_id");
                        res = "chat$$$" + timo_id + "\t" + chatType + "\t\t" + ExtractUtil.getJsonValue("to_id", line) + "\t" + mesType + "\t" + ctime + "\tsend\t" + ExtractUtil.getJsonValue("voice_length", line) + "\t" + ExtractUtil.getEqualValue("data.len", line) + "\t" + messageId + "\t" + baby_id + "\t" + udid + "\t" + content;
                    }
                }
            } else if (logType == LogType.SEND_TO_TIMO) {
                if ("67".equals(type) && line.contains("\"msg_type\":1")) {
                    String subType = ExtractUtil.getJsonValue("msg_sub_type", line);
                    if ("1".equals(subType)) {
                        subType = "morning";
                    } else if ("2".equals(subType)) {
                        subType = "story";
                    } else {
                        return;
                    }
                    res = "messagebox$$$" + timo_id + "\t" + subType + "\tpush\t" + ctime + "\t\t\t" + ExtractUtil.getJsonValue("id", line) + "\t" + baby_id + "\t" + udid;
                } else if ("123".equals(type)) {
                    String subType = ExtractUtil.getJsonValue("sub_type", line);
                    if ("3".equals(subType)) {
                        String result = ExtractUtil.getJsonValue("result", line);
                        if ("1".equals(result)) {
                            res = "shake$$$" + timo_id + "\tsuccess\t" + ExtractUtil.getJsonValue("request_id", line) +
                                    "\t" + ExtractUtil.getJsonValue("result", line) + "\t" + ExtractUtil.getJsonValue("friend_id", line) + "\t" + ctime + "\t" + baby_id + "\t" + udid;
                        } else {
                            res = "shake$$$" + timo_id + "\tfailed\t" + ExtractUtil.getJsonValue("request_id", line) +
                                    "\t" + ExtractUtil.getJsonValue("result", line) + "\t" + ExtractUtil.getJsonValue("friend_id", line) + "\t" + ctime + "\t" + baby_id + "\t" + udid;
                        }
                    }
                }
            } else {
                return;
            }
            System.out.println(res);
        } catch (Exception e) {

            return;
        }
    }

    @Test
    public void testMap() throws Exception {
        map("2016-10-30_00:00:02 /timo/workspace/source/upd-timo-server/src/client/log_router.go client.logRoute(20) [INF] - RECV from timo, stamp=1477756802268, ipPort=117.136.77.116:7135, userId=1832798, babyId=1831773, udid=9142021010002571 type=120, packet={\"date\":20161029,\"gps_succ_cnt\":351,\"gps_fail_cnt\":0,\"wifi_succ_cnt\":0,\"wifi_fail_cnt\":152,\"cell_cnt\":113,\"login_fail_cnt\":0,\"gsensor_fail_cnt\":0,\"sport_access_cnt\":1,\"mf_access_cnt\":0,\"inmsg_cnt\":0,\"outmsg_cnt\":0,\"vmsg_play_cnt\":0,\"incall_cnt\":0,\"outcall_cnt\":3,\"alarm_cnt\":0,\"alarm_ack_cnt\":0,\"ul_data_flow\":252509,\"dl_data_flow\":1314009,\"wifi_ul_data_flow\":0,\"wifi_dl_data_flow\":0,\"game_forbid_in_school_cnt\":0,\"game_forbid_over_count_cnt\":0,\"wifi_conn_cnt\":0,\"wifi_conn_succ\":0,\"wifi_conn_scan_none\":0,\"wifi_auth_cnt\":0,\"wifi_auth_succ\":0,\"camera_enter_cnt\":1,\"camera_capture_cnt\":6,\"use_sticker_cnt\":0,\"camera_effect_cnts\":[0,6,0,0,0,0],\"vibration_enter_cnt\":0,\"stopwatch_enter_cnt\":1,\"theme_enter_cnt\":0,\"swatch_enter_cnt1\":0,\"swatch_enter_cnt2\":0,\"sticker_detail_cnt\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]}");
    }


}
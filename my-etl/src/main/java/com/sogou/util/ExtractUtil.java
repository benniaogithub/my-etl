package com.sogou.util;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Strings;
import com.sogou.tcplog_tm.lib.LogType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提取相关的工具类
 */
public final class ExtractUtil {

    private ExtractUtil() {
    }

    public final static String getEqualValue(String key, String line) {
        String equalStr = ".*[,& ]" + key + "=([^&, |]+).*";
        try {
            Pattern ptn = Pattern.compile(equalStr);
            Matcher m = ptn.matcher(line);
            if (m.matches()) {
                return m.group(1);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }


    public final static String getPacketValue(String line) {
        String equalStr = ".*packet=([^&]+)$";
        try {
            Pattern ptn = Pattern.compile(equalStr);
            Matcher m = ptn.matcher(line);
            if (m.matches()) {
                return m.group(1);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public final static String getJsonValue(String key, String line) {
        String equalStr = ".*[,& \"]" + key + "\":\"?([^\"&, \\}]+).*";
        try {
            Pattern ptn = Pattern.compile(equalStr);
            Matcher m = ptn.matcher(line);
            if (m.matches()) {
                return m.group(1);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public final static JSONArray getJsonArrayValue(String key, String line) {
        String equalStr = ".*[,& \"]" + key + "\":([^\\]]+).*";
        try {
            Pattern ptn = Pattern.compile(equalStr);
            Matcher m = ptn.matcher(line);
            if (m.matches()) {
                return JSONArray.parseArray(m.group(1) + "]");
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public final static String getT120Value(String key, String line) {
        String equalStr = ".*[,& ]" + key + ":([^\";&, ]+).*";
        try {
            Pattern ptn = Pattern.compile(equalStr);
            Matcher m = ptn.matcher(line);
            if (m.matches()) {
                return m.group(1);
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public final static String getTimeValue(String line) {
        String equalStr = ".*[_ ]([0-9]{2}:[0-9]{2}:[0-9]{2}).*";
        try {
            Pattern ptn = Pattern.compile(equalStr);
            Matcher m = ptn.matcher(line);
            if (m.matches()) {
                return m.group(1);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public final static boolean isTmUdid(String udid) {
        if (Strings.isNullOrEmpty(udid) || udid.length() != 16 || !udid.startsWith("91")) {
            return false;
        }
        return true;
    }

    public final static boolean isE1Udid(String udid) {
        if (Strings.isNullOrEmpty(udid) || udid.length() != 16 || !udid.startsWith("913")) {
            return false;
        }
        return true;
    }
    public final static boolean isOldUdid(String udid) {
        if (Strings.isNullOrEmpty(udid) || udid.length() != 16 || udid.startsWith("912")|| udid.startsWith("911")) {
            return true;
        }
        return false;
    }

    public final static LogType getLogType(String line) {
        String udid = getEqualValue("udid", line);
        if (line.contains("RECV from timo")) {
            if (isTmUdid(udid)) {
                return LogType.RECEIVE_FROM_TIMO;
            }
            return LogType.RECEIVE_FROM_CLIENT;
        } else if (line.contains("SEND to client")) {
            if (isTmUdid(udid)) {
                return LogType.SEND_TO_TIMO;
            }
            return LogType.SEND_TO_CLIENT;
        } else {
            return null;
        }
    }
}

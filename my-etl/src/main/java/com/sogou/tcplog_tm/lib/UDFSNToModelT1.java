package com.sogou.tcplog_tm.lib;

import com.google.common.base.Strings;

public class UDFSNToModelT1 {
    public static String evaluate(String sn) {
        try {
            if (Strings.isNullOrEmpty(sn)) {
                return "T1";
            }
            if(sn.length()!=16)return "T1";
            if(!sn.startsWith("91"))return "T1";
            if(sn.startsWith("911"))return "T1";
            else if (sn.startsWith("912"))return "T2";
            else if (sn.startsWith("913"))return "E1";
            else if (sn.startsWith("914"))return "M1";
            else return "T1";

        } catch (Exception e) {
            return "T1";
        }
    }
    public static void main(String[] args) {
        UDFSNToModelT1 udf = new UDFSNToModelT1();
        System.out.println(udf.evaluate("9122210710070561"));
        System.out.println(udf.evaluate(""));
    }
}

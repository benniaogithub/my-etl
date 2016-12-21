package com.sogou.gooseek;

import java.util.HashMap;
import java.util.Map;

/**
 *  抓取规则
 */
public class ExtractRules {
    public static Map<String, String> xmlRuleDic = new HashMap<String, String>();

    static {
        /* 规则文件___箱名称：数据库表名___日期规则,日期字段___URL参数*/
        xmlRuleDic.put("taobao_shop_data_2_2___详情", "teemo_extract_pinpai_detail___1,dateType___device,brandId");
        xmlRuleDic.put("taobao_shop_data_2_2___品牌趋势", "teemo_extract_pinpai_paynum___1,dateType___device,brandId");
        xmlRuleDic.put("taobao_shop_data_2___图形", "teemo_extract_shop_paynum___1,dateType___device,brandId,itemId");
        xmlRuleDic.put("taobao_shop_data_2___PC端流量来源", "teemo_extract_shop_source_pc___1,dateType___device,brandId,itemId");
        xmlRuleDic.put("taobao_shop_data_2___无线端流量来源", "teemo_extract_shop_source_mobile___1,dateType___device,brandId,itemId");
        xmlRuleDic.put("taobao_shop_data_2___PC端引流关键词", "teemo_extract_shop_flow_pc___1,dateType___device,brandId,itemId");
        xmlRuleDic.put("taobao_shop_data_2___无线端引流关键词", "teemo_extract_shop_flow_mobile___1,dateType___device,brandId,itemId");
        xmlRuleDic.put("taobao_shop_data_2___PC端成交关键词", "teemo_extract_shop_traid_pc___1,dateType___device,brandId,itemId");
        xmlRuleDic.put("taobao_shop_data_2___无线端成交关键词", "teemo_extract_shop_traid_mobile___1,dateType___device,brandId,itemId");
    }
}
package com.sogou.gooseek;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sogou.util.DaoUtil;
import com.sogou.util.ExtractUtil;
import com.sogou.util.TimeUtil;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 解析xml，并入库
 */
public class XmlEtl {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlEtl.class);

    public static String xmlPath = "C:\\Users\\Administrator\\DataScraperWorks";
    //public static String xmlPath = "C:\\Users\\admin\\DataScraperWorks";
    public static String dealFlag = "done";
    public static String fileFlag = ".xml";
    public static String ruleFlag = "___";

    public List<File> getAllDealFile() {
        File file = new File(xmlPath);

        File[] tempList = file.listFiles();
        if (tempList == null) {
            return Lists.newArrayList();
        }
        List<File> files = Lists.newArrayList();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                continue;
            }
            int flag = 0;
            for (String key : ExtractRules.xmlRuleDic.keySet()) {
                if (tempList[i].getName().equals(key.split(ruleFlag)[0])) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                continue;
            }
            File[] fileArray = tempList[i].listFiles();
            if (fileArray == null) {
                continue;
            }
            for (int j = 0; j < fileArray.length; j++) {
                if (fileArray[j].isFile() && fileArray[j].getName().contains(fileFlag) && !fileArray[j].getName().contains(dealFlag)) {
                    files.add(fileArray[j]);
                }
            }
        }
        return files;
    }

    public boolean renameFile(File file) {
        String filepath = file.getParentFile().getAbsolutePath();
        String filename = file.getName();
        return file.renameTo(new File(filepath + "/" + filename.split("\\.")[0] + dealFlag + "." + filename.split("\\.")[1]));
    }

    public void etlFile(File file) {
        Document documentroot;
        String filename = file.getName();
        String[] filesplit = filename.split("_");
        int lastlength = filesplit[filesplit.length - 1].length() + filesplit[filesplit.length - 2].length() + 2;
        String subFilename = filename.substring(0, filename.length() - lastlength);
        SAXReader reader = new SAXReader();
        DefaultElement document;
        try {
            documentroot = reader.read(file);
            document = ((DefaultElement) documentroot.selectNodes("extraction").get(0));
        } catch (Exception e) {
            return;
        }
        for (String key : ExtractRules.xmlRuleDic.keySet()) {
            if (subFilename.equals(key.split(ruleFlag)[0])) {
                String ruleValue = ExtractRules.xmlRuleDic.get(key);
                LOGGER.info("match key:" + key + "  value:" + ruleValue);
                String sql = "replace into " + ruleValue.split(ruleFlag)[0] + " values(";
                String baseUrl = ((DefaultElement) document.selectNodes("fullpath").get(0)).getStringValue();
                if (Strings.isNullOrEmpty(baseUrl)) {
                    continue;
                }
                //解析日期
                String cdate = getXmlDate(document, ruleValue, baseUrl);
                if (Strings.isNullOrEmpty(cdate)) {
                    LOGGER.info("extract cdate xml null");
                    continue;
                }
                //解析url上参数
                String urlData = getXmlUrlData(ruleValue, baseUrl);
                if (Strings.isNullOrEmpty(urlData)) {
                    LOGGER.info("extract urldata xml null");
                    continue;
                }
                DefaultElement element = (DefaultElement) document.selectNodes(key.split(ruleFlag)[1]).get(0);
                List<DefaultElement> objliucun = element.selectNodes("item");
                for (DefaultElement item : objliucun) {
                    List<DefaultElement> dataitems = item.elements();
                    String itemsql = sql + "'" + cdate + "'," + urlData;
                    for (DefaultElement dataitem : dataitems) {
                        if (Strings.isNullOrEmpty(dataitem.getStringValue())) {
                            itemsql = "";
                            LOGGER.info("extract main xml null:" + objliucun.toString());
                            break;
                        }
                        itemsql = itemsql + "'" + dataitem.getStringValue() + "',";
                    }
                    if (!Strings.isNullOrEmpty(itemsql)) {
                        itemsql = itemsql.substring(0, itemsql.length() - 1);
                        itemsql += ");";
                        try {
                            LOGGER.info("excute sql:" + itemsql);
                            DaoUtil.online_master.execute(itemsql);
                        } catch (Exception e) {
                            LOGGER.error("execute error:", e);
                        }
                    }
                }
            }
        }

    }

    public String getXmlDate(DefaultElement document, String rule, String url) {
        String flag = rule.split(ruleFlag)[1].split(",")[0];
        String datekey = rule.split(ruleFlag)[1].split(",")[1];
        //url里有recent，取前一天日期
        if (flag.equals("1")) {
            String dateValue = ExtractUtil.getEqualValue(datekey, url);
            if (dateValue.equals("recent1")) {
                String strDate = ((DefaultElement) document.selectNodes("createdate").get(0)).getStringValue();
                if (!Strings.isNullOrEmpty(strDate)) {
                    return TimeUtil.formatDate(TimeUtil.getOffsetDateByNum(TimeUtil.parseDate(strDate, TimeUtil.YYYY_M_D_H_M_S), -1), TimeUtil.YYYYMMDD);
                }
            }
        }
        return null;
    }

    public String getXmlUrlData(String rule, String url) {
        String[] urlKey = rule.split(ruleFlag)[2].split(",");
        String res = "";
        for (int i = 0; i < urlKey.length; i++) {
            String item = ExtractUtil.getEqualValue(urlKey[i], url);
            if (Strings.isNullOrEmpty(item)) {
                return null;
            }
            res = res + "'" + item + "',";
        }
        return res;
    }

    public static void main(String[] args) {
        while (true) {
            LOGGER.info("etl service start");
            XmlEtl xmlEtl = new XmlEtl();
            List<File> files = xmlEtl.getAllDealFile();
            for (File file : files) {
                LOGGER.info("etl filename:" + file.getName());
                xmlEtl.etlFile(file);
                xmlEtl.renameFile(file);
            }
            LOGGER.info("etl service end");
            try {
                Thread.sleep(8 * 60 * 60 * 1000);
            } catch (InterruptedException e) {
                LOGGER.error("sleep error");
                e.printStackTrace();
            }
        }
    }
}

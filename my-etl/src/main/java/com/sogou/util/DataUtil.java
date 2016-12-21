package com.sogou.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 时间相关的工具类
 */
public final class DataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataUtil.class);

    private DataUtil() {

    }
    private final static SecureRandom RANDOM = new SecureRandom();

    public static Integer generateSmsCode() {
        return RANDOM.nextInt(899999) + 100000;
    }

}

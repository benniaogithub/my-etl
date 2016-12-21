package com.sogou.tcplog_tm.lib;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by liuqin212173 on 2016/11/23.
 */
public class MyMapperTest {
    private TcpLogMapper mapper;


    public MyMapperTest() {
        mapper = new TcpLogMapper();
    }
    @Test
    public void testMap() throws Exception {
        Mapper.Context context = mock(Mapper.Context.class);
        Object key = mock(Object.class);
        Text text = new Text("2016-10-30_00:00:02 /timo/workspace/source/upd-timo-server/src/client/log_router.go client.logRoute(20) [INF] - RECV from timo, stamp=1477756802268, ipPort=117.136.77.116:7135, userId=1832798, babyId=1831773, udid=9142021010002571 type=120, packet={\"date\":20161029,\"gps_succ_cnt\":351,\"gps_fail_cnt\":0,\"wifi_succ_cnt\":0,\"wifi_fail_cnt\":152,\"cell_cnt\":113,\"login_fail_cnt\":0,\"gsensor_fail_cnt\":0,\"sport_access_cnt\":1,\"mf_access_cnt\":0,\"inmsg_cnt\":0,\"outmsg_cnt\":0,\"vmsg_play_cnt\":0,\"incall_cnt\":0,\"outcall_cnt\":3,\"alarm_cnt\":0,\"alarm_ack_cnt\":0,\"ul_data_flow\":252509,\"dl_data_flow\":1314009,\"wifi_ul_data_flow\":0,\"wifi_dl_data_flow\":0,\"game_forbid_in_school_cnt\":0,\"game_forbid_over_count_cnt\":0,\"wifi_conn_cnt\":0,\"wifi_conn_succ\":0,\"wifi_conn_scan_none\":0,\"wifi_auth_cnt\":0,\"wifi_auth_succ\":0,\"camera_enter_cnt\":1,\"camera_capture_cnt\":6,\"use_sticker_cnt\":0,\"camera_effect_cnts\":[0,6,0,0,0,0],\"vibration_enter_cnt\":0,\"stopwatch_enter_cnt\":1,\"theme_enter_cnt\":0,\"swatch_enter_cnt1\":0,\"swatch_enter_cnt2\":0,\"sticker_detail_cnt\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]}");
        mapper.map(key,text,context);

    }
}
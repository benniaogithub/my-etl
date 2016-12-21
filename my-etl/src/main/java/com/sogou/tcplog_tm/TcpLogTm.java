package com.sogou.tcplog_tm;

import com.google.common.collect.Lists;
import com.hadoop.mapreduce.LzoTextInputFormat;
import com.sogou.tcplog_tm.lib.TcpLogMapper;
import com.sogou.util.LogReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.List;

/**
 * TCP日志入口程序
 */
public class TcpLogTm implements Tool {

    private Configuration conf;

    private static List<String> filename = Lists.newArrayList(
            "photo"
    );

    @Override
    public int run(String[] args) throws Exception {
        Job job = configureJob(args);
        return (job.waitForCompletion(true) ? 0 : 1);
    }

    private Job configureJob(String[] args) throws IOException {
        Job job = new Job(conf, "dc etl");
        // 必需：指定当前程序所在的jar文件。
        // 如果不调用该函数，hadoop机群上的map/reduce可能会运行失败。
        job.setJobName("etl:tcllog_tm");
        job.setJarByClass(TcpLogTm.class);
        // 必需：指定mapper类
        job.setMapperClass(TcpLogMapper.class);
        job.setReducerClass(LogReducer.class);
        job.setInputFormatClass(LzoTextInputFormat.class);
        job.setOutputKeyClass(Text.class);
        // 必需：指定reduce输出的Value的类型
        job.setOutputValueClass(Text.class);
        for (String item : filename) {
            MultipleOutputs.addNamedOutput(job, item, SequenceFileOutputFormat.class, BytesWritable.class,
                    Text.class);
        }
        // 必需：指定输入路径
        FileInputFormat.addInputPath(job, new Path(args[0]));
        // 必需：指定输出路径
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        return job;
    }

    @Override
    public Configuration getConf() {
        return conf;
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    /**
     * Main
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        // 推荐用ToolRunner来运行Map-Reduce，这样 可以方便的支持-libjars/-jt等通用参数
        ToolRunner.run(conf, new TcpLogTm(), args);
    }

}

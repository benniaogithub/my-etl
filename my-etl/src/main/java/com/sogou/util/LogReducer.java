package com.sogou.util;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import java.io.IOException;

public class LogReducer extends
        Reducer<Text, Text, BytesWritable, Text> {
    private MultipleOutputs<BytesWritable, Text> mos;

    public void reduce(Text key, Iterable<Text> values,
                       Context context) throws IOException, InterruptedException {
        String headname = "";
        String keys = "";
        headname = key.toString().split("\\$\\$\\$")[0];
        keys = key.toString().split("\\$\\$\\$")[1];
//        System.out.println(values);
        for (Text value : values) {
            mos.write(headname, new BytesWritable(), new Text(keys));
        }
    }

    @Override
    protected void setup(Context context) throws IOException,
            InterruptedException {
        mos = new MultipleOutputs<BytesWritable, Text>(context);
        super.setup(context);
    }

    @Override
    protected void cleanup(Context context) throws IOException,
            InterruptedException {
        mos.close();
        super.cleanup(context);
    }
}
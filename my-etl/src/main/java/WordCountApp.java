import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class WordCountApp {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = new Job(conf, WordCountApp.class.getSimpleName());
        job.setJarByClass(WordCountApp.class);
        // TODO: specify a mapper
        job.setMapperClass(MyMapper.class);
        // TODO: specify a reducer
        job.setReducerClass(MyReducer.class);

        // TODO: specify output types
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        // TODO: specify input and output DIRECTORIES (not files)
        FileInputFormat.setInputPaths(job, new Path("hdfs://localhost:9000/input/"));
        FileOutputFormat.setOutputPath(job, new Path("my-etl/output/result3.txt"));

        if (!job.waitForCompletion(true))
            return;
    }

    public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable>{
        Text k2 = new Text();
        LongWritable v2 = new LongWritable();
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] split = value.toString().split(" ");
            for (String word : split) {
                k2.set(word);
                v2.set(1);
                context.write(k2, v2);
            }
        }
    }
    
    public  static class MyReducer extends Reducer<Text, LongWritable, Text, LongWritable>{
        long sum = 0;
        @Override
        protected void reduce(Text k2, Iterable<LongWritable> v2s,
                Context context) throws IOException, InterruptedException {
            for (LongWritable one : v2s) {
                sum+=one.get();
            }
            context.write(k2, new LongWritable(sum));
        }
    }
    
}
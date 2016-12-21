package com.sogou;

import java.io.*;
import java.util.*;

import com.sogou.location_log.lib.LocationLogMapper;
import com.sogou.tcplog_tm.lib.TcpLogMapper;
import com.sogou.util.LogReducer;
import org.apache.hadoop.mrunit.mapreduce.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mrunit.types.Pair;
import org.apache.hadoop.io.Text;


public class TcpLogTmTest {
    private Mapper mapper;
    private Reducer reducer;
    private MapReduceDriver driver;


    public TcpLogTmTest(Mapper _map, Reducer _reducer) {
        mapper = _map;
        reducer = _reducer;
        driver = new MapReduceDriver(mapper, reducer);
    }

    public void test(String fileIn, String fileOut) throws RuntimeException,
            IOException {
        ReadFromFile(fileIn);
        List<Pair> out = driver.run();

        for (Pair pair : out) {
            System.out.print(pair.getFirst() + "  ");
            System.out.println(pair.getSecond());
        }
        //WriteToFile(out, fileOut);
    }

    public void ReadFromFile(String fileName) {
        String line = "";
        File file = new File(fileName);

        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(file));
            while ((line = input.readLine()) != null)
                driver.withInput(new Text(""), new Text(line));
        } catch (IOException ioException) {
            System.err.println("File Open Failed!");
            ioException.printStackTrace();
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void WriteToFile(List<Pair> listPairs, String fileName) {
        File out = new File(fileName);
        if (out.exists())
            out.delete();
        try {
            if (out.createNewFile()) {
                BufferedWriter output = new BufferedWriter(new FileWriter(out));
                for (Pair pair : listPairs) {
                    output.write(pair.getFirst() + " - " + pair.getSecond()
                            + "\r\n");
                }
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Mapper myMap = new TcpLogMapper();// 2 **Change map class name**
        Reducer myReducer = new LogReducer();// 3 **Change reducer class
        // name**
        String fileIn = "F://locationdata (2)";// 4 **Change input file name**
        String fileOut = "F://output.txt";// 5 **Change output file name**
        //String aa="androidmap$$$1010286450749408	11:10:01	1	701	layer~1,at~,from~normal,traffic~0,t~1010286601392,am~normal	none	none	none	none		355318054213076	2	701	20020106";
        try {
            //System.out.println(aa.split(new String("\\$\\$\\$"))[1]);
            TcpLogTmTest tester = new TcpLogTmTest(myMap, myReducer);
            tester.test(fileIn, fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

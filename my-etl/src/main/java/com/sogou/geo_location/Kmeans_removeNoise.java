package com.sogou.geo_location;


import java.io.*;

/**
 * Created by liuqin212173 on 2016/12/8.
 * 删除一定比例的离群点
 */


//K-means算法实现

public class Kmeans_removeNoise{

    //样本数目（测试集）
    static int InstanceNumber ;
    //样本属性数目（测试）
    static int FieldCount ;

    //设置异常点阈值参数（数目为InstanceNumber*t）
    static double t;
    //存放数据的矩阵
    private double[][] data;

    //均值中心
    private double[] meanData;


    //构造函数，初始化
    public void init(int _InstanceNumber,int _FieldCount,double _t )
    {
        //最后一位用来储存结果

        InstanceNumber = _InstanceNumber;
        FieldCount = _FieldCount;
        t = _t;
        data = new double[InstanceNumber][FieldCount+1];
        meanData = new double[_FieldCount];


    }


    /**
     * 主函数入口
     * 每一行为一个样本，有2个属性
     * 主要分为两个步骤
     * 1.读取数据
     * 2.进行聚类

     */

    /*
     * 读取测试集的数据
     *
     * @param trainingFileName 测试集文件名
     */
    public void readData(String trainingFileName)
    {
        try
        {
            FileReader fr = new FileReader(trainingFileName);
            BufferedReader br = new BufferedReader(fr);
            //存放数据的临时变量
            String lineData = null;
            String[] splitData = null;
            int line = 0;
            //按行读取
            while(br.ready())
            {
                //得到原始的字符串
                lineData = br.readLine();
                splitData = lineData.split("\t");
                //转化为数据
//        System.out.println("length:"+splitData.length);

                if(splitData.length>1)
                {
                    for(int i = 0;i < FieldCount;i++)
                    {
                        //将数据截取之后放进数组
                        data[line][i] = Double.parseDouble(splitData[i]);

                    }
                    data[line][FieldCount] = 0;
                    line++;

                }
            }

        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }


    public void Adjust()
    {
        boolean flag = true;
        double[] dis = new double[InstanceNumber];
        while(flag) {
            int count = 0;
            double[] sumData = new double[FieldCount];
            for(int j=0;j<InstanceNumber;j++){
                if(data[j][FieldCount]==0){
                    count ++;
                    for(int t=0;t<FieldCount;t++){
                        sumData[t] += data[j][t];
                    }
                }
            }
            for(int t=0;t<FieldCount;t++){
                meanData[t] = sumData[t]/count;
//                System.out.println(sumData[t]);
//                System.out.println(count);
//                System.out.println(meanData[t]);
            }
            double distanceSum  = 0;
            for(int j=0;j<InstanceNumber;j++){
                if(data[j][FieldCount]==0){
                    dis[j] = DistanceLatLon.LantitudeLongitudeDist(meanData[0],meanData[1],data[j][0],data[j][1]);
                    distanceSum += dis[j];
                }
            }
            distanceSum /= count;
            int index = -1;
//            System.out.println(distanceSum);
            for(int j=0;j<InstanceNumber;j++){
                if(data[j][FieldCount]==0){
                    if(dis[j]>distanceSum)
                    {
                        distanceSum = dis[j];
                        index = j;
                    }
                }
            }
            data[index][FieldCount] = 1;
//            System.out.println(index);
            float rmRatio = (float) (count-1)/InstanceNumber;
            if(rmRatio<=t){
                flag = false;
            }
        }

    }

    public void reqSchool(){
        HttpRequestJson hrj = new HttpRequestJson();
        for(int j=0;j<InstanceNumber;j++){
            if(data[j][FieldCount]==0){
                hrj.httpGet(data[j][0],data[j][1]);
            }
        }
        hrj.getMain();
    }

    /**
     * 将结果输出到一个文件中
     *
     * @param outputfileName
     */
    public void printResult(String outputfileName)
    {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(outputfileName);
            bw = new BufferedWriter(fw);
            //写入文件
            for (int i = 0; i < InstanceNumber; i++) {
                if (data[i][FieldCount] == 0) {
                    bw.write(String.valueOf(data[i][FieldCount]).substring(0, 1));
                    bw.newLine();
                }
            }
            //关闭资源
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (fw != null)
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Kmeans_removeNoise krn = new Kmeans_removeNoise(
        );
        krn.init(90,2,0.65);
        krn.readData("G:\\tmp.data");
        krn.Adjust();
        krn.reqSchool();
    }
}




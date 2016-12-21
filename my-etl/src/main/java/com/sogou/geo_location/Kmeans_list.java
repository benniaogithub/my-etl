package com.sogou.geo_location;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Created by liuqin212173 on 2016/12/8.
 * 删除一定比例的离群点
 */

public class Kmeans_list {

    public static List<ArrayList<ArrayList<Double>>>
    initHelpCenterList(List<ArrayList<ArrayList<Double>>> helpCenterList,int k){
        for(int i=0;i<k;i++){
            helpCenterList.add(new ArrayList<ArrayList<Double>>());
        }
        return helpCenterList;
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        List<ArrayList<Double>> centers = new ArrayList<ArrayList<Double>>();
        List<ArrayList<Double>> newCenters = new ArrayList<ArrayList<Double>>();   //每个样本是个list ,聚类中心列表就是列表嵌套一层列表
        List<ArrayList<ArrayList<Double>>> helpCenterList = new ArrayList<ArrayList<ArrayList<Double>>>();   //属于每个聚类的点放到同一个list，三层列表

        //读入原始数据
        BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream("src/datafile/abaloneNoLabel.data")));
        String data = null;
        List<ArrayList<Double>> dataList = new ArrayList<ArrayList<Double>>();
        while((data=br.readLine())!=null){
            //System.out.println(data);
            String []fields = data.split(",");
            List<Double> tmpList = new ArrayList<Double>();
            for(int i=0; i<fields.length;i++)
                tmpList.add(Double.parseDouble(fields[i]));
            dataList.add((ArrayList<Double>) tmpList);
        }
        br.close();

        //随机确定K个初始聚类中心
        Random rd = new Random();
        int k=3;
        int [] initIndex={59,71,48};
        int [] helpIndex = {0,59,130};
        int [] givenIndex = {0,1,2};
        System.out.println("random centers' index");
        for(int i=0;i<k;i++){
            int index = rd.nextInt(initIndex[i]) + helpIndex[i];
            //int index = givenIndex[i];
            System.out.println("index "+index);
            centers.add(dataList.get(index));
            helpCenterList.add(new ArrayList<ArrayList<Double>>());
        }

        //输出k个初始中心
        System.out.println("original centers:");
        for(int i=0;i<k;i++)
            System.out.println(centers.get(i));

        while(true)
        {//进行若干次迭代，直到聚类中心稳定

            for(int i=0;i<dataList.size();i++){//标注每一条记录所属于的中心
                double minDistance=99999999;
                int centerIndex=-1;
                for(int j=0;j<k;j++){//离0~k之间哪个中心最近
                    double currentDistance=0;
                    for(int t=1;t<centers.get(0).size();t++){//计算两点之间的欧式距离
                        currentDistance +=  ((centers.get(j).get(t)-dataList.get(i).get(t))/(centers.get(j).get(t)+dataList.get(i).get(t))) * ((centers.get(j).get(t)-dataList.get(i).get(t))/(centers.get(j).get(t)+dataList.get(i).get(t)));
                    }
                    if(minDistance>currentDistance){
                        minDistance=currentDistance;
                        centerIndex=j;
                    }
                }
                helpCenterList.get(centerIndex).add(dataList.get(i));
            }

            //  System.out.println(helpCenterList);

            //计算新的k个聚类中心
            for(int i=0;i<k;i++){

                ArrayList<Double> tmp = new ArrayList<Double>();

                for(int j=0;j<centers.get(0).size();j++){
                    double sum=0;
                    for(int t=0;t<helpCenterList.get(i).size();t++)
                        sum+=helpCenterList.get(i).get(t).get(j);
                    tmp.add(sum/helpCenterList.get(i).size());
                }

                newCenters.add(tmp);

            }
            System.out.println("\nnew clusters' centers:\n");
            for(int i=0;i<k;i++)
                System.out.println(newCenters.get(i));
            //计算新旧中心之间的距离，当距离小于阈值时，聚类算法结束
            double distance=0;

            for(int i=0;i<k;i++){
                for(int j=1;j<centers.get(0).size();j++){//计算两点之间的欧式距离
                    distance += ((centers.get(i).get(j)-newCenters.get(i).get(j))/(centers.get(i).get(j)+newCenters.get(i).get(j))) * ((centers.get(i).get(j)-newCenters.get(i).get(j))/(centers.get(i).get(j)+newCenters.get(i).get(j)));
                }
                //System.out.println(i+" "+distance);
            }
            System.out.println("\ndistance: "+distance+"\n\n");
            if(distance==0)//小于阈值时，结束循环
                break;
            else//否则，新的中心来代替旧的中心，进行下一轮迭代
            {
                centers = new ArrayList<ArrayList<Double>>(newCenters);
                //System.out.println(newCenters);
                newCenters = new ArrayList<ArrayList<Double>>();
                helpCenterList = new ArrayList<ArrayList<ArrayList<Double>>>();
                helpCenterList=initHelpCenterList(helpCenterList,k);
            }
        }
        //输出最后聚类结果
        for(int i=0;i<k;i++){
            System.out.println("\n\nCluster: "+(i+1)+"   size: "+helpCenterList.get(i).size()+" :\n\n");
            for(int j=0;j<helpCenterList.get(i).size();j++)
            {
                //  System.out.println(helpCenterList.get(i).get(j));
            }
        }
        int dataSetLength=dataList.size();
        double[][] distance = new double[k][dataSetLength];
        double[] distanceSum=new double[k];
        for(int j=0;j<k;j++)
        {
            for (int i = 0; i < helpCenterList.get(j).size(); i++) {

                distance[j][i] = distance(helpCenterList.get(j).get(i), centers.get(j));
                distanceSum[j]+= distance[j][i];
            }
            //每个簇的平均距离
            distanceSum[j]/= helpCenterList.get(j).size();
            System.err.println(distanceSum[j]);
            double radius=distanceSum[j]+1.5*Standardlizerdistance(distance[j],distanceSum[j]);
            System.err.println("\n\nCluster: "+(j+1)+" 每个簇的正常半径，即阀值（此程序阀值为每个簇的平均距离与1.5倍标准差之和为： "+radius);
            for(int i=0;i<helpCenterList.get(j).size();i++)
            {
                if(distance[j][i]>radius)
                {
                    System.err.println(helpCenterList.get(j).get(i)+"离群 "+distance[j][i]);
                }
//                  else
//                      System.out.println(helpCenterList.get(j).get(i)+"正常半径： "+distance[j][i]);
            }

        }

    }
    private static double distance(ArrayList<Double> element, ArrayList<Double> center) {
        double currentDistance=0;

        for(int t=1;t<element.size();t++){//计算两点之间的欧式距离
            currentDistance +=  (element.get(t)-center.get(t))*(element.get(t)-center.get(t));
        }

        return Math.sqrt(currentDistance);
    }
    private static double Standardlizerdistance(double[] distance, double x) {
        double currentDistance=0;

        for(int t=1;t<distance.length;t++){//计算两点之间的欧式距离
            currentDistance +=  (distance[t]-x)*(distance[t]-x);
        }

        return Math.sqrt(currentDistance/distance.length);
    }
}
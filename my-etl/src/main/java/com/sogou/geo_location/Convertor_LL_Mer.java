package com.sogou.geo_location;


/**
 * User: IvyTang
 * Date: 14-8-20
 * Time: AM11:03
 */
public class Convertor_LL_Mer  {
  public static final double EARTHRADIUS = 6370996.81;
  static final int DIR_LL2MER = 0;
  static final int DIR_MER2LL = 1;

  public static void main(String[] args) {
    Convertor_LL_Mer udfMapToStr = new Convertor_LL_Mer();
    System.out.println(udfMapToStr.evaluate("Mer2LL",9717665.0390625,5439013.671875));
  }

  public String evaluate(String type, double x, double y) {

    double[] result = new double[] { 0, 0 };
    if (type.equals("Mer2LL")) {
      result = ConvertCoord(x, y, DIR_MER2LL);
    } else if (type.equals("LL2Mer")) {
      result = ConvertCoord(x, y, DIR_LL2MER);
    }
    return result[0] + "," + result[1];

  }

  private static double[] ConvertCoord(double x, double y, int direction) {
    double[] ret = new double[2];

    boolean isEast = true;
    boolean isNorth = true;

    int i;
    double temp;

    double[] band;
    Factor4Band[] factor;
    Factor4Band which;

    //如果传入的坐标不位于“东经、北纬”的第一象限，将其映射到第一象限
    if (x < 0) {
      x = -1 * x;
      isEast = false;
    }
    if (y < 0) {
      y = -1 * y;
      isNorth = false;
    }

    //根据转换的方向设定变量
    if (direction == DIR_MER2LL) {
      band = Factor4Band.MERBAND;
      factor = Factor4Band.MER2LL;
    } else {
      band = Factor4Band.LATBAND;
      factor = Factor4Band.LL2MER;
    }

    //判断传入的坐标位于哪一个度带，以此来决定使用哪一组参数
    i = 0;
    which = factor[0]; //随便给个初值，否则编译不过去
    while (band[i] != -1) {
      if (y > band[i]) {
        which = factor[i];
        break;
      }
      i++;
    }

    //计算经纬度坐标
    ret[0] = which.fx0 + which.fx1 * x;

    temp = y / which.fmy;
    ret[1] = which.fy0 + which.fy1 * temp + which.fy2 * temp * temp + which.fy3 * temp * temp * temp + which.fy4
            * temp * temp * temp * temp + which.fy5 * temp * temp * temp * temp * temp + which.fy6 * temp * temp
            * temp * temp * temp * temp;

    //将计算出的经纬度坐标反向映射回所处象限
    if (!isEast) {
      ret[0] = -1 * ret[0];
    }
    if (!isNorth) {
      ret[1] = -1 * ret[1];

    }
    return ret;
  }


}


class Factor4Band {
  //定义纬度带。最后一个-1是为了循环时判断终止条件的方便
  static final double LATBAND_75 = 75;
  static final double LATBAND_60 = 60;
  static final double LATBAND_45 = 45;
  static final double LATBAND_30 = 30;
  static final double LATBAND_15 = 15;
  static final double LATBAND_00 = 0;
  public static final double[] LATBAND = { LATBAND_75, LATBAND_60, LATBAND_45, LATBAND_30, LATBAND_15, LATBAND_00, -1 };

  //定义墨卡托纵坐标带
  static final double MERBAND_75 = 12890594.86;
  static final double MERBAND_60 = 8362377.87;
  static final double MERBAND_45 = 5591021;
  static final double MERBAND_30 = 3481989.83;
  static final double MERBAND_15 = 1678043.12;
  static final double MERBAND_00 = 0;
  public static final double[] MERBAND = { MERBAND_75, MERBAND_60, MERBAND_45, MERBAND_30, MERBAND_15, MERBAND_00, -1 };

  //多项式的系数
  double fx0;
  //多项式的系数
  double fx1;
  //多项式的系数
  double fy0;
  //多项式的系数
  double fy1;
  //多项式的系数
  double fy2;
  //多项式的系数
  double fy3;
  //多项式的系数
  double fy4;
  //多项式的系数
  double fy5;
  //多项式的系数
  double fy6;
  //多项式的系数
  double fmy;

  //私有构造函数，确保无法创建此类的实例。无论Mer2LL或LL2Mer均使用这个构造函数
  private Factor4Band(double fx0, double fx1, double fy0, double fy1, double fy2, double fy3, double fy4, double fy5,
                      double fy6, double fmy) {
    this.fx0 = fx0;
    this.fx1 = fx1;
    this.fy0 = fy0;
    this.fy1 = fy1;
    this.fy2 = fy2;
    this.fy3 = fy3;
    this.fy4 = fy4;
    this.fy5 = fy5;
    this.fy6 = fy6;
    this.fmy = fmy;
  }

  //定义Mer2LL的转换参数
  public static final Factor4Band MER2LL_00 = new Factor4Band(2.890871144776878e-009, 8.983055095805407e-006,
          -0.00000003068298, 7.47137025468032, -0.00000353937994, -0.02145144861037, -0.00001234426596,
          0.00010322952773, -0.00000323890364, 8.260885000000000e+005);

  public static final Factor4Band MER2LL_15 = new Factor4Band(3.091913710684370e-009, 8.983055096812155e-006,
          0.00006995724062, 23.10934304144901, -0.00023663490511, -0.63218178102420, -0.00663494467273,
          0.03430082397953, -0.00466043876332, 2.555164400000000e+006);

  public static final Factor4Band MER2LL_30 = new Factor4Band(-1.981981304930552e-008, 8.983055099779535e-006,
          0.03278182852591, 40.31678527705744, 0.65659298677277, -4.44255534477492, 0.85341911805263,
          0.12923347998204, -0.04625736007561, 4.482777060000000e+006);

  public static final Factor4Band MER2LL_45 = new Factor4Band(-3.030883460898826e-008, 8.983055099835780e-006,
          0.30071316287616, 59.74293618442277, 7.35798407487100, -25.38371002664745, 13.45380521110908,
          -3.29883767235584, 0.32710905363475, 6.856817370000000e+006);

  public static final Factor4Band MER2LL_60 = new Factor4Band(-7.435856389565537e-009, 8.983055097726239e-006,
          -0.78625201886289, 96.32687599759846, -1.85204757529826, -59.36935905485877, 47.40033549296737,
          -16.50741931063887, 2.28786674699375, 1.026014486000000e+007);

  public static final Factor4Band MER2LL_75 = new Factor4Band(1.410526172116255e-008, 8.983055096488720e-006,
          -1.99398338163310, 2.009824383106796e+002, -1.872403703815547e+002, 91.60875166698430, -23.38765649603339,
          2.57121317296198, -0.03801003308653, 1.733798120000000e+007);

  public static final Factor4Band[] MER2LL = { MER2LL_75, MER2LL_60, MER2LL_45, MER2LL_30, MER2LL_15, MER2LL_00 };

  //定义LL2Mer的转换参数
  public static final Factor4Band LL2MER_00 = new Factor4Band(-3.218135878613132e-004, 1.113207020701615e+005,
          0.00369383431289, 8.237256402795718e+005, 0.46104986909093, 2.351343141331292e+003, 1.58060784298199,
          8.77738589078284, 0.37238884252424, 7.45000000000000);

  public static final Factor4Band LL2MER_15 = new Factor4Band(-3.441963504368392e-004, 1.113207020576856e+005,
          2.782353980772752e+002, 2.485758690035394e+006, 6.070750963243378e+003, 5.482118345352118e+004,
          9.540606633304236e+003, -2.710553267466450e+003, 1.405483844121726e+003, 22.50000000000000);

  public static final Factor4Band LL2MER_30 = new Factor4Band(0.00220636496208, 1.113207020209128e+005,
          5.175186112841131e+004, 3.796837749470245e+006, 9.920137397791013e+005, -1.221952217112870e+006,
          1.340652697009075e+006, -6.209436990984312e+005, 1.444169293806241e+005, 37.50000000000000);

  public static final Factor4Band LL2MER_45 = new Factor4Band(0.00337398766765, 1.113207020202162e+005,
          4.481351045890365e+006, -2.339375119931662e+007, 7.968221547186455e+007, -1.159649932797253e+008,
          9.723671115602145e+007, -4.366194633752821e+007, 8.477230501135234e+006, 52.50000000000000);

  public static final Factor4Band LL2MER_60 = new Factor4Band(8.277824516172526e-004, 1.113207020463578e+005,
          6.477955746671608e+008, -4.082003173641316e+009, 1.077490566351142e+010, -1.517187553151559e+010,
          1.205306533862167e+010, -5.124939663577472e+009, 9.133119359512032e+008, 67.50000000000000);

  public static final Factor4Band LL2MER_75 = new Factor4Band(-0.00157021024440, 1.113207020616939e+005,
          1.704480524535203e+015, -1.033898737604234e+016, 2.611266785660388e+016, -3.514966917665370e+016,
          2.659570071840392e+016, -1.072501245418824e+016, 1.800819912950474e+015, 82.50000000000000);

  public static final Factor4Band[] LL2MER = { LL2MER_75, LL2MER_60, LL2MER_45, LL2MER_30, LL2MER_15, LL2MER_00 };
}

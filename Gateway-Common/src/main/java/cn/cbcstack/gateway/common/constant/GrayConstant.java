package cn.cbcstack.gateway.common.constant;

public class GrayConstant {

    public static double MAX_GRAY_THRESHOLD = 0.95D; // 服务灰度的最大比例

    public static String THRESHOLD_GRAY_STRATEGY = "threshold_gray_strategy"; // 根据流量决定是否灰度的策略名

    public static String CLIENT_IP_GRAY_STRATEGY = "client_ip_gray_strategy"; // 根据用户ip决定是否灰度的策略名

}

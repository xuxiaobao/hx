package com.miaosu.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xxb on 2017/5/13.
 */
public class Constants {
    /*
    生产
     */
    public static String AppKey = "6d685cb0d75649e090ccc08f71413226";
    public static String AppSecret = "ffd75688431d47a6b03192685fa64b3f";
    public static String SERVER_URL = "http://www.nm.10086.cn/flowplat/";

    /*public static String AppKey = "b395e66de5d9458b8b8eeedd602ce7d3";
    public static String AppSecret = "592cf42e02304aeea3d2ca4208fc7923";
    public static String SERVER_URL = "https://pdata.4ggogo.com/web-in/";
*/
    public static Map<String, String> TokenMap = null;
    static {
        TokenMap = new HashMap<String, String>();
    }
}

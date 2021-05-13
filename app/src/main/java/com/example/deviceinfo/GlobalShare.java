package com.example.deviceinfo;

import java.util.HashMap;
import java.util.Map;

public class GlobalShare {
    public static Map<Integer,String> androidVersionName  = new HashMap<>();
    static {
        androidVersionName.put(14,"14, Ice Cream Sandwich");
        androidVersionName.put(15, "15, Ice Cream Sandwich");
        androidVersionName.put(16,"16, Jelly Bean");
        androidVersionName.put(17, "17, Jelly Bean");
        androidVersionName.put(18,"18, Jelly Bean");
        androidVersionName.put(19,"19, KitKat");
        androidVersionName.put(21,"21, Lollipop");
        androidVersionName.put(22,"22, Lollipop");
        androidVersionName.put(23,"23, Marshmallow");
        androidVersionName.put(24,"24, Nougat");
        androidVersionName.put(25,"25, Nougat");
        androidVersionName.put(26,"26, Oreo");
        androidVersionName.put(27,"27, Oreo");
        androidVersionName.put(28,"28, Pie");
        androidVersionName.put(29,"29, Android 10");
    }
}

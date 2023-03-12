package com.instantsystem.instantapp.api.utils;

import java.util.Map;

public class Utils {

    private Utils(){}

    public static boolean hasKeyAndValidValue(Map<String, Object> map, String key) {
        return map.containsKey(key) && map.get(key) != null && !map.get(key).equals("");
    }
}

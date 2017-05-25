package com.avatar.newarkescape;

import java.text.DecimalFormat;

/**
 * Created by chx on 2016/12/29.
 */

public class FormatUtils {

    private static DecimalFormat decimalFormat = new DecimalFormat("0.0");

    public static String float2String(float f) {
        String str = String.valueOf(f);
        int idx = str.lastIndexOf(".");
        if (idx == -1) {
            return "";
        }
        return str.substring(0,idx);
    }

    public static String float2StringWith1(float f) {
        return decimalFormat.format(f);
    }

}

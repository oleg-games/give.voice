package com.oleg.givevoice.utils;

public class PhoneUtils {
    public static String getPhoneNumber(String phone) {
        return phone.replaceAll("[^0-9]", "");
//                .replace("-", "")
//                .replace(" ", "")
//                .replace("(", "")
//                .replace(")", "")
//                .replace("+", "")
//                .trim();
    }
}

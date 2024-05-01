package com.gussbom.auth.utils;

import java.util.Random;

public class OtpGenerator {

    static Random random = new Random();
    static StringBuilder sb = new StringBuilder(6);
    public static String generate() {
        for (int i = 0; i < 7; i++) {
            int randomIndex = random.nextInt(9);
            sb.append(randomIndex);
        }
        return sb.toString();
    }
}

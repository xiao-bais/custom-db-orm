package com.custom.joiner.util;

import com.custom.comm.utils.CustomUtil;
import com.custom.comm.exceptions.ExThrowsUtil;

import java.util.Random;

/**
 * @author Xiao-Bai
 * @date 2022/8/28 21:18
 * 自定义无规则字符串构造器
 */
public class CustomCharUtil {

    private final static char[] NUMBERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',};

    /**
     * 基本字母
     */
    private final static char[] BASIC_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',

            'a' , 'b' ,
            'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
            'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
            'o' , 'p' , 'q' , 'r' , 's' , 't' ,
            'u' , 'v' , 'w' , 'x' , 'y' , 'z',

            'A' , 'B' ,
            'C' , 'D' , 'E' , 'F' , 'G' , 'H' ,
            'I' , 'J' , 'K' , 'L' , 'M' , 'N' ,
            'O' , 'P' , 'Q' , 'R' , 'S' , 'T' ,
            'U' , 'V' , 'W' , 'X' , 'Y' , 'Z'
    };


    /**
     * 前置指定部分字符
     */
    private final static char[] FIRST_CHARS = {'a' , 'b' , 'c' , 'd' , 'e' , 'f'};

    /**
     * 指定KEY
     */
    private final static long KEY = 185426L;


    /**
     * 生成一个字符串
     */
    public static String nextStr(int size) {
        if (size < 6) {
            ExThrowsUtil.toCustom("size must not be less than 6");
        }

        // 中间特殊符号的位置
        int centerIndex = (size % 2 == 0 ? size / 2 : size / 2 + 1) - 1;
        Random random = new Random();
        long currNumber = Long.parseLong(CustomUtil.reverse(
                String.valueOf(System.currentTimeMillis()))) / KEY;
        char[] result = new char[size];

        for (int i = 0; i < size; i++) {
            if (i == centerIndex) {
                result[i] = '_';
            }else {
                if (i > 0 && currNumber % i == 0) {
                    int index = (int) ( currNumber + i) % 6;
                    result[i] = FIRST_CHARS[index];
                } else {
                    if (i > centerIndex && i % 2 == 0) {
                        result[i] = NUMBERS[random.nextInt(NUMBERS.length)];
                    }
                    result[i] = BASIC_CHARS[random.nextInt(BASIC_CHARS.length)];
                }
            }
        }

        return new String(result);
    }


    public static void main(String[] args) {

//        for (int i = 0; i < 100; i++) {
//            String nextStr = nextStr(8);
//            System.out.println("nextStr = " + nextStr);
//        }


    }








}

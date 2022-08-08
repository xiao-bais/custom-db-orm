package com.home;

import java.util.regex.Pattern;

/**
 * @Author Xiao-Bai
 * @Date 2022/5/9 17:04
 * @Desc：
 **/
public class Mains {

    private static Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]*");

    public static void main(String[] args) {

        for (int i = 6; i >= 0; i--) {
            System.out.println(i);
        }


    }



}

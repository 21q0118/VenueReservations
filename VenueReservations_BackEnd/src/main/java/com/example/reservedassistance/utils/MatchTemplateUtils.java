package com.example.reservedassistance.utils;

import com.example.reservedassistance.service.impl.MessageServiceImpl;

public class MatchTemplateUtils {


    public static boolean matchesAny(String input) {
        String[] patterns = {
                "尊敬的(.*)您好：\n您预约的(.*)举办的(.*)活动暂时因(.*)而暂时无法继续开展，您的预约已为您自动取消，在此对您表示真诚的抱歉！！！",
                "尊敬的(.*)您好：\n您预约的(.*)举办的(.*)还有(.*)分钟后将开始预约，请您及时来展馆进行参观，谢谢配合",
                "尊敬的(.*)您好：\n您预约的(.*)举办的(.*)未按时检票，现在您当前信誉分为(.*),请遵守相关规定，具体详情可查看右上角用户预约手册",
                "尊敬的(.*)您好：\n您预约的(.*)举办的(.*)未在规定时间内检票，现在您当前信誉分为(.*),请遵守相关规定，具体详情可查看右上角用户预约手册",
                "尊敬的(.*)您好：\n您预约的(.*)举办的(.*)已于(.*)完成检票,祝您参观愉快"
        };

        for (String pattern : patterns) {
            if (match(pattern, input)) {
                return true;
            }
        }
        return false;
    }



    private static boolean match(String pattern, String input) {
        return input.matches(pattern);
    }



}

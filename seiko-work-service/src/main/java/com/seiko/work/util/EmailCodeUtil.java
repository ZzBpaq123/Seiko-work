package com.seiko.work.util;

import java.util.Random;

/**
 * 邮箱验证码工具类
 */
public final class EmailCodeUtil {

    private static final Random RANDOM = new Random();

    private EmailCodeUtil() {
    }

    /**
     * 生成指定长度的数字验证码
     *
     * @param length 验证码长度
     * @return 数字验证码
     */
    public static String generate(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(RANDOM.nextInt(10));
        }
        return code.toString();
    }

}

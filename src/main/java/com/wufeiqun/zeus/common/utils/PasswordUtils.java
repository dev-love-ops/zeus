package com.wufeiqun.zeus.common.utils;

import cn.hutool.crypto.digest.DigestUtil;

public class PasswordUtils {
    public static void main(String[] args) {
        String pass = DigestUtil.bcrypt("123456");
        System.out.println(pass);
        boolean ret = DigestUtil.bcryptCheck("123456", pass);
        System.out.println(ret);
    }
}

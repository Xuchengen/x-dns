package com.github.xuchengen.xdns.test;

import org.junit.jupiter.api.Test;

/**
 * <br>
 * 作者: 徐承恩<br>
 * 邮箱: <a href="mailto:xuchengen@gmail.com">xuchengen@gmail.com</a><br>
 * 日期: 2022-04-22 19:20
 **/
public class OthreTest {

    @Test
    public void t1() {
        System.out.println(MyDomainValidator.isValid("com"));
        System.out.println(MyDomainValidator.isValid("com."));

        System.out.println(MyDomainValidator.isValid("--com"));
        System.out.println(MyDomainValidator.isValid("--com."));

        System.out.println(MyDomainValidator.isValid("中国"));
        System.out.println(MyDomainValidator.isValid("123"));
        System.out.println(MyDomainValidator.isValid("123."));

    }

}

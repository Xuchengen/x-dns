package com.github.xuchengen.xdns.test;

import org.junit.jupiter.api.Test;

/**
 * <br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-24 15:18
 */
public class OtherTest {

    @Test
    public void t1() {
        System.out.println("com is：" + DomainValidator.isValid("com"));
        System.out.println(".com is：" + DomainValidator.isValid(".com"));
        System.out.println("com. is：" + DomainValidator.isValid("com."));
        System.out.println(".com. is：" + DomainValidator.isValid(".com."));

        System.out.println("xuchengen.com is：" + DomainValidator.isValid("xuchengen.com"));
        System.out.println(".xuchengen.com is：" + DomainValidator.isValid(".xuchengen.com"));
        System.out.println("xuchengen.com. is：" + DomainValidator.isValid("xuchengen.com."));
        System.out.println(".xuchengen.com. is：" + DomainValidator.isValid(".xuchengen.com."));

        System.out.println("中国 is：" + DomainValidator.isValid("中国"));
        System.out.println(".中国 is：" + DomainValidator.isValid(".中国"));
        System.out.println("中国. is：" + DomainValidator.isValid("中国."));
        System.out.println(".中国. is：" + DomainValidator.isValid(".中国."));

        System.out.println("香港.中国 is：" + DomainValidator.isValid("香港.中国"));
        System.out.println(".香港.中国 is：" + DomainValidator.isValid(".香港.中国"));
        System.out.println("香港.中国. is：" + DomainValidator.isValid("香港.中国."));
        System.out.println(".香港.中国. is：" + DomainValidator.isValid(".香港.中国."));
    }

}

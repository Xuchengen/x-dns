package com.github.xuchengen.xdns.annotation;

import java.lang.annotation.*;

/**
 * DNS问题类型注解<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-19 13:37
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DnsQuestionType {

    /**
     * 类型字符串
     *
     * @return 字符串
     */
    String type();

}

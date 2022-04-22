package com.github.xuchengen.xdns.utils;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.validator.routines.DomainValidator;

/**
 * 域工具类<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-22 14:17
 */
public class DomainUtil {

    public static final DomainValidator DOMAIN_VALIDATOR = DomainValidator.getInstance();

    public static final String DOMAIN_LOCALHOST = "localhost.";

    public static final String IPV6_ARPA = "1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.ip6.arpa.";

    public static final String IPV4_ARPA = "1.0.0.127.in-addr.arpa.";

    public static final String IPV4_ARPA_STR = ".in-addr.arpa.";

    public static final String IPV6_ARPA_STR = ".ip6.arpa.";

    /**
     * 判断域名是PTR ARPA
     *
     * @param name 域名
     * @return 布尔值
     */
    public static boolean isPtrArpa(String name) {
        return StrUtil.endWithAny(name, IPV4_ARPA_STR, IPV6_ARPA_STR);
    }

    /**
     * 判断域名不是PTR ARPA
     *
     * @param name 域名
     * @return 布尔值
     */
    public static boolean isNotPtrArpa(String name) {
        return !isPtrArpa(name);
    }
}

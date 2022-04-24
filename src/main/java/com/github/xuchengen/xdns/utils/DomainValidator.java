package com.github.xuchengen.xdns.utils;

import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

import java.net.IDN;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 域名校验器<br>
 * 作者: 徐承恩<br>
 * 邮箱: xuchengen@gmail.com
 * 日期: 2022-04-22 20:20
 **/
public class DomainValidator {

    /**
     * 域名长度
     */
    private static final int MAX_DOMAIN_LENGTH = 253;

    /**
     * 域标签正则
     */
    private static final String DOMAIN_LABEL_REGEX = "\\p{Alnum}(?>[\\p{Alnum}-]{0,61}\\p{Alnum})?";

    /**
     * 顶级域标签正则
     */
    private static final String TOP_LABEL_REGEX = "\\p{Alpha}(?>[\\p{Alnum}-]{0,61}\\p{Alnum})?";

    /**
     * 域名匹配正则
     */
    private static final String DOMAIN_NAME_REGEX = "^(?:" + DOMAIN_LABEL_REGEX + "\\.)+" + "(" + TOP_LABEL_REGEX + ")\\.?$";

    public static boolean isValid(String domain) {
        if (StrUtil.isBlank(domain)) {
            return false;
        }

        // 验证的时候需要去掉最后一个点
        domain = StrUtil.removeSuffix(domain, ".");

        if (StrUtil.isBlank(domain)) {
            return false;
        }

        domain = unicodeToASCII(domain);

        if (domain.length() > MAX_DOMAIN_LENGTH) {
            return false;
        }

        String[] groups = getAll(domain);
        if (ArrayUtil.isNotEmpty(groups)) {
            domain = groups[0];
        }

        // 这里仅验证顶级域名是否符合正则，严格来说还是要遵守IANA数据库
        return isMatch(domain);
    }

    /**
     * 获取所有匹配项
     *
     * @param domain 域名
     * @return 字符串数组
     */
    private static String[] getAll(String domain) {
        if (Objects.isNull(domain)) {
            return null;
        }

        Pattern pattern = PatternPool.get(DOMAIN_NAME_REGEX);
        Matcher matcher = pattern.matcher(domain);

        if (matcher.matches()) {
            int count = matcher.groupCount();
            String[] groups = new String[count];
            for (int j = 0; j < count; j++) {
                groups[j] = matcher.group(j + 1);
            }
            return groups;
        }

        return null;
    }

    /**
     * 顶级域是否匹配规则
     *
     * @param domain 顶级域名
     * @return 布尔值
     */
    private static boolean isMatch(String domain) {
        if (Objects.isNull(domain)) {
            return false;
        }

        Pattern pattern = PatternPool.get(TOP_LABEL_REGEX);
        return pattern.matcher(domain).matches();
    }

    /**
     * 域名是否全是ASCII码
     *
     * @param input 输入字符串
     * @return 布尔值
     */
    private static boolean isOnlyASCII(String input) {
        if (input == null) {
            return true;
        }
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) > 0x7F) {
                return false;
            }
        }
        return true;
    }

    /**
     * unicode域名转ASCII域名
     *
     * @param domain 域名
     * @return 字符串
     */
    static String unicodeToASCII(String domain) {
        if (isOnlyASCII(domain)) {
            return domain;
        }

        try {
            final String ascii = IDN.toASCII(domain);
            if (IDNBUGHOLDER.IDN_TOASCII_PRESERVES_TRAILING_DOTS) {
                return ascii;
            }
            final int length = domain.length();
            if (length == 0) {
                return domain;
            }

            char lastChar = domain.charAt(length - 1);
            switch (lastChar) {
                case '\u002E':
                case '\u3002':
                case '\uFF0E':
                case '\uFF61':
                    return ascii + ".";
                default:
                    return ascii;
            }
        } catch (IllegalArgumentException e) {
            return domain;
        }
    }

    private static class IDNBUGHOLDER {
        private static boolean keepsTrailingDot() {
            final String input = "a.";
            return input.equals(IDN.toASCII(input));
        }

        private static final boolean IDN_TOASCII_PRESERVES_TRAILING_DOTS = keepsTrailingDot();
    }

}

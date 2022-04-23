package com.github.xuchengen.xdns.test;

import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.StrUtil;

import java.net.IDN;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <br>
 * 作者: 徐承恩<br>
 * 邮箱: <a href="mailto:xuchengen@gmail.com">xuchengen@gmail.com</a><br>
 * 日期: 2022-04-22 20:20
 **/
public class MyDomainValidator {

    private static final int MAX_DOMAIN_LENGTH = 253;

    private static final String DOMAIN_LABEL_REGEX = "\\p{Alnum}(?>[\\p{Alnum}-]{0,61}\\p{Alnum})?";

    private static final String TOP_LABEL_REGEX = "\\p{Alpha}(?>[\\p{Alnum}-]{0,61}\\p{Alnum})?";

    private static final String DOMAIN_NAME_REGEX =
            "^(?:" + DOMAIN_LABEL_REGEX + "\\.)+" + "(" + TOP_LABEL_REGEX + ")\\.?$";

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

        // 拿到顶级域名
        String[] groups = getAll(domain);

        if (Objects.nonNull(groups) && groups.length > 0) {
            // 最好是做一个数据集匹配
            return isMatch(groups[0]);
        }

        return isMatch(domain);
    }

    private static String[] getAll(String value) {
        if (value == null) {
            return null;
        }

        Pattern pattern = PatternPool.get(DOMAIN_NAME_REGEX);
        Matcher matcher = pattern.matcher(value);

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

    private static boolean isMatch(String value) {
        if (value == null) {
            return false;
        }

        Pattern pattern = PatternPool.get(TOP_LABEL_REGEX);
        return pattern.matcher(value).matches();
    }

    private static class IDNBUGHOLDER {
        private static boolean keepsTrailingDot() {
            final String input = "a.";
            return input.equals(IDN.toASCII(input));
        }

        private static final boolean IDN_TOASCII_PRESERVES_TRAILING_DOTS = keepsTrailingDot();
    }

    static String unicodeToASCII(String input) {
        if (isOnlyASCII(input)) {
            return input;
        }
        try {
            final String ascii = IDN.toASCII(input);
            if (IDNBUGHOLDER.IDN_TOASCII_PRESERVES_TRAILING_DOTS) {
                return ascii;
            }
            final int length = input.length();
            if (length == 0) {
                return input;
            }

            char lastChar = input.charAt(length - 1);
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
            return input;
        }
    }

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

}

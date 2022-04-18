package com.github.xuchengen.xdns.result;

import java.util.List;

/**
 * DNS结果<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 14:31
 */
public class DnsResult {

    public enum Type {A, TXT, NS, MX}

    private final Type type;

    private final String domain;

    private final List<String> records;

    public DnsResult(Type type, String domain, List<String> records) {
        this.type = type;
        this.domain = domain;
        this.records = records;
    }

    public Type getType() {
        return type;
    }

    public String getDomain() {
        return domain;
    }

    public List<String> getRecords() {
        return records;
    }
}

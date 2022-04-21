package com.github.xuchengen.xdns.result;

import io.netty.handler.codec.dns.DnsRecordType;

import java.util.List;

/**
 * DNS结果<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 14:31
 */
public class DnsResult<T> {

    private final DnsRecordType type;

    private final String domain;

    private final List<T> records;

    public DnsResult(DnsRecordType type, String domain, List<T> records) {
        this.type = type;
        this.domain = domain;
        this.records = records;
    }

    public DnsRecordType getType() {
        return type;
    }

    public String getDomain() {
        return domain;
    }

    public List<T> getRecords() {
        return records;
    }
}

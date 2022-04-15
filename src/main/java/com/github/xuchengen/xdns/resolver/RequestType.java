package com.github.xuchengen.xdns.resolver;

import io.netty.handler.codec.dns.DnsRecordType;

/**
 * 请求类型<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 14:26
 */
public class RequestType {

    private final DnsRecordType type;

    public static final RequestType REQUEST_A = new RequestType(DnsRecordType.A);

    public static final RequestType REQUEST_NS = new RequestType(DnsRecordType.NS);

    public static final RequestType REQUEST_MX = new RequestType(DnsRecordType.MX);

    public static final RequestType REQUEST_TXT = new RequestType(DnsRecordType.TXT);

    public DnsRecordType getType() {
        return type;
    }

    public RequestType(DnsRecordType type) {
        this.type = type;
    }
}

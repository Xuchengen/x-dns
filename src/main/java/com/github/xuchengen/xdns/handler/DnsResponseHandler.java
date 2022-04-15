package com.github.xuchengen.xdns.handler;

import com.github.xuchengen.xdns.result.DnsResult;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.util.AttributeKey;

/**
 * 抽象DNS响应处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 14:20
 */
public abstract class DnsResponseHandler<T extends DnsResponse> extends SimpleChannelInboundHandler<T> {

    public final static AttributeKey<DnsResult> RECORD_RESULT = AttributeKey.valueOf("record_result");

    public final static AttributeKey<String> ERROR_MSG = AttributeKey.valueOf("errormsg");

    private final DnsRecordType recordType;

    public DnsRecordType getRecordType() {
        return recordType;
    }

    public DnsResponseHandler(Class<? extends T> clazz, DnsRecordType recordType) {
        super(clazz);
        this.recordType = recordType;
    }
}

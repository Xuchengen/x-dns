package com.github.xuchengen.xdns.handler;

import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.result.DnsResult;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.util.NetUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DNS A记录响应处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 14:40
 */
public class DnsResponseHandlerA<T extends DnsResponse> extends DnsResponseHandler<T> {

    private String domainName;

    public String getDomainName() {
        return domainName;
    }

    public DnsResponseHandlerA(Class<? extends T> clazz) {
        super(clazz, DnsRecordType.A);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String message;
        if (cause instanceof ReadTimeoutException) {
            message = "A handler read timed out";
        } else if (cause instanceof WriteTimeoutException) {
            message = "A handler write timed out";
        } else {
            message = String.format("A handler exception caught, %s", cause.getMessage());
        }
        DnsResult dnsResult = new DnsResult(DnsResult.Type.A, domainName, Collections.emptyList());
        ctx.channel().attr(RECORD_RESULT).set(dnsResult);
        ctx.channel().attr(ERROR_MSG).set(message);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, T dnsResponse) throws Exception {
        if (dnsResponse.count(DnsSection.QUESTION) > 0) {
            DnsQuestion question = dnsResponse.recordAt(DnsSection.QUESTION, 0);
            domainName = question.name();
        } else {
            domainName = "";
        }

        int count = dnsResponse.count(DnsSection.ANSWER);

        if (count == 0) {
            throw new DnsException(dnsResponse.code().toString());
        } else {
            List<String> results = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                DnsRecord record = dnsResponse.recordAt(DnsSection.ANSWER, i);
                if (record.type() == DnsRecordType.A) {
                    DnsRawRecord raw = (DnsRawRecord) record;
                    results.add(NetUtil.bytesToIpAddress(ByteBufUtil.getBytes(raw.content())));
                }
            }

            DnsResult aResult = new DnsResult(DnsResult.Type.A, domainName, results);
            channelHandlerContext.channel().attr(RECORD_RESULT).set(aResult);
        }

        channelHandlerContext.close();
    }
}

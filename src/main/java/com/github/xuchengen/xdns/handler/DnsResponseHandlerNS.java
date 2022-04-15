package com.github.xuchengen.xdns.handler;

import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.result.DnsResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 15:00
 */
public class DnsResponseHandlerNS<T extends DnsResponse> extends DnsResponseHandler<T> {

    private String domainName;

    public String getDomainName() {
        return domainName;
    }

    public DnsResponseHandlerNS(Class<? extends T> clazz) {
        super(clazz, DnsRecordType.NS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String message;
        if (cause instanceof ReadTimeoutException) {
            message = "NS handler read timed out";
        } else if (cause instanceof WriteTimeoutException) {
            message = "NS handler write timed out";
        } else {
            message = String.format("NS handler exception caught, %s", cause.getMessage());
        }

        DnsResult dnsResult = new DnsResult(DnsResult.Type.NS, domainName, Collections.emptyList());
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
                DnsRecord nsrecord = dnsResponse.recordAt(DnsSection.ANSWER, i);
                if (nsrecord.type() == DnsRecordType.NS) {
                    DnsRawRecord raw = (DnsRawRecord) nsrecord;
                    String record = DefaultDnsRecordDecoder.decodeName(raw.content());
                    results.add(record);
                }
            }

            DnsResult nsResult = new DnsResult(DnsResult.Type.NS, domainName, results);
            channelHandlerContext.channel().attr(RECORD_RESULT).set(nsResult);
        }

        channelHandlerContext.close();
    }
}

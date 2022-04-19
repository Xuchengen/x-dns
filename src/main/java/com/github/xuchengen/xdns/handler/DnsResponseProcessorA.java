package com.github.xuchengen.xdns.handler;

import cn.hutool.core.util.StrUtil;
import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.result.DnsResult;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import io.netty.util.NetUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DNS A记录响应处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 14:40
 */
@Component(value = "dnsResponseProcessorA")
public class DnsResponseProcessorA implements DnsResponseProcessor {

    public void doError(ChannelHandlerContext ctx, Throwable throwable) {
        String message;
        if (throwable instanceof ReadTimeoutException) {
            message = "A handler read timed out";
        } else if (throwable instanceof WriteTimeoutException) {
            message = "A handler write timed out";
        } else {
            message = String.format("A handler exception caught, %s", throwable.getMessage());
        }
        String domainName = ctx.channel().attr(DnsResponseHandler.DOMAIN_NAME).get();
        DnsResult dnsResult = new DnsResult(DnsResult.Type.A, domainName, Collections.emptyList());
        ctx.channel().attr(DnsResponseHandler.RESULT).set(dnsResult);
        ctx.channel().attr(DnsResponseHandler.ERROR).set(message);
        ctx.close();
    }

    public void doProcess(ChannelHandlerContext channelHandlerContext, DnsResponse dnsResponse) {
        String domainName = StrUtil.EMPTY;
        if (dnsResponse.count(DnsSection.QUESTION) > 0) {
            DnsQuestion question = dnsResponse.recordAt(DnsSection.QUESTION, 0);
            domainName = question.name();
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

            DnsResult result = new DnsResult(DnsResult.Type.A, domainName, results);
            channelHandlerContext.channel().attr(DnsResponseHandler.RESULT).set(result);
        }

        channelHandlerContext.close();
    }
}

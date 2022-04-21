package com.github.xuchengen.xdns.handler.processor;

import cn.hutool.core.util.StrUtil;
import com.github.xuchengen.xdns.annotation.DnsQuestionType;
import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.handler.DnsResponseHandler;
import com.github.xuchengen.xdns.result.DnsResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DNS PTR记录响应处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-21 09:24
 */
@Component
@DnsQuestionType(type = "PTR")
public class DnsResponseProcessorPTR implements DnsResponseProcessor {

    @Override
    public void doError(ChannelHandlerContext ctx, Throwable throwable) {
        String message;
        if (throwable instanceof ReadTimeoutException) {
            message = "PTR handler read timed out";
        } else if (throwable instanceof WriteTimeoutException) {
            message = "PTR handler write timed out";
        } else {
            message = String.format("PTR handler exception caught, %s", throwable.getMessage());
        }
        String domainName = ctx.channel().attr(DnsResponseHandler.DOMAIN_NAME).get();
        DnsResult dnsResult = new DnsResult(DnsRecordType.PTR, domainName, Collections.emptyList());
        ctx.channel().attr(DnsResponseHandler.RESULT).set(dnsResult);
        ctx.channel().attr(DnsResponseHandler.ERROR).set(message);
        ctx.close();
    }

    @Override
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
                if (DnsRecordType.PTR.equals(record.type())) {
                    DnsRawRecord raw = (DnsRawRecord) record;
                    results.add(raw.content().toString());
                }
            }

            DnsResult result = new DnsResult(DnsRecordType.PTR, domainName, results);
            channelHandlerContext.channel().attr(DnsResponseHandler.RESULT).set(result);
        }

        channelHandlerContext.close();
    }
}

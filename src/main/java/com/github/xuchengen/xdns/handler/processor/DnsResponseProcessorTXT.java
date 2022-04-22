package com.github.xuchengen.xdns.handler.processor;

import cn.hutool.core.util.StrUtil;
import com.github.xuchengen.xdns.annotation.DnsQuestionType;
import com.github.xuchengen.xdns.handler.DnsResponseHandler;
import com.github.xuchengen.xdns.result.DnsResult;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DNS TXT记录处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 15:05
 */
@Component
@DnsQuestionType(type = "TXT")
public class DnsResponseProcessorTXT implements DnsResponseProcessor {

    public void doError(ChannelHandlerContext ctx, Throwable throwable) {
        String message;
        if (throwable instanceof ReadTimeoutException) {
            message = "TXT handler read timed out";
        } else if (throwable instanceof WriteTimeoutException) {
            message = "TXT handler write timed out";
        } else {
            message = String.format("TXT handler exception caught, %s", throwable.getMessage());
        }
        String domainName = ctx.channel().attr(DnsResponseHandler.DOMAIN_NAME).get();
        DnsResult<String> dnsResult = new DnsResult<>(DnsRecordType.TXT, domainName, Collections.emptyList());
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
        List<String> results = Collections.emptyList();

        if (count > 0) {
            results = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                DefaultDnsRawRecord rawRecord = dnsResponse.recordAt(DnsSection.ANSWER, i);
                ByteBuf content = rawRecord.content();
                StringBuilder sb = new StringBuilder();
                while (content.readableBytes() > 0) {
                    int readLen = content.readUnsignedByte();
                    byte[] bytes = new byte[readLen];
                    ByteBuf bb = content.readBytes(readLen);
                    bb.readBytes(bytes);
                    sb.append(new String(bytes));
                }

                results.add(sb.toString());
            }
        }

        DnsResult<String> txtResult = new DnsResult<>(DnsRecordType.TXT, domainName, results);
        channelHandlerContext.channel().attr(DnsResponseHandler.RESULT).set(txtResult);
        channelHandlerContext.close();
    }
}

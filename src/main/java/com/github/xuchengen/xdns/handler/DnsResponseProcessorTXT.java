package com.github.xuchengen.xdns.handler;

import cn.hutool.core.util.StrUtil;
import com.github.xuchengen.xdns.exception.DnsException;
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
@Component(value = "dnsResponseProcessorTXT")
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
        DnsResult dnsResult = new DnsResult(DnsResult.Type.TXT, domainName, Collections.emptyList());
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
                DnsRecord txtrecord = dnsResponse.recordAt(DnsSection.ANSWER, i);
                if (txtrecord.type() == DnsRecordType.TXT) {
                    DnsRawRecord raw = (DnsRawRecord) txtrecord;
                    ByteBuf content = raw.content();
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

            DnsResult txtResult = new DnsResult(DnsResult.Type.TXT, domainName, results);
            channelHandlerContext.channel().attr(DnsResponseHandler.RESULT).set(txtResult);
        }

        channelHandlerContext.close();
    }
}

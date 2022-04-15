package com.github.xuchengen.xdns.handler;

import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.result.DnsResult;
import io.netty.buffer.ByteBuf;
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
 * 2022-04-15 15:05
 */
public class DnsResponseHandlerTXT<T extends DnsResponse> extends DnsResponseHandler<T> {

    private String domainName;

    public DnsResponseHandlerTXT(Class<? extends T> clazz) {
        super(clazz, DnsRecordType.TXT);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String message;
        if (cause instanceof ReadTimeoutException) {
            message = "TXT handler read timed out";
        } else if (cause instanceof WriteTimeoutException) {
            message = "TXT handler write timed out";
        } else {
            message = String.format("TXT handler exception caught, %s", cause.getMessage());
        }
        DnsResult dnsResult = new DnsResult(DnsResult.Type.TXT, domainName, Collections.emptyList());
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
            channelHandlerContext.channel().attr(RECORD_RESULT).set(txtResult);
        }

        channelHandlerContext.close();
    }
}

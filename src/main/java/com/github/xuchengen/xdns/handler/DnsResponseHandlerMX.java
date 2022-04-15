package com.github.xuchengen.xdns.handler;

import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.result.DnsResult;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DNS MX记录响应处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 14:54
 */
public class DnsResponseHandlerMX<T extends DnsResponse> extends DnsResponseHandler<T> {

    private String domainName;

    public DnsResponseHandlerMX(Class<? extends T> inboundMessageType) {
        super(inboundMessageType, DnsRecordType.MX);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String message;
        if (cause instanceof ReadTimeoutException) {
            message = "MX handler read timed out";
        } else if (cause instanceof WriteTimeoutException) {
            message = "MX handler write timed out";
        } else {
            message = String.format("MX handler exception caught, %s", cause.getMessage());
        }
        DnsResult dnsResult = new DnsResult(DnsResult.Type.MX, domainName, Collections.emptyList());
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
            Comparator<Integer> comparator = Comparator.comparingInt(i -> i);
            Map<Integer, List<String>> map = new TreeMap<>(comparator);
            List<String> results;
            for (int i = 0; i < count; i++) {
                DnsRecord mxrecord = dnsResponse.recordAt(DnsSection.ANSWER, i);
                if (mxrecord.type() == DnsRecordType.MX) {
                    DnsRawRecord raw = (DnsRawRecord) mxrecord;
                    ByteBuf content = raw.content();
                    Integer preference = content.readUnsignedShort();
                    String record = DefaultDnsRecordDecoder.decodeName(content);

                    if (map.containsKey(preference)) {
                        map.get(preference).add(record);
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add(record);
                        map.put(preference, list);
                    }
                }
            }

            results = map.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());
            DnsResult mxResult = new DnsResult(DnsResult.Type.MX, domainName, results);
            channelHandlerContext.channel().attr(RECORD_RESULT).set(mxResult);
        }

        channelHandlerContext.close();
    }
}

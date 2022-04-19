package com.github.xuchengen.xdns.handler.processor;

import cn.hutool.core.util.StrUtil;
import com.github.xuchengen.xdns.annotation.DnsQuestionType;
import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.handler.DnsResponseHandler;
import com.github.xuchengen.xdns.result.DnsResult;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DNS MX记录响应处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 14:54
 */
@Component(value = "dnsResponseProcessorMX")
@DnsQuestionType(type = "MX")
public class DnsResponseProcessorMX implements DnsResponseProcessor {

    public void doError(ChannelHandlerContext ctx, Throwable throwable) {
        String message;
        if (throwable instanceof ReadTimeoutException) {
            message = "MX handler read timed out";
        } else if (throwable instanceof WriteTimeoutException) {
            message = "MX handler write timed out";
        } else {
            message = String.format("MX handler exception caught, %s", throwable.getMessage());
        }
        String domainName = ctx.channel().attr(DnsResponseHandler.DOMAIN_NAME).get();
        DnsResult dnsResult = new DnsResult(DnsRecordType.MX, domainName, Collections.emptyList());
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
            DnsResult mxResult = new DnsResult(DnsRecordType.MX, domainName, results);
            channelHandlerContext.channel().attr(DnsResponseHandler.RESULT).set(mxResult);
        }

        channelHandlerContext.close();
    }
}
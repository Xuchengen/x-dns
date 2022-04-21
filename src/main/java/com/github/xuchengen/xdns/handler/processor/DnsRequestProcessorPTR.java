package com.github.xuchengen.xdns.handler.processor;

import com.github.xuchengen.xdns.annotation.DnsQuestionType;
import com.github.xuchengen.xdns.resolver.DnsResolver;
import com.github.xuchengen.xdns.result.DnsResult;
import com.github.xuchengen.xdns.utils.DnsCodecUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * DNS PTR记录请求处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-20 14:22
 */
@Component
@DnsQuestionType(type = "PTR")
public class DnsRequestProcessorPTR implements DnsRequestProcessor {

    @Resource(name = "dnsResolver")
    private DnsResolver dnsResolver;

    private static final String IPV6_LOOKBACK = "1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.ip6.arpa.";

    private static final String IPV4_LOOKBACK = "1.0.0.127.in-addr.arpa.";

    @Override
    public void doProcess(ChannelHandlerContext ctx, DnsQuery query) {
        DnsResponse response;
        if (query instanceof DatagramDnsQuery) {
            DatagramDnsQuery _query = (DatagramDnsQuery) query;
            response = new DatagramDnsResponse(_query.recipient(), _query.sender(), query.id());
        } else {
            response = new DefaultDnsResponse(query.id());
        }

        DefaultDnsQuestion question = query.recordAt(DnsSection.QUESTION);
        DnsRecordType type = question.type();
        String name = question.name();
        response.addRecord(DnsSection.QUESTION, question);
        if (IPV4_LOOKBACK.equals(name) || IPV6_LOOKBACK.equals(name)) {
            ByteBuf buffer = Unpooled.buffer();
            DnsCodecUtil.encodeDomainName("xuchengen.cn", buffer);
            DefaultDnsRawRecord record = new DefaultDnsRawRecord(name, DnsRecordType.PTR, 10, buffer);
            response.addRecord(DnsSection.ANSWER, record);
        } else {
            DnsResult result = dnsResolver.resolveDomainByUdp("223.5.5.5", name, type);
            List<String> records = result.getRecords();
            for (String record : records) {
                ByteBuf buffer = Unpooled.buffer();
                DnsCodecUtil.encodeDomainName(record, buffer);
                DefaultDnsRawRecord rawRecord = new DefaultDnsRawRecord(question.name(), type, 10, buffer);
                response.addRecord(DnsSection.ANSWER, rawRecord);
            }
        }
        ctx.writeAndFlush(response);
    }
}

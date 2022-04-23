package com.github.xuchengen.xdns.handler.processor;

import com.github.xuchengen.xdns.annotation.DnsQuestionType;
import com.github.xuchengen.xdns.resolver.DnsResolver;
import com.github.xuchengen.xdns.result.DnsResult;
import com.github.xuchengen.xdns.utils.DnsCodecUtil;
import com.github.xuchengen.xdns.utils.DomainUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * DNS A记录请求处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-20 13:58
 */
@Component
@DnsQuestionType(type = "CNAME")
public class DnsRequestProcessorCNAME implements DnsRequestProcessor {

    @Resource(name = "dnsResolver")
    private DnsResolver dnsResolver;

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
        response.addRecord(DnsSection.QUESTION, question);
        String name = question.name();

        if (DomainUtil.DOMAIN_LOCALHOST.equals(name) || !DomainUtil.DOMAIN_VALIDATOR.isValid(name)) {
            // 如果是 localhost. 或者不是域名那还查询个什么
            response.setCode(DnsResponseCode.NXDOMAIN);
        } else {
            DnsResult<String> result = dnsResolver.resolveDomainByUdp("223.5.5.5", name, type);
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
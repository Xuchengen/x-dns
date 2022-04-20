package com.github.xuchengen.xdns.handler.processor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.*;
import org.springframework.stereotype.Component;

/**
 * DNS请求默认处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-20 15:57
 */
@Component
public class DefaultDnsRequestProcessor implements DnsRequestProcessor {

    @Override
    public void doProcess(ChannelHandlerContext ctx, DnsQuery query) {
        DnsResponse response;
        if (query instanceof DatagramDnsQuery) {
            DatagramDnsQuery _query = (DatagramDnsQuery) query;
            response = new DatagramDnsResponse(_query.recipient(), _query.sender(),
                    query.id(), _query.opCode(), DnsResponseCode.NOTIMP);
        } else {
            response = new DefaultDnsResponse(query.id(), query.opCode(), DnsResponseCode.NOTIMP);
        }
        DefaultDnsQuestion dnsQuestion = query.recordAt(DnsSection.QUESTION);
        response.addRecord(DnsSection.QUESTION, dnsQuestion);
        ctx.writeAndFlush(response);
    }
}

package com.github.xuchengen.xdns.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.*;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * DNS请求处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-19 15:14
 */
@Component
@ChannelHandler.Sharable
public class DnsRequestHandler extends SimpleChannelInboundHandler<DnsQuery> {

    private static final AttributeKey<DnsQuery> DNS_QUERY = AttributeKey.valueOf("DNS_QUERY");

    private static final AttributeKey<DnsQuestion> DNS_QUESTION = AttributeKey.valueOf("DNS_QUESTION");

    @Resource(name = "dnsRequestProcessorContext")
    private DnsRequestProcessorContext dnsRequestProcessorContext;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DnsQuery query) throws Exception {
        DefaultDnsQuestion dnsQuestion = query.recordAt(DnsSection.QUESTION);
        ctx.channel().attr(DNS_QUERY).set(query);
        ctx.channel().attr(DNS_QUESTION).set(dnsQuestion);
        DnsRecordType type = dnsQuestion.type();
        dnsRequestProcessorContext.get(type).doProcess(ctx, query);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        DnsQuery query = ctx.channel().attr(DNS_QUERY).get();
        DnsQuestion question = ctx.channel().attr(DNS_QUESTION).get();
        DnsResponse response;
        if (query instanceof DatagramDnsQuery) {
            DatagramDnsQuery _query = (DatagramDnsQuery) query;
            response = new DatagramDnsResponse(_query.recipient(), _query.sender(),
                    query.id(), _query.opCode(), DnsResponseCode.SERVFAIL);
        } else {
            response = new DefaultDnsResponse(query.id(), query.opCode(), DnsResponseCode.SERVFAIL);
        }
        response.addRecord(DnsSection.QUESTION, question);
        ctx.writeAndFlush(response);
    }
}

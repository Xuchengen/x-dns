package com.github.xuchengen.xdns.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsSection;

/**
 * <br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-18 16:41
 */
public class DnsRequestHandler extends SimpleChannelInboundHandler<DnsQuery> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DnsQuery msg) throws Exception {
        DnsQuestion question = msg.recordAt(DnsSection.QUESTION);
        String name = question.name();
        System.out.println(name);
        ctx.channel().close();
    }
}

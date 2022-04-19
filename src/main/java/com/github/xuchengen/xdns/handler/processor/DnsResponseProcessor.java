package com.github.xuchengen.xdns.handler.processor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.DnsResponse;

/**
 * DNS响应处理器接口<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 14:20
 */
public interface DnsResponseProcessor {

    void doError(ChannelHandlerContext ctx, Throwable throwable);

    void doProcess(ChannelHandlerContext channelHandlerContext, DnsResponse dnsResponse);
}

package com.github.xuchengen.xdns.handler.processor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.dns.DnsQuery;

/**
 * DNS请求处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-20 13:33
 */
public interface DnsRequestProcessor {

    void doProcess(ChannelHandlerContext ctx, DnsQuery query);

}

package com.github.xuchengen.xdns.handler;

import com.github.xuchengen.xdns.handler.processor.DnsResponseProcessor;
import com.github.xuchengen.xdns.result.DnsResult;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

/**
 * DNS响应处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-18 11:55
 */
@Component
@ChannelHandler.Sharable
public class DnsResponseHandler extends SimpleChannelInboundHandler<DnsResponse> {

    public final static AttributeKey<DnsResult> RESULT = AttributeKey.valueOf("RESULT");

    public final static AttributeKey<String> ERROR = AttributeKey.valueOf("ERROR");

    public final static AttributeKey<DnsRecordType> DNS_RECORD_TYPE = AttributeKey.valueOf("DNS_RECORD_TYPE");

    public final static AttributeKey<String> DOMAIN_NAME = AttributeKey.valueOf("DOMAIN_NAME");

    private final DnsResponseProcessorContext dnsResponseProcessorContext;

    public DnsResponseHandler(DnsResponseProcessorContext dnsResponseProcessorContext) {
        this.dnsResponseProcessorContext = dnsResponseProcessorContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DnsResponse dnsResponse) {
        DnsRecordType type = ctx.channel().attr(DNS_RECORD_TYPE).get();
        DnsResponseProcessor processor = dnsResponseProcessorContext.get(type);
        processor.doProcess(ctx, dnsResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        DnsRecordType type = ctx.channel().attr(DNS_RECORD_TYPE).get();
        DnsResponseProcessor processor = dnsResponseProcessorContext.get(type);
        processor.doError(ctx, throwable);
    }
}

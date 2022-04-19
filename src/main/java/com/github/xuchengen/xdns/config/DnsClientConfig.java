package com.github.xuchengen.xdns.config;

import com.github.xuchengen.xdns.handler.DnsResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.dns.DatagramDnsQueryEncoder;
import io.netty.handler.codec.dns.DatagramDnsResponseDecoder;
import io.netty.handler.codec.dns.TcpDnsQueryEncoder;
import io.netty.handler.codec.dns.TcpDnsResponseDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * DNS客户端配置<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 14:13
 */
@Configuration
public class DnsClientConfig {

    @Bean
    @Lazy
    public Bootstrap dnsTcpClientBootStrap(DnsResponseHandler dnsResponseHandler) {
        return new Bootstrap()
                .group(eventLoopGroupWithClient())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline()
                                .addLast(new ReadTimeoutHandler(10))
                                .addLast(new WriteTimeoutHandler(10))
                                .addLast(new TcpDnsQueryEncoder())
                                .addLast(new TcpDnsResponseDecoder())
                                .addLast(dnsResponseHandler);
                    }
                });
    }

    @Bean
    @Lazy
    public Bootstrap dnsUdpClientBootstrap(DnsResponseHandler dnsResponseHandler) {
        return new Bootstrap()
                .group(eventLoopGroupWithClient())
                .channel(NioDatagramChannel.class)
                .handler(new ChannelInitializer<DatagramChannel>() {
                    @Override
                    protected void initChannel(DatagramChannel socketChannel) {
                        socketChannel.pipeline()
                                .addLast(new ReadTimeoutHandler(10))
                                .addLast(new WriteTimeoutHandler(10))
                                .addLast(new DatagramDnsQueryEncoder())
                                .addLast(new DatagramDnsResponseDecoder())
                                .addLast(dnsResponseHandler);
                    }
                });
    }

    @Bean(destroyMethod = "shutdownGracefully")
    @Lazy
    public EventLoopGroup eventLoopGroupWithClient() {
        return new NioEventLoopGroup();
    }

}

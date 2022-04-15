package com.github.xuchengen.xdns.config;

import com.github.xuchengen.xdns.handler.DnsResponseHandlerA;
import com.github.xuchengen.xdns.handler.DnsResponseHandlerMX;
import com.github.xuchengen.xdns.handler.DnsResponseHandlerNS;
import com.github.xuchengen.xdns.handler.DnsResponseHandlerTXT;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.dns.*;
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
    public Bootstrap tcpMxBootstrap() {
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup());
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                ChannelPipeline p = socketChannel.pipeline();

                p.addLast(new ReadTimeoutHandler(10))
                        .addLast(new WriteTimeoutHandler(10))
                        .addLast(new TcpDnsQueryEncoder())
                        .addLast(new TcpDnsResponseDecoder())
                        .addLast(new DnsResponseHandlerMX<>(DefaultDnsResponse.class));
            }
        });
        return b;
    }

    @Bean
    @Lazy
    public Bootstrap udpMxBootstrap() {
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup());
        b.channel(NioDatagramChannel.class);
        b.handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel socketChannel) {
                ChannelPipeline p = socketChannel.pipeline();

                p.addLast(new DatagramDnsQueryEncoder())
                        .addLast(new DatagramDnsResponseDecoder())
                        .addLast(new DnsResponseHandlerMX<>(DatagramDnsResponse.class));
            }
        });
        return b;
    }

    @Bean
    @Lazy
    public Bootstrap tcpTxtBootstrap() {
        int dnsTimeout = 10;
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup());
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, dnsTimeout * 1000);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                ChannelPipeline p = socketChannel.pipeline();

                p.addLast(new ReadTimeoutHandler(dnsTimeout))
                        .addLast(new WriteTimeoutHandler(dnsTimeout))
                        .addLast(new TcpDnsQueryEncoder())
                        .addLast(new TcpDnsResponseDecoder())
                        .addLast(new DnsResponseHandlerTXT<>(DefaultDnsResponse.class));
            }
        });
        return b;
    }

    @Bean
    @Lazy
    public Bootstrap udpTxtBootstrap() {
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup());
        b.channel(NioDatagramChannel.class);
        b.handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel socketChannel) {
                ChannelPipeline p = socketChannel.pipeline();

                p.addLast(new DatagramDnsQueryEncoder())
                        .addLast(new DatagramDnsResponseDecoder())
                        .addLast(new DnsResponseHandlerTXT<>(DatagramDnsResponse.class));
            }
        });
        return b;
    }

    @Bean
    @Lazy
    public Bootstrap tcpABootstrap() {
        int dnsTimeout = 10;
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup());
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, dnsTimeout * 1000);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                ChannelPipeline p = socketChannel.pipeline();

                p.addLast(new ReadTimeoutHandler(dnsTimeout))
                        .addLast(new WriteTimeoutHandler(dnsTimeout))
                        .addLast(new TcpDnsQueryEncoder())
                        .addLast(new TcpDnsResponseDecoder())
                        .addLast(new DnsResponseHandlerA<>(DefaultDnsResponse.class));
            }
        });
        return b;
    }

    @Bean
    @Lazy
    public Bootstrap udpABootstrap() {
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup());
        b.channel(NioDatagramChannel.class);
        b.handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel socketChannel) {
                ChannelPipeline p = socketChannel.pipeline();

                p.addLast(new DatagramDnsQueryEncoder())
                        .addLast(new DatagramDnsResponseDecoder())
                        .addLast(new DnsResponseHandlerA<>(DatagramDnsResponse.class));
            }
        });
        return b;
    }

    @Bean
    @Lazy
    public Bootstrap tcpNSBootstrap() {
        int dnsTimeout = 10;
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup());
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, dnsTimeout * 1000);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                ChannelPipeline p = socketChannel.pipeline();

                p.addLast(new ReadTimeoutHandler(dnsTimeout))
                        .addLast(new WriteTimeoutHandler(dnsTimeout))
                        .addLast(new TcpDnsQueryEncoder())
                        .addLast(new TcpDnsResponseDecoder())
                        .addLast(new DnsResponseHandlerNS<>(DefaultDnsResponse.class));
            }
        });
        return b;
    }

    @Bean
    @Lazy
    public Bootstrap udpNSBootstrap() {
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup());
        b.channel(NioDatagramChannel.class);
        b.handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel socketChannel) {
                ChannelPipeline p = socketChannel.pipeline();

                p.addLast(new DatagramDnsQueryEncoder())
                        .addLast(new DatagramDnsResponseDecoder())
                        .addLast(new DnsResponseHandlerNS<>(DatagramDnsResponse.class));
            }
        });
        return b;
    }

    @Bean(destroyMethod = "shutdownGracefully")
    @Lazy
    public EventLoopGroup eventLoopGroup() {
        return new NioEventLoopGroup();
    }

}

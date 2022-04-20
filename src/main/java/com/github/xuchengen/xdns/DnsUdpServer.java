package com.github.xuchengen.xdns;

import com.github.xuchengen.xdns.handler.DnsRequestHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.DatagramDnsQueryDecoder;
import io.netty.handler.codec.dns.DatagramDnsResponseEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * <br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-19 14:52
 */
@Component
public class DnsUdpServer implements CommandLineRunner {

    @Resource(name = "dnsRequestHandler")
    private DnsRequestHandler dnsRequestHandler;

    NioEventLoopGroup work = new NioEventLoopGroup();

    @Override
    public void run(String... args) throws Exception {
        new Bootstrap()
                .localAddress(new InetSocketAddress(53))
                .option(ChannelOption.SO_BROADCAST, true)
                .group(work)
                .channel(NioDatagramChannel.class)
                .handler(new ChannelInitializer<NioDatagramChannel>() {

                    @Override
                    protected void initChannel(NioDatagramChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new DatagramDnsQueryDecoder())
                                .addLast(new DatagramDnsResponseEncoder())
                                .addLast(dnsRequestHandler);
                    }
                }).bind();
    }

    @PreDestroy
    public void destroy() {
        work.shutdownGracefully();
    }
}

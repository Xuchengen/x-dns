package com.github.xuchengen.xdns;

import com.github.xuchengen.xdns.handler.DnsRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.dns.TcpDnsQueryDecoder;
import io.netty.handler.codec.dns.TcpDnsResponseEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * <br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-20 13:36
 */
@Component
public class DnsTcpServer implements CommandLineRunner {

    @Resource(name = "dnsRequestHandler")
    private DnsRequestHandler dnsRequestHandler;

    private NioEventLoopGroup boss = new NioEventLoopGroup(1);

    private NioEventLoopGroup work = new NioEventLoopGroup();

    @Override
    public void run(String... args) throws Exception {
        new ServerBootstrap()
                .localAddress(new InetSocketAddress(53))
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new TcpDnsQueryDecoder())
                                .addLast(new TcpDnsResponseEncoder())
                                .addLast(dnsRequestHandler);
                    }
                }).bind();
    }

    @PreDestroy
    public void destroy() {
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }
}

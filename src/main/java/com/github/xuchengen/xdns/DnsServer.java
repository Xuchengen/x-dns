package com.github.xuchengen.xdns;

import com.github.xuchengen.xdns.handler.DnsRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.dns.TcpDnsQueryDecoder;
import io.netty.handler.codec.dns.TcpDnsResponseEncoder;
import io.netty.util.concurrent.Future;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * <br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-18 16:55
 */
@Component
public class DnsServer {

    private EventLoopGroup boss = new NioEventLoopGroup(1);

    private EventLoopGroup worker = new NioEventLoopGroup();

    private Channel channel;

    public ChannelFuture start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(53))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline()
                                .addLast(new TcpDnsQueryDecoder())
                                .addLast(new TcpDnsResponseEncoder())
                                .addLast(new DnsRequestHandler());
                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind().syncUninterruptibly();
        if (channelFuture != null && channelFuture.isSuccess()) {
            channel = channelFuture.channel();
        }
        return channelFuture;
    }

    @PreDestroy
    public void destroy() {
        if (channel != null) {
            channel.close();
        }

        try {
            Future<?> future = worker.shutdownGracefully().await();
            Future<?> future1 = boss.shutdownGracefully().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

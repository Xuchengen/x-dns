package com.github.xuchengen.xdns;

import io.netty.channel.ChannelFuture;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class Launcher implements CommandLineRunner {

    @Resource(name = "dnsServer")
    private DnsServer dnsServer;

    public static void main(String[] args) {
        SpringApplication.run(Launcher.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ChannelFuture channelFuture = dnsServer.start();
        channelFuture.channel().closeFuture().syncUninterruptibly();
    }
}

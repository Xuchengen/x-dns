package com.github.xuchengen.xdns.config;

import com.github.xuchengen.xdns.exception.DnsException;
import io.netty.bootstrap.Bootstrap;
import io.netty.handler.codec.dns.DnsRecordType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 15:08
 */
@Component
public class BootstrapFactory {

    @Resource(name = "tcpMxBootstrap")
    private Bootstrap tcpMxBootstrap;

    @Resource(name = "udpMxBootstrap")
    private Bootstrap udpMxBootstrap;

    @Resource(name = "tcpTxtBootstrap")
    private Bootstrap tcpTxtBootstrap;

    @Resource(name = "udpTxtBootstrap")
    private Bootstrap udpTxtBootstrap;

    @Resource(name = "tcpABootstrap")
    private Bootstrap tcpABootstrap;

    @Resource(name = "udpABootstrap")
    private Bootstrap udpABootstrap;

    @Resource(name = "tcpNSBootstrap")
    private Bootstrap tcpNSBootstrap;

    @Resource(name = "udpNSBootstrap")
    private Bootstrap udpNSBootstrap;

    public Bootstrap getBootstrapTcp(DnsRecordType type) {
        Bootstrap bootstrap;
        switch (type.intValue()) {
            case 1:
                bootstrap = tcpABootstrap;
                break;
            case 2:
                bootstrap = tcpNSBootstrap;
                break;
            case 15:
                bootstrap = tcpMxBootstrap;
                break;
            case 16:
                bootstrap = tcpTxtBootstrap;
                break;
            default:
                throw new DnsException("not support record type");
        }

        return bootstrap;
    }

    public Bootstrap getBootstrapUdp(DnsRecordType type) {
        Bootstrap bootstrap;
        switch (type.intValue()) {
            case 1:
                bootstrap = udpABootstrap;
                break;
            case 2:
                bootstrap = udpNSBootstrap;
                break;
            case 15:
                bootstrap = udpMxBootstrap;
                break;
            case 16:
                bootstrap = udpTxtBootstrap;
                break;
            default:
                throw new DnsException("not support record type");
        }

        return bootstrap;
    }
}

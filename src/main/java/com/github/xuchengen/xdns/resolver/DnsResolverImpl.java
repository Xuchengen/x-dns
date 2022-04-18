package com.github.xuchengen.xdns.resolver;

import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.handler.DnsResponseHandler;
import com.github.xuchengen.xdns.result.DnsResult;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.handler.codec.dns.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * DNS解析器实现<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 14:35
 */
@Service(value = "dnsResolver")
public class DnsResolverImpl implements DnsResolver {

    @Resource(name = "dnsTcpClientBootStrap")
    private Bootstrap dnsTcpClientBootstrap;

    @Resource(name = "dnsUdpClientBootstrap")
    private Bootstrap dnsUdpClientBootstrap;

    @Override
    public DnsResult resolveDomainByTcp(String dnsIp, String domainName,
                                        RequestType requestType) throws DnsException {
        if (dnsIp.isEmpty()) {
            dnsIp = "8.8.8.8";
        }

        final int dnsTimeout = 10;

        short randomID = DnsResolver.getRandomId();

        final Channel ch;
        try {
            ch = dnsTcpClientBootstrap.connect(dnsIp, 53).sync().channel();
        } catch (Throwable cte) {
            throw new DnsException(String.format("fail to connect dns server, %s", cte.getMessage()));
        }

        DnsQuery query = new DefaultDnsQuery(randomID, DnsOpCode.QUERY)
                .setRecord(DnsSection.QUESTION, new DefaultDnsQuestion(domainName, requestType.getType()))
                .setRecursionDesired(true);
        ch.attr(DnsResponseHandler.DNS_RECORD_TYPE).set(requestType.getType());
        ch.attr(DnsResponseHandler.DOMAIN_NAME).set(domainName);
        try {
            ch.writeAndFlush(query).sync().addListener(future ->
            {
                if (!future.isSuccess()) {
                    throw new DnsException("fail send query message");
                } else if (future.isCancelled()) {
                    throw new DnsException("operation cancelled");
                }
            });

            boolean bSuccess = ch.closeFuture().await(dnsTimeout, TimeUnit.SECONDS);

            if (!bSuccess) {
                ch.close().sync();
                throw new DnsException(String.format(
                        "fail to resolve domain by TCP, timed out, domain : %s, dns : %s", domainName, dnsIp));
            }
        } catch (InterruptedException ie) {
            throw new DnsException("fail to resolve record, interrupted exception");
        }

        DnsResult result = ch.attr(DnsResponseHandler.RESULT).get();
        if (result.getRecords().isEmpty()) {
            throw new DnsException(ch.attr(DnsResponseHandler.ERROR).get());
        }

        return result;
    }

    @Override
    public DnsResult resolveDomainByUdp(String dnsIp, String domainName,
                                        RequestType requestType) throws DnsException {
        if (dnsIp.isEmpty()) {
            dnsIp = "8.8.8.8";
        }

        final int dnsTimeout = 10;

        short randomID = DnsResolver.getRandomId();

        InetSocketAddress addr = new InetSocketAddress(dnsIp, 53);

        final Channel ch;
        try {
            ch = dnsUdpClientBootstrap.bind(0).sync().channel();

            DnsQuery query = new DatagramDnsQuery(null, addr, randomID)
                    .setRecord(DnsSection.QUESTION, new DefaultDnsQuestion(domainName, requestType.getType()))
                    .setRecursionDesired(true);
            ch.attr(DnsResponseHandler.DNS_RECORD_TYPE).set(requestType.getType());
            ch.attr(DnsResponseHandler.DOMAIN_NAME).set(domainName);
            ch.writeAndFlush(query).sync().addListener(future ->
            {
                if (!future.isSuccess()) {
                    throw new DnsException("fail send query message");
                } else if (future.isCancelled()) {
                    throw new DnsException("operation cancelled");
                }
            });

            boolean bSuccess = ch.closeFuture().await(dnsTimeout, TimeUnit.SECONDS);
            if (!bSuccess) {
                ch.close().sync();
                throw new DnsException(String.format(
                        "fail to resolve domain by UDP, timed out, domain : %s, dns : %s", domainName, dnsIp));
            }
        } catch (InterruptedException ie) {
            throw new DnsException("fail to resolve record, interrupted exception");
        }

        DnsResult result = ch.attr(DnsResponseHandler.RESULT).get();
        if (result.getRecords().isEmpty()) {
            throw new DnsException(ch.attr(DnsResponseHandler.ERROR).get());
        }

        return result;
    }
}

package com.github.xuchengen.xdns.resolver;

import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.result.DnsResult;
import io.netty.handler.codec.dns.DnsRecordType;

import java.util.Random;

/**
 * DNS解析器接口<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 14:30
 */
public interface DnsResolver {

    static short getRandomId() {
        return (short) new Random().nextInt(1 << 15);
    }

    <T extends DnsResult> T resolveDomainByTcp(String dnsIp, String domainName,
                                               DnsRecordType dnsRecordType) throws DnsException;

    <T extends DnsResult> T resolveDomainByUdp(String dnsIp, String domainName,
                                               DnsRecordType dnsRecordType) throws DnsException;
}

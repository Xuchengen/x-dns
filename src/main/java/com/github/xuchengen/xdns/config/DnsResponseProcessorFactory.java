package com.github.xuchengen.xdns.config;

import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.handler.*;
import io.netty.handler.codec.dns.DnsRecordType;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * DNS响应处理器工厂<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-18 13:09
 */
@Component(value = "dnsResponseProcessorFactory")
public class DnsResponseProcessorFactory {

    private final Map<DnsRecordType, DnsResponseProcessor> cache;

    public DnsResponseProcessorFactory(ApplicationContext applicationContext) {
        cache = new HashMap<>();
        cache.put(DnsRecordType.A, applicationContext.getBean(DnsResponseProcessorA.class));
        cache.put(DnsRecordType.NS, applicationContext.getBean(DnsResponseProcessorNS.class));
        cache.put(DnsRecordType.MX, applicationContext.getBean(DnsResponseProcessorMX.class));
        cache.put(DnsRecordType.TXT, applicationContext.getBean(DnsResponseProcessorTXT.class));
    }

    /**
     * 根据DNS记录类型获取响应处理器
     *
     * @param type DNS记录类型
     * @return 响应处理器
     */
    public DnsResponseProcessor getProcessorByType(DnsRecordType type) {
        DnsResponseProcessor processor = cache.get(type);
        if (Objects.nonNull(processor)) {
            return cache.get(type);
        }
        throw new DnsException("not support record type");
    }
}
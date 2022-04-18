package com.github.xuchengen.xdns.config;

import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.handler.*;
import io.netty.handler.codec.dns.DnsRecordType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * DNS响应处理器工厂<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-18 13:09
 */
@Component(value = "dnsResponseProcessorFactory")
public class DnsResponseProcessorFactory {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 根据DNS记录类型获取响应处理器
     *
     * @param type DNS记录类型
     * @return 响应处理器
     */
    public DnsResponseProcessor getProcessorByType(DnsRecordType type) {
        DnsResponseProcessor handler;
        switch (type.intValue()) {
            case 1:
                handler = applicationContext.getBean(DnsResponseProcessorA.class);
                break;
            case 2:
                handler = applicationContext.getBean(DnsResponseProcessorNS.class);
                break;
            case 15:
                handler = applicationContext.getBean(DnsResponseProcessorMX.class);
                break;
            case 16:
                handler = applicationContext.getBean(DnsResponseProcessorTXT.class);
                break;
            default:
                throw new DnsException("not support record type");
        }
        return handler;
    }
}
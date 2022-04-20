package com.github.xuchengen.xdns.handler;

import com.github.xuchengen.xdns.annotation.DnsQuestionType;
import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.handler.processor.DefaultDnsRequestProcessor;
import com.github.xuchengen.xdns.handler.processor.DnsRequestProcessor;
import io.netty.handler.codec.dns.DnsRecordType;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * DNS请求处理器上下文<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-20 14:08
 */
@Component
public class DnsRequestProcessorContext {

    private static final Map<DnsRecordType, Class<? extends DnsRequestProcessor>> cache = new HashMap<>();

    private final ApplicationContext applicationContext;

    private static final Object lock = new Object();

    public DnsRequestProcessor get(DnsRecordType dnsRecordType) {
        if (Objects.isNull(dnsRecordType)) {
            throw new IllegalArgumentException("dns record type is null");
        }

        Class<? extends DnsRequestProcessor> clazz = cache.get(dnsRecordType);
        if (Objects.isNull(clazz)) {
            return applicationContext.getBean(DefaultDnsRequestProcessor.class);
        }

        return applicationContext.getBean(clazz);
    }

    public DnsRequestProcessorContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        synchronized (lock) {
            Map<String, DnsRequestProcessor> processorMap = applicationContext.getBeansOfType(DnsRequestProcessor.class);
            if (processorMap.size() == 0) {
                throw new DnsException("the system does not define a dns request processor");
            }

            for (Map.Entry<String, DnsRequestProcessor> entry : processorMap.entrySet()) {
                Class<? extends DnsRequestProcessor> processorClass = entry.getValue().getClass();
                DnsQuestionType annotation = processorClass.getAnnotation(DnsQuestionType.class);

                if (Objects.isNull(annotation)) {
                    annotation = AnnotationUtils.findAnnotation(processorClass, DnsQuestionType.class);
                }

                if (Objects.isNull(annotation)) {
                    continue;
                }

                String type = annotation.type();
                DnsRecordType dnsRecordType = DnsRecordType.valueOf(type);
                if (cache.containsKey(dnsRecordType)) {
                    throw new DnsException("duplicate dns request processor annotation");
                }

                cache.put(dnsRecordType, processorClass);
            }

            if (cache.isEmpty()) {
                throw new DnsException("the system does not define a dns request processor");
            }
        }
    }

}

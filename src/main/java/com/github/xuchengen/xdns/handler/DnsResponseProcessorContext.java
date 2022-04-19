package com.github.xuchengen.xdns.handler;

import com.github.xuchengen.xdns.annotation.DnsQuestionType;
import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.handler.processor.DnsResponseProcessor;
import io.netty.handler.codec.dns.DnsRecordType;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * DNS响应处理器上下文<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-19 14:00
 */
@Component
public class DnsResponseProcessorContext {

    private static final Map<DnsRecordType, Class<? extends DnsResponseProcessor>> cache = new HashMap<>();

    private final ApplicationContext applicationContext;

    private static final Object lock = new Object();

    public DnsResponseProcessor get(DnsRecordType dnsRecordType) {
        if (Objects.isNull(dnsRecordType)) {
            throw new IllegalArgumentException("dns record type is null");
        }

        Class<? extends DnsResponseProcessor> clazz = cache.get(dnsRecordType);
        if (Objects.isNull(clazz)) {
            throw new DnsException("not support record type");
        }

        return applicationContext.getBean(clazz);
    }

    public DnsResponseProcessorContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        synchronized (lock) {
            Map<String, DnsResponseProcessor> processorMap = applicationContext.getBeansOfType(DnsResponseProcessor.class);
            if (processorMap.size() == 0) {
                throw new DnsException("the system does not define a dns response processor");
            }

            for (Map.Entry<String, DnsResponseProcessor> entry : processorMap.entrySet()) {
                Class<? extends DnsResponseProcessor> processorClass = entry.getValue().getClass();
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
                    throw new DnsException("duplicate dns response processor annotation");
                }

                cache.put(dnsRecordType, processorClass);
            }

            if (cache.isEmpty()) {
                throw new DnsException("the system does not define a dns response processor");
            }
        }
    }
}

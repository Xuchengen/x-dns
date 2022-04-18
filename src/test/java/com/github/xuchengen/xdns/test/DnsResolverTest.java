package com.github.xuchengen.xdns.test;

import cn.hutool.json.JSONUtil;
import com.github.xuchengen.xdns.exception.DnsException;
import com.github.xuchengen.xdns.resolver.DnsResolver;
import com.github.xuchengen.xdns.resolver.RequestType;
import com.github.xuchengen.xdns.result.DnsResult;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * <br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-15 15:30
 */
public class DnsResolverTest extends BaseTest {

    @Resource(name = "dnsResolver")
    private DnsResolver dnsResolver;

    @Test
    void dnsResolveAsync() {
        List<CompletableFuture<Void>> listFuture = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String domainName;
            if (i % 3 == 0) {
                domainName = "taobao.com";
            } else if (i % 3 == 1) {
                domainName = "jd.com";
            } else {
                domainName = "alipay.com";
            }

            CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() ->
                            dnsResolver.resolveDomainByTcp("8.8.8.218", domainName, RequestType.REQUEST_A))
                    .exceptionally(throwable ->
                    {
                        System.out.println("exceptionally : " + throwable.getMessage());
                        return new DnsResult(DnsResult.Type.A, domainName, Collections.emptyList());
                    })
                    .thenAccept(e -> System.out.println("这是什么：" + JSONUtil.toJsonStr(e)));

            listFuture.add(completableFuture);
        }

        try {
            listFuture.stream().map(CompletableFuture::join).forEach(System.out::println);
        } catch (CompletionException ce) {
            System.out.println("ce : " + ce.getMessage());
        } catch (DnsException de) {
            System.out.println("de : " + de.getMessage());
        }
    }

    @Test
    void dnsResolveSync() {
        for (int i = 0; i < 100; i++) {
            String domainName;
            if (i % 3 == 0) {
                domainName = "naver.com";
            } else if (i % 3 == 1) {
                domainName = "google.com";
            } else {
                domainName = "kakao.com";
            }

            DnsResult result = dnsResolver.resolveDomainByUdp("223.5.5.5", domainName, RequestType.REQUEST_A);
            System.out.println(JSONUtil.toJsonStr(result.getRecords()));
        }
    }
}

package com.github.xuchengen.xdns.test;

import com.github.xuchengen.xdns.utils.DomainUtil;
import org.junit.jupiter.api.Test;

/**
 * <br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-24 15:18
 */
public class OtherTest {

    @Test
    public void t1() {
        System.out.println(DomainUtil.cleanRoot("com........"));
    }

}

package com.github.xuchengen.xdns.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

/**
 * DNS编解码工具类<br>
 * Netty官方工具类受包权限限制<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-20 16:39
 */
public class DnsCodecUtil {

    private static final String ROOT = ".";

    public static void encodeDomainName(String name, ByteBuf buf) {
        if (ROOT.equals(name)) {
            buf.writeByte(0);
            return;
        }

        final String[] labels = name.split("\\.");
        for (String label : labels) {
            final int labelLen = label.length();
            if (labelLen == 0) {
                break;
            }

            buf.writeByte(labelLen);
            ByteBufUtil.writeAscii(buf, label);
        }

        buf.writeByte(0);
    }
}

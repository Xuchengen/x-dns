package com.github.xuchengen.xdns.result;

/**
 * DNS MX记录结果<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 2022-04-21 14:53
 */
public class DnsResultMX {

    private Integer preference;

    private String mailExchange;

    public Integer getPreference() {
        return preference;
    }

    public void setPreference(Integer preference) {
        this.preference = preference;
    }

    public String getMailExchange() {
        return mailExchange;
    }

    public void setMailExchange(String mailExchange) {
        this.mailExchange = mailExchange;
    }

    public DnsResultMX(Integer preference, String mailExchange) {
        this.preference = preference;
        this.mailExchange = mailExchange;
    }
}

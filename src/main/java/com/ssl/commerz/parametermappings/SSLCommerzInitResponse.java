package com.ssl.commerz.parametermappings;

import java.util.List;

public class SSLCommerzInitResponse {

    public String status;
    public String failedreason;
    public String sessionkey;
    public Gw gw;
    public String redirectGatewayURL;
    public String redirectGatewayURLFailed;
    public String GatewayPageURL;
    public String storeBanner;
    public String storeLogo;
    public List<Desc> desc;
    public String is_direct_pay_enable;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFailedreason(String failedreason) {
        this.failedreason = failedreason;
    }

    public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }

    public void setGw(Gw gw) {
        this.gw = gw;
    }

    public void setRedirectGatewayURL(String redirectGatewayURL) {
        this.redirectGatewayURL = redirectGatewayURL;
    }

    public void setRedirectGatewayURLFailed(String redirectGatewayURLFailed) {
        this.redirectGatewayURLFailed = redirectGatewayURLFailed;
    }

    public void setGatewayPageURL(String gatewayPageURL) {
        GatewayPageURL = gatewayPageURL;
    }

    public void setStoreBanner(String storeBanner) {
        this.storeBanner = storeBanner;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public void setDesc(List<Desc> desc) {
        this.desc = desc;
    }

    public void setIs_direct_pay_enable(String is_direct_pay_enable) {
        this.is_direct_pay_enable = is_direct_pay_enable;
    }

    public String getStatus() {
        return status;
    }

    public String getFailedreason() {
        return failedreason;
    }

    public String getSessionkey() {
        return sessionkey;
    }

    public Gw getGw() {
        return gw;
    }

    public String getRedirectGatewayURL() {
        return redirectGatewayURL;
    }

    public String getRedirectGatewayURLFailed() {
        return redirectGatewayURLFailed;
    }

    public String getGatewayPageURL() {
        return GatewayPageURL;
    }

    public String getStoreBanner() {
        return storeBanner;
    }

    public String getStoreLogo() {
        return storeLogo;
    }

    public List<Desc> getDesc() {
        return desc;
    }

    public String getIs_direct_pay_enable() {
        return is_direct_pay_enable;
    }

}
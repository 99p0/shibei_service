///*
// *  Birdplanet.com Inc.
// *  Copyright (c) 2019-2019 All Rights Reserved.
// */
//
//package com.birdplanet.service.pay.config.alipay;
//
//import com.birdplanet.service.pay.config.PayConfig;
//
///**
// * @author 杨润[uncle.yang@outlook.com]
// * @title: AlipayConfig
// * @date 2019-07-08 13:09
// */
//@PayConfig(name = "alipay.zmn")
//public class ZmnAlipayConfig implements IAlipayConfig {
//
//  @Override public String getAppId() {
//    return "2019060365454173";
//  }
//
//  @Override public String getServerUrl() {
//    return "https://openapi.alipay.com/gateway.do";
//  }
//
//  @Override public String getPrivateKey() {
//    return "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDLqPktQa+4sAQp\n"
//        + "hBAmtrHahhAVCuIg0F1OSXgrKabYYbPGFRzn1LcGjqEnaYn4nyFzs9iuT5h6IN9R\n"
//        + "P2b57gwb0XW2y4GRkk3eAWVDQkJs4gsX7WrgHQ/a2B11fNChX3EQVcOL8mIyb3Xa\n"
//        + "GxnUcUsg7uY/xkYUMK2JGfHNBF4Doqbc4ou7NHvQT4y2/rMvKFh18qXo4okkiRDn\n"
//        + "NRqsJgd1PLt3ARmQzC41jBjEiICzSvRpCp3MgqmjYA6j7R0pAPqMkppPnOWEX5Zb\n"
//        + "1eL3ztiB1x0vcpyGVy+RV1F71FIzpQ1FvD+j37USEYKrK8V4jMk9gAQDkaB10xZQ\n"
//        + "hWuwM7pbAgMBAAECggEADaIDBA+IlsFZlfncJutF3o3o16/B0VaBWHM8kw8XtxZb\n"
//        + "NWBGbhJkKhE0InJDJRxp3dflIk37s/ktWb/3wbU4pEgSLClVRAxaIAlR4P23ephr\n"
//        + "C6/93oCzhf+/gRTY1mSQKfrDu0N/ZUbmybHUuc7Xq52yVgSMvl8/zpSpBgNL08xZ\n"
//        + "1ZmOFIOlRZU0ZZmXHeUKtm91zD/Kvl9KtyRCC1Wu4IjJ8sVe0N9RuLICgfFNw6Go\n"
//        + "oT/3pPYSjb4RI8LeOp3ovVdCkxle8FV+zvMhSWRS3P/EdvgJq/LuR5HptWbj7rmW\n"
//        + "+g9o1GKKtpVKOa8xVr865HtOCvPej3X3JwZL/c45MQKBgQD5HOOJXKoebmD+hk1P\n"
//        + "y/cfyBH9yi6G/foc56nplxwlbui9ZxFd0bRhyThD6MQaTmBKgUxFrM1bg+gedsMY\n"
//        + "vP1PUS92hUuc8GHNkAcwuVIXP3GO5n8fPIDGoLTWPKU/UdpY4lgajuSClwiGG1u4\n"
//        + "RJp+LTHbUTPcydjAv7vYvU59wwKBgQDRSmO/UPW/wSNeAEJ5LwOrbyL96uiWAroS\n"
//        + "YB8b3XFkFWcprbUrewtGrKZGL2ZZnlACfgd332sbXZRdE1AQlvR52Yb5Z3f4W7Zo\n"
//        + "qSYJ64fE28XuD0SQfNzB17TJ1OLJByqZGVEvp/QwX2AA/4liHKFCnoDHo322Z/bp\n"
//        + "9gF595wPiQKBgHKNx1BuTUiLJ41Dwx+TwsNZpHRZWgX3muFZ4repJmdMVjhqwgCr\n"
//        + "o1BoULhhvHQAYKVCSgmsSg7wXHhN0q94kN+9jJ7q8vdrTv5RYg0U21wXb6fv9Lsv\n"
//        + "fmRYmHAAGVlB0a+X/j3QQeKPg3rv78CPw5+MNuYMtFWueD9CrCYh/4szAoGAKRWC\n"
//        + "SS8aXr8KM0y3v9Qz0zZo1xGDANeeMTc/RBgPR0dQTgU0eEmaKBCCP2rUm6kRKbwV\n"
//        + "uTlZbDDNjAvbfrYzXawO6+ycx1sxjpAN0Iq0xzchPp8upXJtX3v5mFk6/51xhkZL\n"
//        + "AqDktY4zaV7AWEsoeMxmmUoFxoXYGYtaNNgfgYkCgYEA5C9ZqMdnVKFeazWDY2T4\n"
//        + "g6sHW+2Iz95roijh39XOa/Dwj4Zy4k1eOOQfTAs5k9nwWwxFx6LzP4SJqAlzKqtd\n"
//        + "di1XR+oKMmQpTaGi3XiZQdYD7Pna1Jn0Mu0oM7OVrtUkk55us2Ji6Hc2yf6AOEl1\n"
//        + "2WJF0I6ojpWldi3+e5EBKDo=";
//  }
//
//  @Override public String getPublicKey() {
//    return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy6j5LUGvuLAEKYQQJrax2oYQFQriINBdTkl4Kymm2GGzxhUc59S3Bo6hJ2mJ+J8hc7PYrk+YeiDfUT9m+e4MG9F1tsuBkZJN3gFlQ0JCbOILF+1q4B0P2tgddXzQoV9xEFXDi/JiMm912hsZ1HFLIO7mP8ZGFDCtiRnxzQReA6Km3OKLuzR70E+Mtv6zLyhYdfKl6OKJJIkQ5zUarCYHdTy7dwEZkMwuNYwYxIiAs0r0aQqdzIKpo2AOo+0dKQD6jJKaT5zlhF+WW9Xi987YgdcdL3KchlcvkVdRe9RSM6UNRbw/o9+1EhGCqyvFeIzJPYAEA5GgddMWUIVrsDO6WwIDAQAB";
//  }
//
//  @Override public String getPublicKeyAlipay() {
//    return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAppZVcMnU6+UREU+LegGtFhK6RTa2MSjKuH+BAvD7vMmHH9mBmkmnGLHGG2k+0pqeAyPwGgyWIHtV/PCpt5y6qWEYvFpRbm0mpAlUEs2AWgr3MVdmnetOREkNkwY+ie1YMNA6VhKvmWbIfHKSEHvz8kCI595loua5mXBSpJ5f31XmMLZIp9YgyAfj7eUuij1fHFL+HTDQjsQRPh/jFk6xspocpD/5JaxtdO0wwnP72455clO+hGff2ALFgU1Rgoplp4GzHx6SOXGoUgWMPdGwbgpLpmQ2BGD2sFBLxMPHs+WQ25CIQgSWsXcZz5CNYQAGVkQWt+owZSd3p+nNvQw1IwIDAQAB";
//  }
//
//  @Override public String getReturnUrl() {
//    return "https://alipay.zhuomuniaodaka.com/mine/recharge_result";
//  }
//
//  @Override public String getNotifyUrl() {
//    return "https://api.zhuomuniaodaka.com/punch/callback/alipay/notify";
//  }
//
//  @Override public String getGrantType() {
//    return "authorization_code";
//  }
//
//  @Override public String getRedirectUrl() {
//    return "https://alipay.zhuomuniaodaka.com/alipay_oauth_redirect";
//  }
//
//  @Override public String getCharset() {
//    return "UTF-8";
//  }
//
//  @Override public String getFormat() {
//    return "json";
//  }
//
//  @Override public String getSignType() {
//    return "RSA2";
//  }
//
//  @Override public String getPayChannelDef() {
//    return "balance,pcredit,pcreditpayInstallment,moneyFund,debitCardExpress,bankPay,creditCard,creditCardExpress,creditCardCartoon";
//  }
//}

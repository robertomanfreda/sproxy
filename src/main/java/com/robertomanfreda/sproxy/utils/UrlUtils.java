package com.robertomanfreda.sproxy.utils;

import org.springframework.http.RequestEntity;

public class UrlUtils {

    public static String removeSecWafUrlFromUrl(RequestEntity<?> request) {
        String originalUrl = request.getUrl().getAuthority() + "/";

        if (null != request.getUrl().getScheme()) {
            originalUrl = request.getUrl().getScheme() + "://" + originalUrl;
        }

        return request.getUrl().toString().replaceFirst(originalUrl, "");
    }

}

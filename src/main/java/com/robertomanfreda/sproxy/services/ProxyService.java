package com.robertomanfreda.sproxy.services;

import com.robertomanfreda.sproxy.exceptions.ProxyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

@Slf4j
@Service
public class ProxyService {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";

    private final HttpClient httpClient = HttpClients.createDefault();

    public <T extends HttpRequestBase> HttpResponse doProxy(HttpEntity<?> request, T httpRequest)
            throws ProxyException, UnsupportedEncodingException {

        if (!request.getHeaders().isEmpty()) setHeaders(httpRequest, request);
        if (request.hasBody()) setBody(httpRequest, request);

        String url = httpRequest.getURI().toString();
        if (url.contains(HTTPS) || url.contains(HTTP)) {
            return directProxy(httpRequest);
        } else {
            return automaticProxy(httpRequest, url);
        }
    }

    private <T extends HttpRequestBase> HttpResponse directProxy(T httpRequest) throws ProxyException {
        HttpResponse httpResponse;

        try {
            httpResponse = httpClient.execute(httpRequest);
        } catch (IOException e) {
            log.error("directProxyGet(...) failed - using {}", HTTPS);
            throw new ProxyException("Direct proxy ERROR.");
        }

        return httpResponse;
    }

    private <T extends HttpRequestBase> HttpResponse automaticProxy(T httpRequest, String url) throws ProxyException {
        HttpResponse httpResponse;

        // Try using https
        try {
            httpResponse = sendRequestWithCustomProtocol(httpRequest, HTTPS, url);
        } catch (IOException e) {
            log.error("automaticProxyGet(...) failed - using {} - calling url {}", HTTPS, url);

            // Try using http
            try {
                httpResponse = sendRequestWithCustomProtocol(httpRequest, HTTP, url);
            } catch (IOException ioe) {
                log.error("automaticProxyGet(...) failed - using {} - calling url {}", HTTP, url);
                throw new ProxyException("Automatic proxy ERROR.");
            }
        }

        return httpResponse;
    }

    private <T extends HttpRequestBase> void setHeaders(T httpRequest, HttpEntity<?> request) {
        request.getHeaders().forEach((key1, value) -> {
            // Without ignoring these headers calls will fail!
            if (!key1.equalsIgnoreCase("host") && !key1.equalsIgnoreCase("content-length")) {
                httpRequest.addHeader(key1, value.get(0));
            }
        });
    }

    private <T extends HttpRequestBase> void setBody(T httpRequest,
                                                     HttpEntity<?> request) throws UnsupportedEncodingException {
        List<String> contentType = request.getHeaders().get("content-type");

        if (null != request.getBody() && null != contentType) {
            switch (contentType.get(0)) {
                case MediaType.APPLICATION_JSON_VALUE:
                    // TODO should manage with other types too
                    ((HttpPost) httpRequest).setEntity(new StringEntity(request.getBody().toString()));
                    break;
                case MediaType.APPLICATION_FORM_URLENCODED_VALUE:
                    break;
                default:
                    throw new UnsupportedEncodingException("Provided " + contentType.get(0) + " not supported");
            }
        }
    }

    private <T extends HttpRequestBase> HttpResponse sendRequestWithCustomProtocol(T httpRequest, String protocol,
                                                                                   String url) throws IOException {
        httpRequest.setURI(URI.create(protocol + "://" + url));
        return httpClient.execute(httpRequest);
    }

}

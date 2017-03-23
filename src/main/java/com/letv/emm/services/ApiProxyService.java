package com.letv.emm.services;

import com.letv.emm.vo.ProxyErrorVo;
import org.apache.http.client.methods.*;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ApiProxyService {
    private static final String ProxyUrlNamed = "url";

    private static final String[] expectHeaders = new String[]{ "host", "content-length", "url", ProxyUrlNamed};

    public void invokeApiProxy(HttpServletRequest request, HttpServletResponse response) {
        String httpMethod = request.getMethod();
        //if proxy url is empty return error message
        String proxyUrl = request.getHeader(ProxyUrlNamed);
        if (null == proxyUrl || proxyUrl.isEmpty()) {
            errorMessage(500, "proxy url isEmpty", response);
            return;
        }
        String url = "";
        if (null != request.getQueryString() && !request.getQueryString().isEmpty()) {
            url = proxyUrl + "?" + request.getQueryString();
        } else {
            url = proxyUrl;
        }

        if (!isUrl(url)) {
            errorMessage(501, "proxy url illegal", response);
            return;
        }

        HttpRequestBase httpRequestBase = null;
        if (httpMethod.equalsIgnoreCase(HttpGet.METHOD_NAME)) {
            httpRequestBase = new HttpGet(url);
            initRequestHeader(request, httpRequestBase);
        } else if (httpMethod.equalsIgnoreCase(HttpHead.METHOD_NAME)) {
            httpRequestBase = new HttpHead(url);
            initRequestHeader(request, httpRequestBase);
        } else if (httpMethod.equalsIgnoreCase(HttpPost.METHOD_NAME)) {
            httpRequestBase = new HttpPost(url);
            initRequestHeader(request, httpRequestBase);
            ((HttpPost) httpRequestBase).setEntity(getInputStreamEntry(request));
        } else if (httpMethod.equalsIgnoreCase(HttpPut.METHOD_NAME)) {
            httpRequestBase = new HttpPut(url);
            initRequestHeader(request, httpRequestBase);
            ((HttpPut) httpRequestBase).setEntity(getInputStreamEntry(request));
        } else if (httpMethod.equalsIgnoreCase(HttpPatch.METHOD_NAME)) {
            httpRequestBase = new HttpPatch(url);
            initRequestHeader(request, httpRequestBase);
            ((HttpPatch) httpRequestBase).setEntity(getInputStreamEntry(request));
        } else if (httpMethod.equalsIgnoreCase(HttpDelete.METHOD_NAME)) {
            httpRequestBase = new HttpDelete(url);
            initRequestHeader(request, httpRequestBase);
        } else if (httpMethod.equalsIgnoreCase(HttpOptions.METHOD_NAME)) {
            httpRequestBase = new HttpOptions(url);
            initRequestHeader(request, httpRequestBase);
        } else if (httpMethod.equalsIgnoreCase(HttpTrace.METHOD_NAME)) {
            httpRequestBase = new HttpTrace(url);
            initRequestHeader(request, httpRequestBase);
        }

        execute(httpRequestBase, response);
    }

    private void initRequestHeader(HttpServletRequest request, HttpRequestBase httpRequestBase) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!contains(headerName)) {
                httpRequestBase.addHeader(headerName, request.getHeader(headerName));
            }
        }
    }

    private boolean contains(String headerName) {
        for (String expectHeader : expectHeaders) {
            if (headerName.equalsIgnoreCase(expectHeader)) {
                return true;
            }
        }
        return false;
    }

    private void flushResponse(CloseableHttpResponse closeableHttpResponse, HttpServletResponse response) {
        if (null != closeableHttpResponse) {
            long contentLength = closeableHttpResponse.getEntity().getContentLength();
            InputStream input = null;
            try {
                input = closeableHttpResponse.getEntity().getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.setContentLength((int) contentLength);
            byte[] buffer = new byte[10240];

            try {
                OutputStream output = response.getOutputStream();
                for (int length = 0; (length = input.read(buffer)) > 0; ) {
                    output.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void execute(HttpRequestBase httpRequestBase, HttpServletResponse response) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            closeableHttpResponse = httpClient.execute(httpRequestBase);
        } catch (IOException e) {
            e.printStackTrace();
        }
        flushResponse(closeableHttpResponse, response);
    }

    private InputStreamEntity getInputStreamEntry(HttpServletRequest request) {
        InputStreamEntity inputEntry = null;
        try {
            InputStream inputStream = request.getInputStream();
            inputEntry = new InputStreamEntity(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputEntry;
    }

    private void errorMessage(int code, String message, HttpServletResponse response) {
        ProxyErrorVo error = new ProxyErrorVo();
        error.setMessage(message);
        error.setStatus(code);
        JSONObject errorObject = new JSONObject(error);
        try {
            response.getWriter().write(errorObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isUrl(String url) {
        if (url == null) {
            return false;
        }
        String regEx = "^(https?|ftp|file)://[a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(url);
        return matcher.matches();
    }
}

package com.jn.agileway.http.rr;

import java.util.Enumeration;
import java.util.Locale;

public interface HttpRequest<D> {
    D getDelegate();
    String getRemoteHost();
    String getMethod();
    String getRequestURI();
    String getHeader(String name);
    Enumeration<String> getHeaderNames();
    Enumeration<String> getHeaders(String name);
    Object getAttribute(String name);
    void setAttribute(String name, Object value);
    Locale getLocale();
    StringBuffer getRequestURL();


}

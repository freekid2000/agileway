package com.jn.agileway.jaxrs.rr;

import com.jn.agileway.http.rr.HttpResponse;

import javax.ws.rs.container.ContainerResponseContext;
import java.util.Collection;

public class JaxrsHttpResponse implements HttpResponse<ContainerResponseContext> {
    private ContainerResponseContext delegate;

    public ContainerResponseContext getDelegate(){
        return this.delegate;
    }

    @Override
    public void addHeader(String name, String value) {

    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public int getStatusCode() {
        return this.delegate.getStatus();
    }
}

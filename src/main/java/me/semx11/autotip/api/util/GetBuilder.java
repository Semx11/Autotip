package me.semx11.autotip.api.util;

import me.semx11.autotip.api.request.AbstractRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

public class GetBuilder {

    private static final String BASE_URL = "https://api.autotip.pro/";

    private final RequestBuilder builder;

    private GetBuilder(AbstractRequest request) {
        this.builder = RequestBuilder.get().setUri(BASE_URL + request.getType().getEndpoint());
    }

    public static GetBuilder of(AbstractRequest request) {
        return new GetBuilder(request);
    }

    public GetBuilder addParameter(String name, Object value) {
        this.builder.addParameter(name, value.toString());
        return this;
    }

    public HttpUriRequest build() {
        return this.builder.build();
    }

}

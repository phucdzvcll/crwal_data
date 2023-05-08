package models;

import org.openqa.selenium.devtools.v85.network.model.RequestId;

import java.util.Objects;

public class InternalRequest {
    final RequestId requestId;
    final String requestUrl;

    public InternalRequest(RequestId requestId, String requestUrl) {
        this.requestId = requestId;
        this.requestUrl = requestUrl;
    }

    public RequestId getRequestId() {
        return requestId;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalRequest request = (InternalRequest) o;
        return Objects.equals(requestId.toString(), request.requestId.toString()) && Objects.equals(requestUrl, request.requestUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, requestUrl);
    }
}

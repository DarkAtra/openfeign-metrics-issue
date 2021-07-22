package de.darkatra;

import feign.Response;
import feign.codec.ErrorDecoder;

public class PersonClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(final String methodKey, final Response response) {
        // note: this is not a feign exception
        return new ClientException(methodKey + " resulted in status: " + response.status());
    }
}

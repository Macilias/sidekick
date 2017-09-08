package com.macilias.apps.model;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public class Response {

    private String responseText;
    private Set<ResponseArgument> responseArguments = new HashSet<>();

    public Response(String responseText) {
        this.responseText = responseText;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public Optional<ResponseArgument> getResponseArgument(ResponseArgumentName name) {
        for (ResponseArgument responseArgument : responseArguments) {
            if (responseArgument.getName().equals(name)) {
                return Optional.of(responseArgument);
            }
        }
        return Optional.empty();
    }

    public Set<ResponseArgument> getResponseArguments() {
        return responseArguments;
    }

    public void addResponseArgument(ResponseArgument responseArgument) {
        responseArguments.add(responseArgument);
    }

    @Override
    public String toString() {
        return "Response{" +
                "responseText='" + responseText + '\'' +
                ", responseArguments=" + StringUtils.join(responseArguments, ",") +
                '}';
    }
}

package com.macilias.apps.model;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public class ResponseArgument {

    ResponseArgumentName name;
    String value;

    public ResponseArgument(ResponseArgumentName name, String value) {
        this.name = name;
        this.value = value;
    }

    public ResponseArgumentName getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ResponseArgument{" +
                "name=" + name.name() +
                ", value='" + value + '\'' +
                '}';
    }
}

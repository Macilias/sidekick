package com.macilias.apps.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public enum ArgumentName {

    WHERE, WHAT, SINCE, FAKED_UPDATE;

    public static Set<String> getLowerValues() {
        ArgumentName[] values = values();
        Set<String> result = new HashSet<>();
        for (int i = 0; i < values.length; i++) {
            result.add(values[i].name().toLowerCase());
        }
        return result;
    }

    public static ArgumentName fromValue(String value) {

        ArgumentName[] values = values();
        for (ArgumentName argumentName : values) {
            if(value.equalsIgnoreCase(argumentName.name().toLowerCase())) {
                return argumentName;
            }
        }

        throw new RuntimeException("the provided value " + value + " is not a valid argument");

    }

}

package com.macilias.apps.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public enum Intent {

    UPDATE, FOLLOWER_COUNT, GET_COMMENTS, GET_NEW, POST;

    public static Set<String> getLowerValues() {
        Intent[] values = values();
        Set<String> result = new HashSet<>();
        for (int i = 0; i < values.length; i++) {
            result.add(values[i].name().toLowerCase());
        }
        return result;
    }

    public static Intent fromValue(String value) {

        Intent[] values = values();
        for (Intent intent : values) {
            if(value.equalsIgnoreCase(intent.name().toLowerCase())) {
                return intent;
            }
        }

        throw new RuntimeException("the provided value " + value + " is not a valid intent");

    }

}

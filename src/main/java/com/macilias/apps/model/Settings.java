package com.macilias.apps.model;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public interface Settings {

    public static final String CROWD_TANGLE_API_TOKEN = System.getenv("CROWD_TANGLE_API_TOKEN");
    public static final String CROWD_TANGLE_LIST_ID = System.getenv("CROWD_TANGLE_LIST_ID");

}

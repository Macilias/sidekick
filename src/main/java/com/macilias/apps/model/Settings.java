package com.macilias.apps.model;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public interface Settings {

    int FUSEKI_PORT = 4321;

    String CROWD_TANGLE_API_TOKEN = System.getenv("CROWD_TANGLE_API_TOKEN");
    String CROWD_TANGLE_LIST_ID = System.getenv("CROWD_TANGLE_LIST_ID");

    String FACEBOOK_APP_ID = System.getenv("FACEBOOK_APP_ID");
    String FACEBOOK_APP_SECRET = System.getenv("FACEBOOK_APP_SECRET");
    String FACEBOOK_ACCESS_TOKEN = System.getenv("FACEBOOK_ACCESS_TOKEN");
    String FACEBOOK_PERMISSIONS = System.getenv("FACEBOOK_PERMISSIONS");
    String FACEBOOK_CALLBACK_URL = System.getenv("FACEBOOK_CALLBACK_URL");

}

package com.macilias.apps.service.facebook;

import com.macilias.apps.service.Service;
import facebook4j.Facebook;
import facebook4j.FacebookFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public class FacebookService implements Service {

    Facebook facebook = new FacebookFactory().getInstance();


    @Override
    public int directContactCount(Optional<String> where, Optional<String> since) throws IOException {
        return 0;
    }
}

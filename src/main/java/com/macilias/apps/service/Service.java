package com.macilias.apps.service;

import java.io.IOException;
import java.util.Optional;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public interface Service {

    int directContactCount(Optional<String> where, Optional<String> since) throws IOException;

}

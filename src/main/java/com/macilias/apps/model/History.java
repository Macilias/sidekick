package com.macilias.apps.model;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public class History {

    private static final Logger LOG = Logger.getLogger(History.class);

    private List<Request> previousRequests = new ArrayList<>();

    public Optional<Request> getLastRequestByIntent(Intent intent, Optional<ArgumentName> argumentName) {
        LOG.info("getLastRequestByIntent(): history size: " +previousRequests.size()+ ", searching history for " + intent.name()  + (argumentName.isPresent() ?  " with argument " + argumentName.get() : ""));
        if (previousRequests.size() == 0) {
            LOG.info("I don`t know about any previous requests yet.");
            return Optional.empty();
        }
        for (int i = previousRequests.size() - 1; i >= 0 ; i--) {
            Request previousRequest = previousRequests.get(i);
            if (previousRequest.getIntent() != null && previousRequest.getIntent().equals(intent)){
                LOG.info("getLastRequestByIntent(): a request with same intent " + intent.name() + " has been found" + (argumentName.isPresent() ?  ". checking for argument " + argumentName.get() : ""));
                if (argumentName.isPresent()) {
                    if (previousRequest.getOptionalArgument(argumentName.get()).isPresent()) {
                        // if argumentName has been specified it also needs to be part of the last request
                        LOG.info("getLastRequestByIntent() found this one: " + previousRequest);
                        return Optional.of(previousRequest);
                    }
                } else {
                    // if no argumentName has been specified this is the last request
                    LOG.info("getLastRequestByIntent(): found previous matching request fo intent");
                    return Optional.of(previousRequest);
                }
            } else {
                LOG.info("its not " + previousRequest + " on position: " + i);
            }

        }
        LOG.info("Nothing matched your query.");
        return Optional.empty();
    }

    public Optional<Request> getLastRequest() {
        if (previousRequests.size() > 0) {
            return Optional.of(previousRequests.get(previousRequests.size() -1));
        }
        return Optional.empty();
    }

    public void addRequest(Request request) {
        LOG.info("addRequest(): " + request.toString());
        previousRequests.add(request);
    }

}

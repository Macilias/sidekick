package com.macilias.apps.model;

import org.apache.jena.query.*;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.sse.SSE;
import org.apache.jena.system.Txn;
import org.apache.log4j.Logger;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class manages past queries and provides a method to get related
 *
 * @author maciej.niemczyk@voipfuture.com
 */
@Repository
public class History {

    private static final Logger LOG = Logger.getLogger(History.class);

    private List<Request> previousRequests = new ArrayList<>();

    @SpringBean(name = "embeddedDb")
    EmbeddedDb embeddedDb;

    /**
     * A query is matching when it served the same intent and had same filter as arguments like WHERE with same values.
     * Some arguments might be also checked by name, like WHAT in last post or
     *
     * @param intent        the Intent of the query, the last query is one without an intent (arguments and argumentNames are ignored)
     * @param arguments     optional, if present not only the name, but also the values (either exact or at least one) must match
     * @param argumentNames optional, if present not only the name, but also the values (either exact or at least one) must match
     * @param exact         defines if all of the argument values must be identical
     *                      if exact == true:
     *                      a argument WHERE query for facebook & twitter does not match a previous query for facebook only
     *                      a argument WHERE query for facebook only does not match a previous query for facebook and twitter
     *                      a argumentName query for WHERE does not match queries with additional filters like SINCE
     *                      if exact == false:
     *                      the presented queries returns true, because the previous query is not exact but RELATED
     * @return
     */
    public Optional<Request> getLastMatchingRequest(Intent intent, List<Argument> arguments, List<ArgumentName> argumentNames, boolean exact) {
//        LOG.info("getLastRequestByIntent(): history size: " +previousRequests.size()+ ", searching history for " + intent.name() + " with  " + arguments.size()+ " arguments and " + argumentNames.size() + " argument names" );
//        if (previousRequests.size() == 0) {
//            LOG.info("I don`t know about any previous requests yet.");
//            return Optional.empty();
//        }
//        for (int i = previousRequests.size() - 1; i >= 0 ; i--) {
//            Request previousRequest = previousRequests.get(i);
//            if (previousRequest.getIntent() != null && previousRequest.getIntent().equals(intent)){
//                LOG.info("getLastMatchingRequest(): a request with same intent " + intent.name() + " has been found");
//
//                if (current.getOptionalArgument(ArgumentName.WHERE).isPresent()) {
//                    Optional<Argument> previousRequestOptionalWhere = previousRequest.getOptionalArgument(ArgumentName.WHERE);
//                    if (previousRequestOptionalWhere.isPresent()) {
//                        Argument previousArgument = previousRequestOptionalWhere.get();
//                        if (previousArgument.containsAllValues(current.getOptionalArgument(ArgumentName.WHERE).get().getArgumentValues())) {
//                            // if argumentName has been specified it also needs to be part of the last request in case the value matches
//                            LOG.info("getLastMatchingRequest() found this one: " + previousRequest);
//                            return Optional.of(previousRequest);
//                        } else {
//                            LOG.info("it's not " + previousRequest + " on position: " + i + ", the values does not match.");
//                        }
//                    }
//                } else {
//                    // if no WHERE argumentName has been specified this is the last request
//                    LOG.info("getLastMatchingRequest(): found previous matching request fo intent");
//                    return Optional.of(previousRequest);
//                }
//            } else {
//                LOG.info("its not " + previousRequest + " on position: " + i);
//            }
//
//        }
//        LOG.info("Nothing matched your query.");
        return Optional.empty();
    }

    public Optional<Request> getLastRequest() {
        if (previousRequests.size() > 0) {
            return Optional.of(previousRequests.get(previousRequests.size() - 1));
        }
        return Optional.empty();
    }

    public void addRequest(Request request) {
        LOG.info("addRequest(): " + request.toString());
        previousRequests.add(request);
    }

    public void performWrite() {
        DatasetGraph dsg = embeddedDb.getDs().asDatasetGraph();
        // Add some data while live.
        // Write transaction.
        Txn.executeWrite(dsg, () -> {
            Quad q = SSE.parseQuad("(_ :s :p _:b)");
            dsg.add(q);
        });
    }

    public void performRead() {
        DatasetGraph dsg = embeddedDb.getDs().asDatasetGraph();
        // Query data while live
        // Read transaction.
        Txn.executeRead(dsg, () -> {
            Dataset ds = DatasetFactory.wrap(dsg);
            try (QueryExecution qExec = QueryExecutionFactory.create("SELECT * { ?s  ?o  ?z}", ds)) {
                ResultSet rs = qExec.execSelect();
                ResultSetFormatter.out(rs);
            }
        });
    }

    public void setEmbeddedDb(EmbeddedDb embeddedDb) {
        this.embeddedDb = embeddedDb;
    }
}

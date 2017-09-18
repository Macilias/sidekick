package com.macilias.apps.model;

import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.fuseki.Fuseki;
import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;

/**
 * Some Description
 *
 * @author maciej.niemczyk@voipfuture.com
 */
public class EmbeddedDb {

    FusekiServer server;
    Dataset ds;

    public FusekiServer getServer() {
        if (server == null) {
            server = FusekiServer.create()
                    .setPort(Settings.FUSEKI_PORT)
                    .add("/ds", getDs())
                    .build();
        }
        return server;
    }

    public Dataset getDs() {
        if (ds == null) {
            ds = DatasetFactory.createTxnMem();
        }
        return ds;
    }

    public static void silance() {
        LogCtl.setLevel(Fuseki.serverLogName, "WARN");
        LogCtl.setLevel(Fuseki.actionLogName, "WARN");
        LogCtl.setLevel(Fuseki.requestLogName, "WARN");
        LogCtl.setLevel(Fuseki.adminLogName, "WARN");
        LogCtl.setLevel("org.eclipse.jetty", "WARN");
    }
}

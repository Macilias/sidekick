package com.macilias.apps.service;

import com.macilias.apps.model.Settings;
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

    public static FusekiServer getServer() {
        Dataset ds = DatasetFactory.createTxnMem();
        FusekiServer server = FusekiServer.create()
                .setPort(Settings.FUSEKI_PORT)
                .add("/ds", ds)
                .build();
        return server;
    }

    public static void silance() {
        LogCtl.setLevel(Fuseki.serverLogName,  "WARN");
        LogCtl.setLevel(Fuseki.actionLogName,  "WARN");
        LogCtl.setLevel(Fuseki.requestLogName, "WARN");
        LogCtl.setLevel(Fuseki.adminLogName,   "WARN");
        LogCtl.setLevel("org.eclipse.jetty",   "WARN");
    }
}

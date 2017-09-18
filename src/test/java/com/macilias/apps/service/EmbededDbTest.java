package com.macilias.apps.service;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.atlas.web.TypedInputStream;
import org.apache.jena.fuseki.embedded.FusekiEmbeddedServer;
import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.jena.fuseki.server.DataService;
import org.apache.jena.fuseki.server.OperationName;
import org.apache.jena.graph.Graph;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.web.HttpOp;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.sparql.sse.SSE;
import org.apache.jena.system.Txn;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.OutputStream;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class EmbededDbTest {

    @Test
    public  void embedded_04() {
        DatasetGraph dsg = dataset() ;
        Txn.executeWrite(dsg,  ()->{
            Quad q = SSE.parseQuad("(_ :s :p _:b)") ;
            dsg.add(q);
        }) ;

        // A service with just being able to do quads operations
        // That is, GET, POST, PUT on  "/data" in N-quads and TriG.
        DataService dataService = new DataService(dsg) ;
        dataService.addEndpoint(OperationName.Quads_RW, "");
        dataService.addEndpoint(OperationName.Query, "");
        dataService.addEndpoint(OperationName.Update, "");

//        FusekiEmbeddedServer server = FusekiEmbeddedServer.create()
//                .setPort(4321)
//                .add("/data", dataService)
//                .build() ;
//        server.start() ;

        FusekiServer server = FusekiServer.create()
                .setPort(4323)
                .add("/data", dataService)
                .build() ;
        server.start() ;

        try {
            // Put data in.
            String data = "(graph (:s :p 1) (:s :p 2) (:s :p 3))" ;
            Graph g = SSE.parseGraph(data) ;
            HttpEntity e = graphToHttpEntity(g) ;
            HttpOp.execHttpPut("http://localhost:4323/data", e) ;

            // Get data out.
            try ( TypedInputStream in = HttpOp.execHttpGet("http://localhost:4323/data") ) {
                Graph g2 = GraphFactory.createDefaultGraph() ;
                RDFDataMgr.read(g2, in, RDFLanguages.contentTypeToLang(in.getContentType())) ;
                assertTrue(g.isIsomorphicWith(g2)) ;
            }
            // Query.
            query("http://localhost:4323/data", "SELECT * { ?s ?p ?o}", qExec->{
                ResultSet rs = qExec.execSelect() ;
                int x = ResultSetFormatter.consume(rs) ;
                assertEquals(3, x) ;
            }) ;
            // Update
            UpdateRequest req = UpdateFactory.create("CLEAR DEFAULT") ;
            UpdateExecutionFactory.createRemote(req, "http://localhost:4323/data").execute();
            // Query again.
            query("http://localhost:4323/data", "SELECT * { ?s ?p ?o}", qExec-> {
                ResultSet rs = qExec.execSelect() ;
                int x = ResultSetFormatter.consume(rs) ;
                assertEquals(0, x) ;
            }) ;
        } finally { server.stop() ; }
    }

    /** Create an HttpEntity for the graph */
    protected static  HttpEntity graphToHttpEntity(final Graph graph) {
        final RDFFormat syntax = RDFFormat.TURTLE_BLOCKS ;
        ContentProducer producer = new ContentProducer() {
            @Override
            public  void writeTo(OutputStream out) {
                RDFDataMgr.write(out, graph, syntax) ;
            }
        } ;
        EntityTemplate entity = new EntityTemplate(producer) ;
        ContentType ct = syntax.getLang().getContentType() ;
        entity.setContentType(ct.getContentType()) ;
        return entity ;
    }

    static DatasetGraph dataset() {
        return DatasetGraphFactory.createTxnMem() ;
    }

    static  void query(String URL, String query, Consumer<QueryExecution> body) {
        try (QueryExecution qExec = QueryExecutionFactory.sparqlService(URL, query) ) {
            body.accept(qExec);
        }
    }

}

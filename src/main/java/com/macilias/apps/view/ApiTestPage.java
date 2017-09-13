package com.macilias.apps.view;

import com.macilias.apps.model.Settings;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.UUID;

public class ApiTestPage extends WebPage {
    private static final long serialVersionUID = 1L;

    /**
     * A template for creating a nice SPARQL query
     */
    private static final String UPDATE_TEMPLATE =
            "PREFIX dc: <http://purl.org/dc/elements/1.1/>"
                    + "INSERT DATA"
                    + "{ <http://example/%s>    dc:title    \"A new book\" ;"
                    + "                         dc:creator  \"A.N.Other\" ." + "}   ";

    public ApiTestPage(final PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // Add a form with an onSubmit implementation that sets a message
        Form<Void> form = new Form<>("form");

        Button button1 = new Button("create") {
            @Override
            public void onSubmit() {
                info("button1.onSubmit executed");
                addNewBookToCollection();
            }
        };
        form.add(button1);

        Button button2 = new Button("query") {
            @Override
            public void onSubmit() {
                info("button2.onSubmit executed");
                queryTheCollection();
            }
        };
        button2.setDefaultFormProcessing(false);
        form.add(button2);

        add(form);

    }

    private void addNewBookToCollection() {
        //Add a new book to the collection
        String id = UUID.randomUUID().toString();
        System.out.println(String.format("Adding %s", id));
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(
                UpdateFactory.create(String.format(UPDATE_TEMPLATE, id)),
                "http://localhost:"+ Settings.FUSEKI_PORT+"/ds/update");
        upp.execute();
    }

    private void queryTheCollection() {
        //Query the collection, dump output
        QueryExecution qe = QueryExecutionFactory.sparqlService(
                "http://localhost:"+ Settings.FUSEKI_PORT+"/ds/query", "SELECT * WHERE {?x ?r ?y}");
        ResultSet results = qe.execSelect();
        ResultSetFormatter.out(System.out, results);
        qe.close();
    }
}

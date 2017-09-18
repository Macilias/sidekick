package com.macilias.apps.view;

import com.macilias.apps.model.History;
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
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.UUID;

public class ApiTestPage extends WebPage {
    private static final long serialVersionUID = 1L;

    @SpringBean(name = "history")
    History history;

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

        Button button1 = new Button("create1") {
            @Override
            public void onSubmit() {
                info("button1.onSubmit executed");
                addNewBookToCollection();
            }
        };
        form.add(button1);

        Button button2 = new Button("query1") {
            @Override
            public void onSubmit() {
                info("button2.onSubmit executed");
                queryTheCollection();
            }
        };
        button2.setDefaultFormProcessing(false);
        form.add(button2);

        Button button3 = new Button("create2") {
            @Override
            public void onSubmit() {
                info("button3.onSubmit executed");
                history.performWrite();
            }
        };
        form.add(button3);

        Button button4 = new Button("query2") {
            @Override
            public void onSubmit() {
                info("button4.onSubmit executed");
                history.performRead();
            }
        };
        button4.setDefaultFormProcessing(false);
        form.add(button4);

        add(form);

    }

    private void addNewBookToCollection() {
        //Add a new book to the collection
        String id = UUID.randomUUID().toString();
        System.out.println(String.format("Adding %s", id));
        UpdateProcessor upp = UpdateExecutionFactory.createRemote(
                UpdateFactory.create(String.format(UPDATE_TEMPLATE, id)),
                "http://localhost:" + Settings.FUSEKI_PORT + "/ds/update");
        upp.execute();
    }

    private void queryTheCollection() {
        //Query the collection, dump output
        QueryExecution qe = QueryExecutionFactory.sparqlService(
                "http://localhost:" + Settings.FUSEKI_PORT + "/ds/query", "SELECT * WHERE {?x ?r ?y}");
        ResultSet results = qe.execSelect();
        ResultSetFormatter.out(System.out, results);
        qe.close();
    }

}

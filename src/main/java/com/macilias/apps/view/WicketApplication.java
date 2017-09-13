package com.macilias.apps.view;

import com.macilias.apps.service.EmbeddedDb;
import org.apache.jena.fuseki.embedded.FusekiServer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Application object for your web application.
 */
public class WicketApplication extends WebApplication
{
	FusekiServer db;
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return ApiTestPage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();
		db = EmbeddedDb.getServer();
		db.start();
		// add your configuration here
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.stop();
	}
}

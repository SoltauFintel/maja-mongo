package de.mwvb.maja.mongo;

import de.mwvb.maja.web.AbstractWebApp;
import de.mwvb.maja.web.AppConfig;
import de.mwvb.maja.web.Plugin;

public class MongoPlugin implements Plugin {
	public static Database database;
	private final String dbname;
	
	public MongoPlugin(String dbname) {
		this.dbname = dbname;
	}
	
	@Override
	public void init() {
		AppConfig config = AbstractWebApp.config;
		database = new Database(config.get("dbhost", "localhost"), config.get("dbname", dbname),
				config.get("dbuser"), config.get("dbpw"));
	}

	@Override
	public void routes() {
	}
	
	@Override
	public void printInfo() {
		AppConfig config = AbstractWebApp.config;
		System.out.println("MongoDB database: " + config.get("dbname", dbname) + "@" + config.get("dbhost", "localhost")
			+ (config.get("dbpw") == null ? "" : " with password"));
	}
}

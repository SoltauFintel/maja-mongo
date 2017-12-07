package de.mwvb.maja.mongo;

import de.mwvb.maja.web.AbstractWebApp;
import de.mwvb.maja.web.AppConfig;
import de.mwvb.maja.web.Plugin;

public class MongoPlugin implements Plugin {
	public static Database database;
	private final String dbname;
	private final Class<?> entityClasses[];
	private String info;
	
	public MongoPlugin(String dbname, Class<?> ... entityClasses) {
		this.dbname = dbname;
		this.entityClasses = entityClasses;
	}
	
	@Override
	public void init() {
		AppConfig config = AbstractWebApp.config;
		String host = config.get("dbhost", "localhost");
		String databaseName = config.get("dbname", dbname);
		String user = config.get("dbuser");
		String password = config.get("dbpw");
		database = new Database(host, databaseName, user, password, entityClasses);
		info = "MongoDB database: " + databaseName + "@" + host;
		if (password != null) {
			info += " with password";
		}
	}

	@Override
	public void routes() {
	}
	
	@Override
	public void printInfo() {
		System.out.println(info);
	}
}

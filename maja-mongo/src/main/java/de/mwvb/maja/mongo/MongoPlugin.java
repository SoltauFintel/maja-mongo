package de.mwvb.maja.mongo;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provides;

import de.mwvb.maja.web.AppConfig;
import de.mwvb.maja.web.Plugin;

public class MongoPlugin implements Plugin {
	private final Class<?> entityClasses[];
	private String info;
	
	public MongoPlugin(Class<?> ... entityClasses) {
		this.entityClasses = entityClasses;
	}
	
	@Override
	public Module getModule() {
		return new AbstractModule() {
			private Database database; // Singleton
			
			@Override
			protected void configure() {
			}
			
			@Provides
			@Inject
			public Database database(AppConfig config) {
				if (database == null) {
					String host = config.get("dbhost", "localhost");
					String databaseName = config.get("dbname");
					if (databaseName == null || databaseName.trim().isEmpty()) {
						throw new RuntimeException("dbname missing in AppConfig.properties");
					}
					String user = config.get("dbuser");
					String password = config.get("dbpw");
					database = new Database(host, databaseName, user, password, entityClasses);
					info = "MongoDB database: " + (user == null ? "" : (user + (password == null ? "" : "/***") + "@"))
							+ databaseName + ":" + host;
				}
				return database;
			}
		};
	}
	
	@Override
	public void init() {
	}

	@Override
	public void routes() {
	}
	
	@Override
	public void printInfo() {
		System.out.println(info);
	}
}

package de.mwvb.maja.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pmw.tinylog.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provides;

import de.mwvb.maja.web.AppConfig;
import de.mwvb.maja.web.BroadcastListener;
import de.mwvb.maja.web.Broadcaster;
import de.mwvb.maja.web.Plugin;

public class MongoPlugin implements Plugin, BroadcastListener {
	public static final String ENTITY_CLASS = "entityClass";
	private final List<Class<?>> entityClasses;
	private String info;
	@Inject
	private Broadcaster broadcaster;
	
	public MongoPlugin(Class<?> ... entityClasses) {
		this.entityClasses = new ArrayList<>(Arrays.asList(entityClasses));
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
			public Database provideDatabase(AppConfig config) {
				if (database == null) {
					String dbhost = config.get("dbhost", "localhost");
					String dbname = config.get("dbname");
					if (dbname == null || dbname.trim().isEmpty()) {
						throw new RuntimeException("Parameter 'dbname' missing in config.");
					}
					Logger.trace("open database " + dbname);					
					String dbuser = config.get("dbuser"); // optional parameter
					String dbpw = config.get("dbpw");     // optional parameter
					database = new Database(dbhost, dbname, dbuser, dbpw, entityClasses);
					info = "MongoDB database: " + (dbuser == null ? "" : (dbuser + (dbpw == null ? "" : "/***") + "@"))
							+ dbname + ":" + dbhost;
				}
				return database;
			}
		};
	}
	
	@Override
	public void prepare() {
		broadcaster.addListener(this);
	}

	@Override
	public void install() {
	}

	@Override
	public void routes() {
	}
	
	@Override
	public void printInfo() {
		System.out.println(info);
	}

	@Override
	public void handle(String topic, String data) {
		if (ENTITY_CLASS.equals(topic) && data != null && !data.trim().isEmpty()) {
			try {
System.out.println("add entityClass: " + data);				
				entityClasses.add(Class.forName(data));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

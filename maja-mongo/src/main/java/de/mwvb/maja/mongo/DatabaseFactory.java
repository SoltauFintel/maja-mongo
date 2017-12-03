package de.mwvb.maja.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.pmw.tinylog.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.mwvb.maja.web.AppConfig;
import de.mwvb.maja.web.BroadcastListener;

@Singleton
public class DatabaseFactory implements BroadcastListener {
	public static final String ENTITY_CLASS = "entityClass";
	private final List<Class<?>> entityClasses = new ArrayList<>();
	private Database database;
	private String info;
	@Inject
	private AppConfig config;
	
	public Database getDatabase() {
		if (database == null) {
			String dbhost = config.get("dbhost", "localhost");
			String dbname = config.get("dbname");
			if (dbname == null || dbname.trim().isEmpty()) {
				throw new RuntimeException("Parameter 'dbname' missing in config.");
			}
			Logger.trace("open database " + dbname + " with " + entityClasses.size() + " entity classes");					
			String dbuser = config.get("dbuser"); // optional parameter
			String dbpw = config.get("dbpw");     // optional parameter
			database = new Database(dbhost, dbname, dbuser, dbpw, entityClasses);
			info = "MongoDB database: " + (dbuser == null ? "" : (dbuser + (dbpw == null ? "" : "/***") + "@"))
					+ dbname + ":" + dbhost;
		}
		return database;
	}
	
	public Datastore ds() {
		return getDatabase().ds();
	}

	@Override
	public void handle(String topic, String data) {
		if (ENTITY_CLASS.equals(topic) && data != null && !data.trim().isEmpty()) {
			try {
				entityClasses.add(Class.forName(data));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void addAll(Class<?>[] pEntityClasses) {
		entityClasses.addAll(Arrays.asList(pEntityClasses));
	}
	
	@Override
	public String toString() {
		if (info == null) {
			getDatabase();
		}
		return info;
	}
}

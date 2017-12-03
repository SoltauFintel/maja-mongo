package de.mwvb.maja.mongo;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.logging.MorphiaLoggerFactory;
import org.mongodb.morphia.logging.slf4j.SLF4JLoggerImplFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import de.mwvb.maja.web.AppConfig;

/**
 * Zugriff auf MongoDB
 */
public class Database {
	private MongoClient client;
	private Morphia morphia;
	private Datastore ds;
	private final String info;
	
	static {
		MorphiaLoggerFactory.registerLogger(SLF4JLoggerImplFactory.class);
	}
	
	public static Database open(String dbname, AppConfig config, List<Class<?>> entityClasses) {
		String host = config.get("dbhost", "localhost");
		String databaseName = config.get("dbname", dbname);
		String user = config.get("dbuser");
		String password = config.get("dbpw");
		return new Database(host, databaseName, user, password, entityClasses);
	}
	
	/**
	 * Öffnet Datenbank.
	 * 
	 * @param dbhost z.B. "localhost"
	 * @param name Name der MongoDB Datenbank
	 * @param user Datenbank Benutzername, null or leer wenn nicht erforderlich
	 * @param password Kennwort des Datenbank Benutzers, null or leer wenn nicht erforderlich
	 * @param entityClasses Für jedes Package eine Klasse, damit das Package registriert wird.
	 * Es muss also NICHT jede Entity Klasse angegeben werden!
	 */
	public Database(String dbhost, String name, String user, String password, List<Class<?>> entityClasses) {
		List<MongoCredential> credentialsList = new ArrayList<>();
		if (user != null && !user.isEmpty()) {
			MongoCredential cred = MongoCredential.createCredential(user, name, password.toCharArray());
			credentialsList.add(cred);
		}
		client = new MongoClient(new ServerAddress(dbhost), credentialsList);
		morphia = new Morphia();
		ds = morphia.createDatastore(client, name);
		entityClasses.forEach(entityClass -> morphia.mapPackageFromClass(entityClass));
		ds.ensureIndexes();
		info = "MongoDB database: " + user + (password == null ? "" : "/***") + "@" + name + ":" + dbhost;
	}
	
	/**
	 * @return Host/Name/User of database
	 */
	@Override
	public String toString() {
		return info;
	}

	public Datastore ds() {
		return ds;
	}
	
	public void close() {
		ds = null;
		morphia = null;
		client.close();
		client = null;
	}
}

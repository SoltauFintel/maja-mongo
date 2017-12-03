package de.mwvb.maja.mongo;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;

import de.mwvb.maja.web.Broadcaster;
import de.mwvb.maja.web.Plugin;

public class MongoPlugin implements Plugin {
	private final Class<?> entityClasses[];
	@Inject
	private Broadcaster broadcaster;
	@Inject
	private DatabaseFactory databaseFactory;
	
	public MongoPlugin(Class<?> ... entityClasses) {
		this.entityClasses = entityClasses;
	}
	
	@Override
	public Module getModule() {
		return new AbstractModule() {
			@Override
			protected void configure() {
				bind(DatabaseFactory.class);
			}
		};
	}
	
	@Override
	public void prepare() {
		broadcaster.addListener(databaseFactory);
		databaseFactory.addAll(entityClasses);
	}

	@Override
	public void install() {
	}

	@Override
	public void routes() {
	}
	
	@Override
	public void printInfo() {
		System.out.println(databaseFactory.toString());
	}
}

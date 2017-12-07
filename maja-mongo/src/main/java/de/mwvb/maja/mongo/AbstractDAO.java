package de.mwvb.maja.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.CRC32;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.google.inject.Inject;

public abstract class AbstractDAO<E> {
	@Inject
	private Database database;
	
	/**
	 * @return entity class
	 */
	protected abstract Class<E> getCls();
	
	public void save(E entity) {
		ds().save(entity);
	}

	public void delete(E entity) {
		ds().delete(entity);
	}
	
	/**
	 * @return all entities of the collection
	 */
	public List<E> list() {
		return createQuery().asList();
	}

	/**
	 * Find by id
	 * 
	 * @param id String
	 * @return null if not exists
	 */
	public E get(String id) {
		return createQuery().field("id").equal(id).get();
	}

	/**
	 * @return number of entities in collection
	 */
	public long size() {
		return createQuery().count();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> distinct(String fieldname) {
		return new ArrayList<String>(ds().getCollection(getCls()).distinct(fieldname));
	}
	
	protected final Query<E> createQuery() {
		return ds().createQuery(getCls());
	}

	protected final Datastore ds() {
		return database.ds();
	}

	public static String genId() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static String code6(String str) {
		CRC32 crc = new CRC32();
		crc.update(str.getBytes());
		String ret = "000000" + Integer.toString((int) crc.getValue(), 36).toLowerCase().replace("-", "");
		return ret.substring(ret.length() - 6);
	}
}

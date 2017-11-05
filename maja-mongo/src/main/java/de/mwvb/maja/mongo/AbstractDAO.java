package de.mwvb.maja.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.CRC32;

import org.mongodb.morphia.Datastore;

public abstract class AbstractDAO<E> {
	protected final Datastore ds;
	protected final Class<E> cls;
	
	public AbstractDAO(Database database, Class<E> cls) {
		this.ds = database.ds();
		this.cls = cls;
	}

	public void save(E entity) {
		ds.save(entity);
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
	
	public long size() {
		return ds.createQuery(cls).count();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> distinct(String fieldname) {
		return new ArrayList<String>(ds.getCollection(cls).distinct(fieldname));
	}
	
	public void delete(E entity) {
		ds.delete(entity);
	}
}

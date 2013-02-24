package com.xbockr.dr.repository.meta;

import java.io.File;
import java.util.List;

import com.xbockr.dr.repository.meta.db.Database;
import com.xbockr.dr.repository.meta.db.Database.DBException;
import com.xbockr.dr.repository.meta.db.Database.NotFoundException;
import com.xbockr.dr.repository.meta.db.IDatabase;

public class MetaEngine implements IMetaEngine {

	private File path;
	private IDatabase database;

	public MetaEngine(File path) {
		super();
		this.path = path;
		this.database = new Database(path);
	}

	@Override
	public File getPath() {
		return path;
	}

	@Override
	public boolean exists(MetaInfo metaInfo) {
		return this.exists(metaInfo.getName());
	}

	@Override
	public MetaInfo getMetaInfo(String name) throws DBException, NotFoundException {
		return database.getMetaInfo(name);
	}

	@Override
	public void add(MetaInfo metaInfo) throws DBException {
		String statement = "insert into MetaInfo(name,description,originalName,timestamp,numberOfFiles,size) values('%s','%s','%s',%d,%d,%d)";
		statement = String.format(statement, metaInfo.getName(),
				metaInfo.getDescription(), metaInfo.getOriginalName(),
				metaInfo.getTimestamp(), metaInfo.getNumberOfFiles(),
				metaInfo.getSize());
		database.executeUpdate(statement);
	}

	@Override
	public void replace(String name, MetaInfo metaInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<MetaInfo> list() {
		try {
			return database.list();
		} catch (DBException e) {
			throw new RuntimeException("Unable to read database");
		}
	}

	@Override
	public void delete(String name) throws DBException {
		String statementString = String.format("delete from MetaInfo where name = '%s'",name);
		this.database.executeUpdate(statementString);
	}

	@Override
	public boolean exists(String name) {
		String statement = "select * from MetaInfo where name = '%s'";
		statement = String.format(statement, name);
		try {
			return database.exists(statement);
		} catch (DBException e) {
			throw new RuntimeException("Unable to read database");
		}
	}

}

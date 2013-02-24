package com.xbockr.dr.repository.meta;

import java.io.File;
import java.util.List;

import com.xbockr.dr.repository.meta.db.Database.DBException;
import com.xbockr.dr.repository.meta.db.Database.NotFoundException;

public interface IMetaEngine {
	File getPath();
	
	boolean exists(MetaInfo metaInfo);
	boolean exists(String name);
	MetaInfo getMetaInfo(String name) throws DBException, NotFoundException;
	void add(MetaInfo metaInfo) throws DBException;
	void replace(String name, MetaInfo metaInfo);
	List<MetaInfo> list();
	void delete(String name) throws DBException;
}

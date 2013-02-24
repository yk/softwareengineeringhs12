package com.xbockr.dr.repository.meta.db;

import java.io.File;
import java.util.List;

import com.xbockr.dr.repository.meta.MetaInfo;
import com.xbockr.dr.repository.meta.db.Database.DBException;
import com.xbockr.dr.repository.meta.db.Database.NotFoundException;

/**
 * @author yk
 *	low level wrapper around database
 */
public interface IDatabase {
	String DBFILENAME = ".db";
	File getPath();
	//TODO
	void executeStatement(String statementString) throws DBException;
	void executeUpdate(String statementString) throws DBException;
	boolean exists(String statement) throws DBException;
	MetaInfo getMetaInfo(String name) throws DBException, NotFoundException;
	List<MetaInfo> list() throws DBException;
}

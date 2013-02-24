package com.xbockr.dr.repository;

import java.io.File;
import java.util.List;
import java.util.Properties;

import com.xbockr.dr.repository.meta.MetaInfo;

/**
 * @author yk
 *	Contains a FileSystem Object for doing its operations.
 */
public interface IRepository {
	String HIDDENDIRECTORYNAME = ".xbockr";
	void add(MetaInfo metaInfo, File path,boolean move);
	void export(String name, File path);
	List<MetaInfo> list();
	void delete(String name);
	
	boolean exists(MetaInfo metaInfo);
	String getUniqueName(String originalName);
	
	File getPath(); //Path of the repository from where all operations operate
	void replace(String dataSetToReplace, File dataSetToAdd,
			String description, boolean move);
	void addWithinReplace(MetaInfo metaInfo, File path, boolean move);
	void server(File incomingDirectory,File htmlOverview, File logFile, long checkingIntervalInSeconds, String completenessDetectionClassName, Properties properties);
}

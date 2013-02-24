package com.xbockr.dr.control;

import java.io.File;

/**
 * @author yk
 *	instantiates an IRepository and uses it to execute the desired command
 */
public interface IControl {
	void executeAdd(File repositoryPath, File dataSet, String name, String description, boolean move);
	void executeDelete(File repositoryPath,String name);
	void executeExport(File repositoryPath, String dataSetName, File destinationPath);
	void executeList(File repositoryPath, boolean pretty);
	void executeReplace(File repositoryPath, String dataSetToRelace,
			File dataSetToAdd, String description, boolean move);
	void executeHelp();
}

package com.xbockr.dr.filesystem;

import java.io.File;

import com.xbockr.dr.filesystem.FileSystem.FSException;

public interface IFileSystem {
	Long getNumberOfFiles(File path);
	Long getSize(File path);
	void move(File from, File to) throws FSException;
	void copy(File from, File to) throws FSException;
	void delete(File path);
	//maybe a mkdir command?
	File getPath();
	String getOriginalName(File path);
	}

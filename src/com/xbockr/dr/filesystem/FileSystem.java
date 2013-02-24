package com.xbockr.dr.filesystem;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FileSystem implements IFileSystem {
	public static class FSException extends Exception {
		private static final long serialVersionUID = 1241805373592363217L;
		private IOException ex;

		public FSException(String message, IOException ex) {
			super(message);
			this.ex = ex;
		}

		public IOException getCause() {
			return this.ex;
		}
	}

	private File path;

	public FileSystem(File path) {
		super();
		this.path = path;
	}

	@Override
	public Long getNumberOfFiles(File path) {
		if (path.isFile()) {
			return 1L;
		}
		// directory
		long l = FileUtils.listFiles(path, null, true).size();
		return l;
	}

	@Override
	public Long getSize(File path) {
		if (path.isFile()) {
			return FileUtils.sizeOf(path);
		}
		// directory
		return FileUtils.sizeOfDirectory(path);
	}

	@Override
	public String getOriginalName(File path) {
		return path.getName();
	}

	@Override
	public void move(File from, File to) throws FSException {
		try {
			FileUtils.moveToDirectory(from, to, true);
		} catch (IOException e) {
			throw new FSException("Could not move", e);
		}
	}

	@Override
	public void copy(File from, File to) throws FSException {
		try {
			to.mkdirs();
			if (from.isFile()) {
				FileUtils.copyFileToDirectory(from, to);
			} else {
				FileUtils.copyDirectoryToDirectory(from, to);
			}
		} catch (IOException e) {
			throw new FSException("Could not copy", e);
		}
	}

	public void delete(File file) {
		FileUtils.deleteQuietly(file);
	}

	@Override
	public File getPath() {
		return path;
	}

}

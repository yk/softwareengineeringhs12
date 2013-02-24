package com.xbockr.dr.repository;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.xbockr.dr.filesystem.FileSystem;
import com.xbockr.dr.filesystem.FileSystem.FSException;
import com.xbockr.dr.filesystem.IFileSystem;
import com.xbockr.dr.repository.meta.IMetaEngine;
import com.xbockr.dr.repository.meta.MetaEngine;
import com.xbockr.dr.repository.meta.MetaInfo;
import com.xbockr.dr.repository.meta.db.Database.DBException;
import com.xbockr.dr.repository.meta.db.Database.NotFoundException;
import com.xbockr.dr.server.ICompletenessDetection;
import com.xbockr.dr.server.RepositoryServer;
import com.xbockr.dr.util.OutHelper;

public class Repository implements IRepository {
	private File path;
	private IMetaEngine metaEngine;
	private IFileSystem fileSystem;

	public Repository(File path, boolean createOnDemand) {
		super();
		this.path = path;
		File hidden = new File(path, HIDDENDIRECTORYNAME);
		if (!createOnDemand && !hidden.exists()) {
			throw new RuntimeException("Repository Invalid");
		}
		if (createOnDemand) {
			if (!hidden.exists() && path.exists()
					&& (!path.isDirectory() || path.list().length > 0)) {
				throw new RuntimeException(
						"Repository must be a nonexistent or empty folder");
			}
			File d = new File(path.getAbsolutePath()).getParentFile();
			while (d != null) {
				try {
					if (d.exists()
							&& FileUtils.directoryContains(d, new File(d,
									HIDDENDIRECTORYNAME))) {
						throw new RuntimeException(
								"Repository path must not be within another repository");
					}
				} catch (IOException e) {
					throw new RuntimeException(
							"Error while validating repository path");
				}
				d = d.getParentFile();
			}
			if (!path.exists()) {
				path.mkdirs();
			}
			if (!hidden.exists()) {
				hidden.mkdirs();
			}
		}
		this.metaEngine = new MetaEngine(path);
		this.fileSystem = new FileSystem(path);
	}

	@Override
	public void export(String name, File path) {
		if (!path.exists()) {
			path.mkdirs();
		}
		if (!path.isDirectory()) {
			throw new RuntimeException("Destination must be a directory");
		}
		try {
			MetaInfo metaInfo = this.metaEngine.getMetaInfo(name);
			File pathInRep = new File(new File(this.path, metaInfo.getName()),
					metaInfo.getOriginalName());
			this.fileSystem.copy(pathInRep, path);
		} catch (DBException e) {
			throw new RuntimeException("Database error.");
		} catch (NotFoundException e) {
			throw new RuntimeException(e.getMessage());
		} catch (FSException e) {
			throw new RuntimeException("Unable to export Data Set.");
		}
		
		OutHelper.out("exported " + name + " to " + path.getAbsolutePath());

	}

	@Override
	public List<MetaInfo> list() {
		return this.metaEngine.list();
	}

	@Override
	public void delete(String name) {
		try {
			MetaInfo metaInfo = this.metaEngine.getMetaInfo(name);
			File pathIntoRep = new File(this.path, metaInfo.getName());
			this.metaEngine.delete(metaInfo.getName());
			this.fileSystem.delete(pathIntoRep);
		} catch (DBException e) {
			throw new RuntimeException("Database error. Failed to delete");
		} catch (NotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
		OutHelper.out("deleted " + name + " from repository");
	}

	@Override
	public File getPath() {
		return path;
	}

	@Override
	public void add(MetaInfo metaInfo, File path, boolean move) {
		if (this.metaEngine.exists(metaInfo)) {
			throw new RuntimeException(
					"Data set name already exists in the repository, use replace");
		}
		if (!path.exists()) {
			throw new RuntimeException("File path for data set does not exist");
		}
		// populate metainfo
		metaInfo.setNumberOfFiles(fileSystem.getNumberOfFiles(path));
		metaInfo.setOriginalName(fileSystem.getOriginalName(path));
		metaInfo.setSize(fileSystem.getSize(path));
		File dataSetInRep = new File(this.path, metaInfo.getName());
		try {
			dataSetInRep.mkdirs();
			if (move) {
				fileSystem.move(path, dataSetInRep);
			} else {
				fileSystem.copy(path, dataSetInRep);
			}
		} catch (FSException e) {
			throw new RuntimeException("Unable to move or copy files");
		}
		try {
			metaEngine.add(metaInfo);
		} catch (DBException e) {
			if (move) {
				// must reverse file system operation
				try {
					fileSystem.move(new File(dataSetInRep.list()[0]),
							path.getParentFile());
				} catch (FSException e1) {
					throw new RuntimeException(
							"Fatal error, repository state might be corrupted");
				}
			}
			throw new RuntimeException(
					"Unable to add meta information to the repository");
		}
		OutHelper.out("successfully added " + metaInfo.getOriginalName() + " to repository");
	}

	@Override
	public void addWithinReplace(MetaInfo metaInfo, File path, boolean move) {
		if (!path.exists()) {
			throw new RuntimeException("File path for data set does not exist");
		}
		// populate metainfo
		metaInfo.setNumberOfFiles(fileSystem.getNumberOfFiles(path));
		metaInfo.setOriginalName(fileSystem.getOriginalName(path));
		metaInfo.setSize(fileSystem.getSize(path));
		File dataSetInRep = new File(this.path, metaInfo.getName());
		try {
			dataSetInRep.mkdirs();
			if (move) {
				fileSystem.move(path, dataSetInRep);
			} else {
				fileSystem.copy(path, dataSetInRep);
			}
		} catch (FSException e) {
			throw new RuntimeException("Unable to move or copy files");
		}
		try {
			metaEngine.add(metaInfo);
		} catch (DBException e) {
			if (move) {
				// must reverse file system operation
				try {
					fileSystem.move(new File(dataSetInRep.list()[0]),
							path.getParentFile());
				} catch (FSException e1) {
					throw new RuntimeException(
							"Fatal error, repository state might be corrupted");
				}
			}
			throw new RuntimeException(
					"Unable to add meta information to the repository");
		}
	}

	@Override
	public void replace(String dataSetToReplace, File dataSetToAdd,
			String description, boolean move) {
		MetaInfo newMetaInfo;
		if (description.equals("")) {
			MetaInfo metaInfo;
			try {
				metaInfo = getMetaEngine().getMetaInfo(dataSetToReplace);
			} catch (DBException e) {
				throw new RuntimeException("Database error");
			} catch (NotFoundException e) {
				throw new RuntimeException(
						"File path for data set to be replaced does not exist");
			}
			String oldDescription = metaInfo.getDescription();
			// not exactly the same: new time stamp etc. for replacing file
			newMetaInfo = MetaInfo.createFromAdd(dataSetToReplace,
					oldDescription);
		} else {
			newMetaInfo = MetaInfo.createFromAdd(dataSetToReplace, description);
		}

		// delete old file
		delete(dataSetToReplace);
		// add new file
		addWithinReplace(newMetaInfo, dataSetToAdd, move);
		OutHelper.out("replaced " + dataSetToReplace + " with " + dataSetToAdd.getName());
	}

	IMetaEngine getMetaEngine() {
		return this.metaEngine;
	}

	IFileSystem getFileSystem() {
		return this.fileSystem;
	}

	@Override
	public boolean exists(MetaInfo metaInfo) {
		return this.metaEngine.exists(metaInfo);
	}

	@Override
	public String getUniqueName(String originalName) {
		String name = originalName;
		name = name.toUpperCase();
		name = name.replaceAll("[^A-Z0-9_\\-]", "");
		String uname = "";
		long l = 0L;
		boolean unique = false;
		while (!unique) {
			String ls = "_" + l++;
			int len = Math.min(name.length(), 40 - ls.length());
			uname = name.substring(0, len) + ls;
			unique = !this.metaEngine.exists(uname);
		}
		return uname;
	}

	@Override
	public void server(File incomingDirectory,File htmlOverview, File logFile,
			long checkingIntervalInSeconds,
			String completenessDetectionClassName, Properties properties) {
		// all properties already checked!!!
		// now check: htmlOverview == null -> no overview will be created
		Class<?> cdc = null;
		try {
			cdc = Class.forName(completenessDetectionClassName);
			if (!Arrays.asList(cdc.getInterfaces()).contains(
					ICompletenessDetection.class)) {
				throw new RuntimeException(
						"Invalid completeness detection class.");
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Completeness detection class not found.");
		}

		RepositoryServer repServ = new RepositoryServer(incomingDirectory, htmlOverview, logFile,
				checkingIntervalInSeconds, cdc.asSubclass(ICompletenessDetection.class),
				properties);
		repServ.runServer(this);
	}

}

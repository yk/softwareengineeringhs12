package com.xbockr.dr.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;

import com.xbockr.dr.gui.RepositoryGUI;
import com.xbockr.dr.repository.IRepository;
import com.xbockr.dr.repository.Repository;
import com.xbockr.dr.repository.meta.MetaInfo;
import com.xbockr.dr.util.HelpMsg;
import com.xbockr.dr.util.MetaInfoListPrinter;

public class Control implements IControl {

	@Override
	public void executeAdd(File repositoryPath, File dataSet, String name,
			String description, boolean move) {
		// create repository
		IRepository rep = new Repository(repositoryPath, true);
		// create metainfo
		MetaInfo metaInfo = MetaInfo.createFromAdd(name, description);
		if (name.equals("")) {
			metaInfo.setName(rep.getUniqueName(dataSet.getName()));
		}
		rep.add(metaInfo, dataSet, move);
	}

	@Override
	public void executeDelete(File repositoryPath, String name) {
		IRepository rep = new Repository(repositoryPath, false);
		rep.delete(name);
	}

	@Override
	public void executeExport(File repositoryPath, String dataSetName,
			File destinationPath) {
		IRepository rep = new Repository(repositoryPath, false);
		rep.export(dataSetName, destinationPath);
	}

	@Override
	public void executeList(File repositoryPath, boolean pretty) {
		IRepository rep = new Repository(repositoryPath, false);
		List<MetaInfo> metaInfos = rep.list();
		new MetaInfoListPrinter(metaInfos).print(pretty);
	}

	@Override
	public void executeReplace(File repositoryPath, String dataSetToReplace,
			File dataSetToAdd, String description, boolean move) {
		IRepository rep = new Repository(repositoryPath, false);
		rep.replace(dataSetToReplace, dataSetToAdd, description, move);
	}

	@Override
	public void executeHelp() {
		String hm = HelpMsg.getHelpMsg();
		System.out.println(hm);

	}

	public void executeServer(File repositoryPath, File propertiesFile) {
		IRepository rep = new Repository(repositoryPath, true);
		Properties properties = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(propertiesFile);
			properties.load(in);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"Error starting server. Property file not found.");
		} catch (IOException e) {
			throw new RuntimeException(
					"Error starting server. Error reading property file.");
		} finally {
			IOUtils.closeQuietly(in);
		}

		// check if properties exist and valid

		String incomDir = properties.getProperty("incoming-directory");
		// check if incoming-directory is specified
		if (incomDir == null) {
			throw new RuntimeException(
					"Error starting server. Incoming-directory not specified.");
		}
		File incomingDirectory = new File(incomDir);
		// check if specified incoming-directory exists
		if (!incomingDirectory.exists()) {
			throw new RuntimeException(
					"Error starting server. Specified incoming-directory does not exist.");
		}

		File htmlOverview = null; //file being null means should not create overview
		String htmlOverviewString = properties.getProperty("html-overview");
		if(htmlOverviewString != null){
			htmlOverview = new File(htmlOverviewString);
		}

		String log = properties.getProperty("log-file");
		File logFile = null;
		// check if log-file specified:
		if (log == null) {
			// !!! IMPORTANT AND WORKS: 
			// if not specified, it will be server.log inside the repository folder
//			log = "server.log";
//			String logFilePath = repositoryPath.toString();
//			logFilePath = logFilePath + "/" + log;
			logFile = new File(repositoryPath,"server.log");
		} else {
			logFile = new File(log);
		}

		String intervals = properties
				.getProperty("checking-interval-in-seconds");
		// check if checking-interval-in-seconds specified:
		if (intervals == null) {
			throw new RuntimeException(
					"Error starting server. Checking-interval-in-seconds not specified.");
		}
		long checkingIntervalInSeconds = Long.parseLong(intervals);

		String completenessDetectionClassName = properties
				.getProperty("completeness-detection.class-name");
		// check if completeness-detection.class-name specified:
		if (completenessDetectionClassName == null) {
			throw new RuntimeException(
					"Error staring server. Completeness-detection.class-name not specified.");
		}

		rep.server(incomingDirectory,htmlOverview, logFile,
				checkingIntervalInSeconds, completenessDetectionClassName,
				properties);
	}
	
	public void executeGUI(){
		/* Create and display the form */
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	            //new GUI();
	        	new RepositoryGUI().setVisible(true);
	        }
	    });
	}
	
	
}

package com.xbockr.dr.server;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.xbockr.dr.repository.IRepository;
import com.xbockr.dr.repository.meta.MetaInfo;
import com.xbockr.dr.util.MetaInfoListHtmlPrinter;
import com.xbockr.dr.util.OutHelper;

public class RepositoryServer {
	private File incomingDirectory;
	private File htmlOverview;
	private File logFile;
	private long checkingIntervalInSeconds;
	private Class<? extends ICompletenessDetection> completenessDetectionClass;
	private Properties properties;

	public RepositoryServer(File incomingDirectory, File htmlOverview,
			File logFile, long checkingIntervalInSeconds,
			Class<? extends ICompletenessDetection> completenessDetectionClass,
			Properties properties) {
		super();
		this.incomingDirectory = incomingDirectory;
		this.htmlOverview = htmlOverview;
		this.logFile = logFile;
		this.checkingIntervalInSeconds = checkingIntervalInSeconds;
		this.completenessDetectionClass = completenessDetectionClass;
		this.properties = properties;
	}

	public void runServer(IRepository repository) {
		ICompletenessDetection completenessDetection = null;
		try {
			completenessDetection = completenessDetectionClass.newInstance();
			completenessDetection.init(this.properties);
		} catch (InstantiationException e) {
			throw new RuntimeException(
					"Unable to instantiate Completetness Detection");
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"Unable to instantiate Completetness Detection");
		}
		// PrintStream oldOut = System.out;
		// System.out.println("Successfully started server.");
		// final Logger log = Logger.getLogger("com.xbockr.dr");
		//
		// log.setLevel(Level.INFO);
		// log.setUseParentHandlers(false);
		try {
			OutHelper.toStdOut(false);
			OutHelper.toFile(true, logFile);
			// FileHandler fh = new FileHandler(logFile.getAbsolutePath(),
			// true);
			// fh.setFormatter(new Formatter() {
			// @Override
			// public String format(LogRecord arg0) {
			// String timestamp = new MessageFormat(
			// "{0,date,yyyy-MM-dd HH:mm:ss}")
			// .format(new Object[] { new Date().getTime() });
			// Level logLevel = arg0.getLevel();
			// String logLevelString = "[nolevel]";
			// if (logLevel.equals(Level.INFO)) {
			// logLevelString = "[INFO]";
			// } else if (logLevel.equals(Level.SEVERE)) {
			// logLevelString = "[ERROR]";
			// }
			// String msg = arg0.getMessage();
			// return timestamp + " " + logLevelString + " " + msg;
			// }
			// });
			// log.addHandler(fh);
			// System.setOut(new PrintStream(new ByteArrayOutputStream() {
			// @Override
			// public void flush() throws IOException {
			// String record;
			// synchronized (this) {
			// super.flush();
			// record = this.toString();
			// super.reset();
			//
			// log.log(Level.INFO, record);
			//
			// }
			// }
			// }));
			// System.setErr(new PrintStream(new ByteArrayOutputStream() {
			// @Override
			// public void flush() throws IOException {
			// String record;
			// synchronized (this) {
			// super.flush();
			// record = this.toString();
			// super.reset();
			//
			// log.log(Level.SEVERE, record);
			//
			// }
			// }
			// }));
		} catch (SecurityException e1) {
			throw new RuntimeException("Unable to open log file");
		} catch (IOException e1) {
			throw new RuntimeException("Unable to open log file");
		}
		System.out.println("Successfully started server.");
		OutHelper.out("data-repository version 0.6");
		for (Object name : properties.keySet()) {
			OutHelper.out(name.toString() + ": "
					+ properties.get(name).toString());
		}
		// System.out.flush();
		// log.log(Level.SEVERE,"hello2");

		// if htmlOverview specified start server with overview
		try {
			printHTML(repository);
		} catch (IOException e1) {
			throw new RuntimeException("Unable to create HTML overview");
		}
		while (true) {
			String[] files = incomingDirectory.list();
			boolean hasNew = false;
			for (String s : files) {
				File f = new File(incomingDirectory, s);
				if (f.equals(incomingDirectory)) {
					continue;
				}
				if (completenessDetection.isComplete(f)) {
					String name = repository.getUniqueName(f.getName());
					repository.add(MetaInfo.createFromAdd(name, ""), f, true);
					hasNew = true; // if we call printHTML here it might get
									// created for every new dataset; if we call
									// it below it is only done once per import
									// batch
				}
			}
			if (hasNew) {
				try {
					printHTML(repository);
				} catch (IOException e) {
					throw new RuntimeException("Unable to create HTML overview");
				}
			}
			try {
				Thread.sleep(checkingIntervalInSeconds * 1000);
			} catch (InterruptedException e) {
				OutHelper.out("Ending Server.");
				return;
			}
		}
	}

	private void printHTML(IRepository repository) throws IOException {
		if (htmlOverview != null) {
			new MetaInfoListHtmlPrinter(htmlOverview, repository.list())
					.print();
		}
	}
}

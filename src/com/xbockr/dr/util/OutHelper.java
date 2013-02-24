package com.xbockr.dr.util;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

public class OutHelper {
	private static class CustomLevel extends Level {
		private static final long serialVersionUID = -8169708863460101466L;

		private CustomLevel(String name, int value) {
			super(name, value);
		}
	}

	private static final Level ERROR = new CustomLevel("ERROR",
			Level.SEVERE.intValue() + 1);

	private static final Logger logger = Logger
			.getLogger("com.xbockr.dr.usrmsg");

	static {
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.INFO);
		removeAllHandlers();
	}

	private static boolean toStdOut = true;
	private static boolean toGUI = false;

	public static void toStdOut(boolean b) {
		toStdOut = b;
	}

	public static void toGUI(boolean b) {
		toGUI = b;
	}

	public static void toFile(boolean b, File f) throws SecurityException,
			IOException {
		if (!b) {
			removeAllHandlers();
		} else {
			FileHandler fh = new FileHandler(f.getAbsolutePath(), true);
			fh.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord arg0) {
					String timestamp = new MessageFormat(
							"{0,date,yyyy-MM-dd HH:mm:ss}")
							.format(new Object[] { new Date().getTime() });
					Level logLevel = arg0.getLevel();
					String logLevelString = "[" + logLevel.getName() + "]";
					String msg = arg0.getMessage();
					return timestamp + " " + logLevelString + " " + msg + "\n";
				}
			});
			logger.addHandler(fh);
		}
	}

	private static void removeAllHandlers() {
		for (Handler h : logger.getHandlers()) {
			logger.removeHandler(h);
		}
	}

	public static void out(String s) {
		if (toStdOut) {
			System.out.println(s);
		}
		if (toGUI) {
			JOptionPane.showMessageDialog(null, s);
		}
		logger.log(Level.INFO, s);
	}

	public static void error(String s) {
		if (toStdOut) {
			System.err.println(s);
		}
		if (toGUI) {
			JOptionPane.showMessageDialog(null, s, "Error", JOptionPane.ERROR_MESSAGE);
		}
		logger.log(ERROR, s);

	}

}

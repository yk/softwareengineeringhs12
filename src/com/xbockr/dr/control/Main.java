package com.xbockr.dr.control;

import gnu.getopt.Getopt;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.xbockr.dr.util.OutHelper;

/**
 * @author yk, markoni
 * 
 */

public class Main {
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface ShellCallable {

	}

	@ShellCallable
	private void add(String[] args) throws IOException {
		Getopt g = new Getopt("data-repository", args, "d:mn:");
		int c;
		String arg;
		String description = "";
		String name = "";
		boolean move = false;
		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 'd':
				arg = g.getOptarg();
//				checkArgOf_d(arg);
				description = arg;
				break;
			case 'm':
				move = true;
				break;
			case 'n':
				arg = g.getOptarg();
//				checkArgOf_n(arg);
				name = arg;
				break;
			case '?':
				break; // getopt() already printed an error
			default:
				break;
			}
		}
		int i = g.getOptind();

		// check if repository and data set specified
		if (args.length < i + 3) {
			throw new RuntimeException(
					"Data set or data repository not specified.");
		}
		String repositoryPath = args[++i];
		String dataSet = args[++i];

		new Control().executeAdd(new File(repositoryPath), new File(dataSet),
				name, description, move);
	}

	@ShellCallable
	private void server(String[] args) throws IOException {
		// check if repository and data set specified
		if (args.length < 3) {
			throw new RuntimeException(
					"Property file or data repository not specified.");
		}
		String repositoryPath = args[1];
		String propertyFilePath = args[2];

		new Control().executeServer(new File(repositoryPath), new File(
				propertyFilePath));
	}

	@ShellCallable
	private void delete(String[] args) {
		// check if repository and data set specified
		if (args.length < 3) {
			throw new RuntimeException(
					"Data set or data repository not specified.");
		}
		String repositoryPath = args[1];
		String dataSetName = args[2];

		new Control().executeDelete(new File(repositoryPath), dataSetName);
	}

	@ShellCallable
	private void export(String[] args) {
		// check if repository and data set specified
		if (args.length < 4) {
			throw new RuntimeException(
					"Data set or data repository not specified.");
		}
		String repositoryPath = args[1];
		String dataSetName = args[2];
		String destinationFolderName = args[3];
		new Control().executeExport(new File(repositoryPath), dataSetName,
				new File(destinationFolderName));
	}

	@ShellCallable
	private void list(String[] args) {
		Getopt g = new Getopt("data-repository", args, "p");
		int c;
		boolean pretty = false;
		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 'p':
				pretty = true;
				break;
			case '?':
				break; // getopt() already printed an error
			default:
				break;
			}
		}
		int i = g.getOptind();
		// check if repository and data set specified
		if (args.length < i + 2) {
			throw new RuntimeException(
					"Data set or data repository not specified.");
		}
		String repositoryPath = args[++i];
		new Control().executeList(new File(repositoryPath), pretty);
	}

	@ShellCallable
	private void replace(String[] args) {
		Getopt g = new Getopt("data-repository", args, "d:m");
		int c;
		String arg;
		String description = "";
		boolean move = false;
		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 'd':
				arg = g.getOptarg();
//				checkArgOf_d(arg);
				description = arg;
				break;
			case 'm':
				move = true;
				break;
			case '?':
				break; // getopt() already printed an error
			default:
				break;
			}
		}
		int i = g.getOptind();
		// check if repository and data set specified
		if (args.length < i + 4) {
			throw new RuntimeException(
					"Data set or data repository not specified.");
		}
		String repositoryPath = args[++i];
		String dataSetToReplace = args[++i];
		String dataSetToAdd = args[++i];

		new Control().executeReplace(new File(repositoryPath),
				dataSetToReplace, new File(dataSetToAdd), description, move);

	}

	@ShellCallable
	private void help(String[] args) throws IOException {
		new Control().executeHelp();
	}
	
	@ShellCallable
	private void gui(String[] args){
		new Control().executeGUI();
	}

	/**
	 * @param args
	 *            The desired method is called automatically, just copy the
	 *            "add" method (incl. 1 line annotation above it) and modify it
	 */
	static void parse(String[] args) {
		Method m;
		try {
			m = Main.class.getDeclaredMethod(args[0], String[].class);
			if (!m.isAnnotationPresent(ShellCallable.class)) {
				throw new NoSuchMethodException();
			}
			m.invoke(new Main(), new Object[] { args });
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Command invalid");
		} catch (SecurityException e) {
			throw new RuntimeException("Command invalid");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Command invalid");
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Command invalid");
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause(); // will get caught in
														// main
			} else {
				throw new RuntimeException("Error executing command"); // will
																		// get
																		// caught
																		// in
																		// main
			}
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 1) {
				new Control().executeHelp();
			} else {
				parse(args);
			}
		} catch (RuntimeException ex) {
			OutHelper.error(ex.getMessage());
			System.exit(1);
		}
	}

}

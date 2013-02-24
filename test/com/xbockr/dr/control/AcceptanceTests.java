package com.xbockr.dr.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xbockr.dr.server.MarkerFileDetection;
import com.xbockr.dr.server.UnchangedModificationDateDetection;

public class AcceptanceTests {
	private final static int BIGFILESIZE = 2000000;

	private final static File tmpFolder = new File("./tmp"), file1 = new File(
			tmpFolder, "f1.txt"), file2 = new File(tmpFolder, "f2.txt"),
			folder = new File(tmpFolder, "folder"), bigFile = new File(
					tmpFolder, "bigfile.txt"), bigFile2 = new File(tmpFolder,
					"bigfile2.txt"), fileInFolder = new File(folder,
					"fileInFolder.txt"), folderInFolder = new File(folder,
					"fif"), fileInFolderInFolder = new File(folderInFolder,
					"fifif.txt"), incomingDir = new File(tmpFolder,
					"./incoming"), exportDir = new File(tmpFolder, "./export"),
			repo = new File(tmpFolder, "repo"), html = new File(tmpFolder,
					"overview.html"), log = new File(tmpFolder, "server.log"),
			properties = new File(tmpFolder, "server.properties");

	@Before
	public void setUp() throws Exception {
		folder.mkdirs();
		folderInFolder.mkdirs();
		incomingDir.mkdirs();

		for (File f : new File[] { file1, file2, fileInFolder,
				fileInFolderInFolder }) {
			FileWriter fw = new FileWriter(f);
			fw.write("This is file content in file " + f.getName());
			fw.close();
		}
		char[] bytes = new char[BIGFILESIZE];
		for (int i = 0; i < BIGFILESIZE; i++) {
			bytes[i] = (char) (i % 256);
		}
		FileWriter fw = new FileWriter(bigFile);
		fw.write(bytes);
		fw.close();
		fw = new FileWriter(bigFile2);
		fw.write(bytes);
		fw.close();
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(tmpFolder);
	}

	@Test
	public void testOnSheet() throws IOException, InterruptedException {
		Main.parse(new String[] { "add", repo.getPath(), bigFile.getPath() });
		Main.parse(new String[] { "add", repo.getPath(), "-m",
				bigFile.getPath() });
		assertFalse(bigFile.exists());
		Main.parse(new String[] { "add", repo.getPath(), folder.getPath() });
		Main.parse(new String[] { "add", repo.getPath(), "-m", folder.getPath() });
		assertFalse(folder.exists());
		PrintStream o = System.out;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos));
		Main.parse(new String[] { "list", repo.getPath() });
		System.setOut(o);
		String listOut = baos.toString();
		System.out.println(listOut);
		assertTrue(Pattern
				.matches(
						"(?ms)Name	Original Name	Timestamp	Number of Files	Size	Description\n"
								+ "FOLDER_1	folder	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	2	83	\n"
								+ "FOLDER_0	folder	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	2	83	\n"
								+ "BIGFILETXT_1	bigfile.txt	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	1	2999936	\n"
								+ "BIGFILETXT_0	bigfile.txt	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	1	2999936	\n",
						listOut));
		baos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos));
		Main.parse(new String[] { "list", "-p", repo.getPath() });
		listOut = baos.toString();
		System.setOut(o);
		System.out.println(listOut);
		assertTrue(Pattern
				.matches(
						"(?ms) Name         \\| Original Name \\| Timestamp           \\| Number of Files \\| Size    \\| Description \n"
								+ "--------------\\+---------------\\+---------------------\\+-----------------\\+---------\\+-------------\n"
								+ " FOLDER_1     \\| folder        \\| \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} \\|               2 \\|      83 \\|             \n"
								+ " FOLDER_0     \\| folder        \\| \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} \\|               2 \\|      83 \\|             \n"
								+ " BIGFILETXT_1 \\| bigfile.txt   \\| \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} \\|               1 \\| 2999936 \\|             \n"
								+ " BIGFILETXT_0 \\| bigfile.txt   \\| \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} \\|               1 \\| 2999936 \\|             \n",
						listOut));
		Main.parse(new String[] { "export", repo.getPath(), "FOLDER_1",
				exportDir.getPath() });
		File expF1 = new File(exportDir, folder.getName());
		assertTrue(exportDir.exists() && exportDir.isDirectory()
				&& expF1.exists()
				&& new File(new File(expF1, "fif"), "fifif.txt").exists());
		Main.parse(new String[] { "replace", repo.getPath(), "BIGFILETXT_0",
				exportDir.getPath() });
		baos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos));
		Main.parse(new String[] { "list", repo.getPath() });
		listOut = baos.toString();
		System.setOut(o);
		System.out.println(listOut);
		assertTrue(Pattern
				.matches(
						"(?ms)Name	Original Name	Timestamp	Number of Files	Size	Description\n"
								+ "BIGFILETXT_0	export	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	2	83	\n"
								+ "FOLDER_1	folder	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	2	83	\n"
								+ "FOLDER_0	folder	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	2	83	\n"
								+ "BIGFILETXT_1	bigfile.txt	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	1	2999936	\n",
						listOut));
		Main.parse(new String[] { "export", repo.getPath(), "BIGFILETXT_0",
				exportDir.getPath() });
		assertTrue(new File(new File(exportDir, "export"), folder.getName())
				.exists());
		Main.parse(new String[] { "delete", repo.getPath(), "FOLDER_1" });
		Main.parse(new String[] { "delete", repo.getPath(), "FOLDER_0" });
		baos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos));
		Main.parse(new String[] { "list", repo.getPath() });
		listOut = baos.toString();
		System.setOut(o);
		System.out.println(listOut);
		assertTrue(Pattern
				.matches(
						"(?ms)Name	Original Name	Timestamp	Number of Files	Size	Description\n"
								+ "BIGFILETXT_0	export	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	2	83	\n"
								+ "BIGFILETXT_1	bigfile.txt	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	1	2999936	\n",
						listOut));
		Properties props = new Properties();
		props.setProperty("incoming-directory", incomingDir.getPath());
		props.setProperty("html-overview", html.getPath());
		props.setProperty("log-file", log.getPath());
		props.setProperty("checking-interval-in-seconds", "" + 1);
		props.setProperty("completeness-detection.class-name",
				MarkerFileDetection.class.getName());
		String prefix = ".dr_prefix_";
		props.setProperty("completeness-detection.prefix", prefix);
		props.store(new FileWriter(properties), null);
		Thread sthread = new Thread(new Runnable() {
			@Override
			public void run() {
				Main.parse(new String[] { "server", repo.getPath(),
						properties.getPath() });
			}
		});
		sthread.setDaemon(true);
		sthread.start();
		Thread.sleep(500);
		String htmlString = FileUtils.readFileToString(html);
		// System.out.println(htmlString);
		assertTrue(Pattern
				.matches(

						"(?ms)<html>\n"
								+ "<body>\n"
								+ "<h1>html overview</h1>\n"
								+ "<table border=\"1\">\n"
								+ "<tr><th>Name</th><th>Original Name</th><th>Timestamp</th><th>Number of Files</th><th>Size</th><th>Description</th></tr>\n"
								+ "<tr><td>BIGFILETXT_0</td><td>export</td><td>\\d{4}-\\d{2}-\\d{2} "
								+ "\\d{2}:\\d{2}:\\d{2}</td><td>2</td><td>83</td><td></td></tr>\n"
								+ "<tr><td>BIGFILETXT_1</td><td>bigfile.txt</td><td>\\d{4}-\\d{2}-\\d{2} "
								+ "\\d{2}:\\d{2}:\\d{2}</td><td>1</td><td>2999936</td><td></td></tr>\n"
								+ "</table>\n" + "</body>\n" + "</html>\n"

						, htmlString));
		String logString = FileUtils.readFileToString(log);
		// System.out.println(logString);
		FileUtils.copyFileToDirectory(bigFile2, incomingDir);
		Thread.sleep(1100);
		File mbf2 = new File(incomingDir, bigFile2.getName());
		assertTrue(mbf2.exists());
		assertEquals(htmlString, FileUtils.readFileToString(html));
		assertEquals(logString, FileUtils.readFileToString(log));
		File marker = new File(incomingDir, prefix + mbf2.getName());
		FileUtils.touch(marker);
		Thread.sleep(1100);
		assertTrue(Pattern
				.matches(
						"<html>\n"
								+ "<body>\n"
								+ "<h1>html overview</h1>\n"
								+ "<table border=\"1\">\n"
								+ "<tr><th>Name</th><th>Original Name</th><th>Timestamp</th><th>Number of Files</th><th>Size</th><th>Description</th></tr>\n"
								+ "<tr><td>BIGFILE2TXT_0</td><td>bigfile2.txt</td><td>\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}</td><td>1</td><td>2999936</td><td></td></tr>\n"
								+ "<tr><td>BIGFILETXT_0</td><td>export</td><td>\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}</td><td>2</td><td>83</td><td></td></tr>\n"
								+ "<tr><td>BIGFILETXT_1</td><td>bigfile.txt</td><td>\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}</td><td>1</td><td>2999936</td><td></td></tr>\n"
								+ "</table>\n" + "</body>\n" + "</html>\n",
						FileUtils.readFileToString(html)));

		List<String> logLines = FileUtils.readLines(log);
		assertTrue(Pattern
				.matches(
						"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} \\[INFO\\] successfully added bigfile2.txt to repository",
						logLines.get(logLines.size() - 1)));
		sthread.interrupt();
		sthread.join();
		assertFalse(sthread.isAlive());
		baos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos));
		Main.parse(new String[] { "list", repo.getPath() });
		listOut = baos.toString();
		System.setOut(o);
		System.out.println(listOut);
		assertTrue(Pattern
				.matches(
						"(?ms)Name	Original Name	Timestamp	Number of Files	Size	Description\n"
								+ "BIGFILE2TXT_0	bigfile2.txt	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	1	2999936	\n"
								+ "BIGFILETXT_0	export	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	2	83	\n"
								+ "BIGFILETXT_1	bigfile.txt	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	1	2999936	\n",
						listOut));
		Main.parse(new String[] { "export", repo.getPath(), "BIGFILE2TXT_0",
				exportDir.getPath() });
		assertTrue(new File(exportDir, "bigfile2.txt").exists());
		props.remove("completeness-detection.prefix");
		props.setProperty("completeness-detection.class-name",
				UnchangedModificationDateDetection.class.getName());
		props.setProperty("completeness-detection.quiet-period-in-seconds",
				"" + 2);
		props.store(new FileWriter(properties), null);
		sthread = new Thread(new Runnable() {
			@Override
			public void run() {
				Main.parse(new String[] { "server", repo.getPath(),
						properties.getPath() });
			}
		});
		sthread.setDaemon(true);
		sthread.start();
		Thread.sleep(500);
		File fol1 = new File(incomingDir, "fol1");
		File fol2 = new File(fol1, "fol2");
		fol1.mkdir();
		FileUtils.copyFileToDirectory(file1, fol1);
		fol2.mkdir();
		FileUtils.copyFileToDirectory(file2, fol2);
		Thread.sleep(3100);
		assertFalse(fol1.exists());
		assertTrue(Pattern
				.matches(
						"(?ms)<html>\n"
								+ "<body>\n"
								+ "<h1>html overview</h1>\n"
								+ "<table border=\"1\">\n"
								+ "<tr><th>Name</th><th>Original Name</th><th>Timestamp</th><th>Number of Files</th><th>Size</th><th>Description</th></tr>\n"
								+ "<tr><td>FOL1_0</td><td>fol1</td><td>\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}</td><td>2</td><td>70</td><td></td></tr>\n"
								+ "<tr><td>BIGFILE2TXT_0</td><td>bigfile2.txt</td><td>\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}</td><td>1</td><td>2999936</td><td></td></tr>\n"
								+ "<tr><td>BIGFILETXT_0</td><td>export</td><td>\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}</td><td>2</td><td>83</td><td></td></tr>\n"
								+ "<tr><td>BIGFILETXT_1</td><td>bigfile.txt</td><td>\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}</td><td>1</td><td>2999936</td><td></td></tr>\n"
								+ "</table>\n" + "</body>\n" + "</html>\n" + "",
						FileUtils.readFileToString(html)));
		logLines = FileUtils.readLines(log);
		assertTrue(Pattern
				.matches(
						"(?ms)\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} \\[INFO\\] successfully added fol1 to repository",
						logLines.get(logLines.size() - 1)));
		sthread.interrupt();
		sthread.join();
		assertFalse(sthread.isAlive());
		baos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos));
		Main.parse(new String[] { "list", repo.getPath() });
		listOut = baos.toString();
		System.setOut(o);
		System.out.println(listOut);
		assertTrue(Pattern
				.matches(
						"(?ms)Name	Original Name	Timestamp	Number of Files	Size	Description\n"
								+ "FOL1_0	fol1	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	2	70	\n"
								+ "BIGFILE2TXT_0	bigfile2.txt	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	1	2999936	\n"
								+ "BIGFILETXT_0	export	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	2	83	\n"
								+ "BIGFILETXT_1	bigfile.txt	\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}	1	2999936	\n",
						listOut));
		Main.parse(new String[] { "export", repo.getPath(), "FOL1_0",
				exportDir.getPath() });
		File newFol1 = new File(exportDir, "fol1");
		assertTrue(newFol1.exists() && newFol1.isDirectory());
//		System.out.println("nop");
	}
}

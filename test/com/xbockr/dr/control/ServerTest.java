package com.xbockr.dr.control;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {
	private final static File tmpFolder = new File("./tmp"), file1 = new File(
			tmpFolder, "f1.txt"), file2 = new File(tmpFolder, "f2.txt"),
			folder = new File(tmpFolder, "folder"), fileInFolder = new File(
					folder, "fileInFolder.txt"), incomingDir = new File(
					tmpFolder, "incoming"), properties = new File(
					"tmp/test.properties");

	Thread serverThread;
	Properties props;

	private String markerPrefix;

	@Before
	public void setUp() throws Exception {
		props = new Properties();
		folder.mkdirs();
		incomingDir.mkdirs();

		FileInputStream propsIn = new FileInputStream("dist/default.properties");
		props.load(propsIn);
		propsIn.close();

		props.setProperty("incoming-directory", incomingDir.getPath());
		props.setProperty("html-overview", "tmp/overview.html");
		props.setProperty("log-file", "tmp/server.log");
		props.setProperty("checking-interval-in-seconds", "1");
		props.setProperty("completeness-detection.class-name",
				"com.xbockr.dr.server.NoDetection");
		markerPrefix = ".data_repository_marker_";
		props.setProperty("completeness-detection.prefix", markerPrefix);
		props.setProperty("completeness-detection.quiet-period-in-seconds", "2");

		writeProperties();

		for (File f : new File[] { file1, file2, fileInFolder }) {
			FileWriter fw = new FileWriter(f);
			fw.write("This is file content in file " + f.getName());
			fw.close();
		}
		restartServer();
	}

	private void restartServer() throws InterruptedException {
		if (serverThread != null && serverThread.isAlive()) {
			serverThread.interrupt();
		}
		serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Main.parse(new String[] { "server", "tmp/repo",
						properties.getPath() });
			}
		});
		// serverThread.setDaemon(true);
		serverThread.start();
		Thread.sleep(200);
	}

	private void writeProperties() throws FileNotFoundException, IOException {
		FileOutputStream propsOut = new FileOutputStream(properties);
		props.store(propsOut, "test properties");
		propsOut.close();
	}

	@After
	public void tearDown() throws Exception {
		if (serverThread != null && serverThread.isAlive()) {
			serverThread.interrupt();
		}
		FileUtils.deleteDirectory(tmpFolder);
	}

	@Test
	public void startServerTest() throws IOException, InterruptedException {
		// System.out.println(Arrays.asList(new
		// File(tmpFolder,"repo/.xbockr").list()));
		FileUtils.copyFileToDirectory(file1, incomingDir);
		File newFile = new File(incomingDir, file1.getName());
		FileUtils.touch(newFile);
		assertTrue(newFile.exists());
		Thread.sleep(1100);
		assertFalse(newFile.exists());
		assertTrue(new File(tmpFolder, "server.log").exists());
	}

	@Test
	public void markerDetection() throws IOException, InterruptedException {
		props.setProperty("completeness-detection.class-name",
				"com.xbockr.dr.server.MarkerFileDetection");
		writeProperties();
		restartServer();
		FileUtils.copyFileToDirectory(file1, incomingDir);
		File newFile = new File(incomingDir, file1.getName());
		FileUtils.touch(newFile);
		assertTrue(newFile.exists());
		Thread.sleep(1100);
		assertTrue(newFile.exists());
		File marker = new File(incomingDir, markerPrefix + file1.getName());
		FileUtils.touch(marker);
		Thread.sleep(1100);
		assertFalse(newFile.exists());
		assertFalse(marker.exists());
	}

	@Test
	public void unchangedDetection() throws FileNotFoundException, IOException,
			InterruptedException {
		props.setProperty("completeness-detection.class-name",
				"com.xbockr.dr.server.UnchangedModificationDateDetection");
		writeProperties();
		restartServer();
		FileUtils.copyFileToDirectory(file1, incomingDir);
		File newFile = new File(incomingDir, file1.getName());
		FileUtils.touch(newFile);
		FileUtils.copyDirectoryToDirectory(folder, incomingDir);
		File newFolder = new File(incomingDir,folder.getName());
		FileUtils.touch(newFolder);
		assertTrue(newFile.exists() && newFile.isFile());
		assertTrue(newFolder.exists() && newFolder.isDirectory());
		Thread.sleep(1100);
		assertTrue(newFile.exists());
		PrintWriter w = new PrintWriter(newFile);
		for(int i=0;i<5;i++){
			Thread.sleep(250);
			assertTrue(newFile.exists());
			w.println("i = " + i);
		}
		w.close();
		Thread.sleep(3100);
		assertFalse(newFile.exists());
		assertFalse(newFolder.exists());
	}
}

package com.xbockr.dr.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xbockr.dr.filesystem.FileSystem.FSException;

public class FileSystemTest {

	private final static File tmpFolder = new File("./tmp"), file1 = new File(
			tmpFolder, "f1.txt"), file2 = new File(tmpFolder, "f2.txt"),
			folder = new File(tmpFolder, "folder"), fileInFolder = new File(
					folder, "fileInFolder.txt");

	@Before
	public void setUp() throws Exception {
		folder.mkdirs();

		for (File f : new File[] { file1, file2, fileInFolder }) {
			FileWriter fw = new FileWriter(f);
			fw.write("This is file content in file " + f.getName());
			fw.close();
		}
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(tmpFolder);
	}

	@Test
	public void testMove() throws FSException {
		File f1 = new File(folder, "f1.txt");
		File f2 = new File(file1.getAbsolutePath());
		assertFalse(f1.exists());
		assertTrue(f2.exists());
		new FileSystem(tmpFolder).move(file1, folder);
		assertTrue(f1.exists());
		assertFalse(f2.exists());
	}

	@Test
	public void testCopy() throws FSException {
		File f1 = new File(folder, "f1.txt");
		File f2 = new File(file1.getAbsolutePath());
		assertFalse(f1.exists());
		assertTrue(f2.exists());
		new FileSystem(tmpFolder).copy(file1, folder);
		assertTrue(f1.exists());
		assertTrue(f2.exists());
	}
	
	@Test(expected=FSException.class)
	public void testMoveInexistentFile() throws FSException{
		new FileSystem(tmpFolder).move(new File("./asdfijf/afysd/hsh"), folder);
	}
	
	@Test
	public void testMoveFolder() throws FSException{
		File newFolder = new File(tmpFolder,"newFolder");
		assertTrue(!newFolder.exists());
		new FileSystem(tmpFolder).move(folder, newFolder);
		assertTrue(newFolder.exists());
		assertFalse(folder.exists());
		assertTrue(Arrays.asList(new File(newFolder,"folder").list()).contains("fileInFolder.txt"));
	}
	
	@Test
	public void testGetStuff(){
		assertEquals(45L,new FileSystem(tmpFolder).getSize(folder).longValue());
		assertEquals("folder",new FileSystem(tmpFolder).getOriginalName(folder));
		assertEquals(1L,new FileSystem(tmpFolder).getNumberOfFiles(folder).longValue());
	}
	
	@Test
	public void testDelete(){
		assertTrue(fileInFolder.exists());
		new FileSystem(tmpFolder).delete(folder);
		assertFalse(fileInFolder.exists());
	}

}

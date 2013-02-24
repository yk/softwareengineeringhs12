package com.xbockr.dr.control;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xbockr.dr.repository.IRepository;
import com.xbockr.dr.repository.meta.db.IDatabase;

public class MainTest {

	private final static File tmpFolder = new File("./tmp"), file1 = new File(
			tmpFolder, "f1.txt"), file2 = new File(tmpFolder, "f2.txt"),
			folder = new File(tmpFolder, "folder"), fileInFolder = new File(
					folder, "fileInFolder.txt"), incomingDir = new File(tmpFolder,"./incoming");

	@Before
	public void setUp() throws Exception {
		folder.mkdirs();
		incomingDir.mkdirs();

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
	public void testList() {
		testAddTwiceWithoutName();
		Main.parse(new String[]{"list","tmp/repo"});
		Main.parse(new String[]{"list","-p","tmp/repo"});
	}

	//there is not much to test here, since everything has been tested in the other tests already
	@Test
	public void testAdd() {
		assertTrue(file1.exists());
		Main.parse(new String[]{"add" , "-n", "MYNAME", "-d" , "teh description", "-m" , "tmp/repo", "tmp/f1.txt"});
		assertFalse(file1.exists());
		assertTrue(new File(new File(new File(tmpFolder,"repo"),IRepository.HIDDENDIRECTORYNAME),IDatabase.DBFILENAME).exists());
		assertTrue(new File(new File(tmpFolder,"repo"),"MYNAME").exists());
	}
	
	@Test
	public void testAddWithoutName() {
		assertTrue(file1.exists());
		Main.parse(new String[]{"add", "-d" , "teh description", "tmp/repo", "tmp/f1.txt"});
		assertTrue(file1.exists());
		assertTrue(new File(new File(new File(tmpFolder,"repo"),IRepository.HIDDENDIRECTORYNAME),IDatabase.DBFILENAME).exists());
		assertTrue(new File(new File(tmpFolder,"repo"),"F1TXT_0").exists());
	}
	
	@Test
	public void testAddTwiceWithoutName() {
		assertTrue(file1.exists());
		Main.parse(new String[]{"add", "-d" , "teh description", "tmp/repo", "tmp/f1.txt"});
		assertTrue(file1.exists());
		assertTrue(new File(new File(new File(tmpFolder,"repo"),IRepository.HIDDENDIRECTORYNAME),IDatabase.DBFILENAME).exists());
		assertTrue(new File(new File(tmpFolder,"repo"),"F1TXT_0").exists());
		assertFalse(new File(new File(tmpFolder,"repo"),"F1TXT_1").exists());
		Main.parse(new String[]{"add", "-d" , "teh description", "tmp/repo", "tmp/f1.txt"});
		assertTrue(file1.exists());
		assertTrue(new File(new File(tmpFolder,"repo"),"F1TXT_1").exists());
	}
	
	@Test
	public void testAddTwoDifferentWithoutName() {
		assertTrue(file1.exists());
		Main.parse(new String[]{"add", "-d" , "teh description", "tmp/repo", "tmp/f1.txt"});
		assertTrue(file1.exists());
		assertTrue(new File(new File(new File(tmpFolder,"repo"),IRepository.HIDDENDIRECTORYNAME),IDatabase.DBFILENAME).exists());
		assertTrue(new File(new File(tmpFolder,"repo"),"F1TXT_0").exists());
		assertFalse(new File(new File(tmpFolder,"repo"),"F2TXT_0").exists());
		Main.parse(new String[]{"add","-m",  "-d" , "teh description", "tmp/repo", "tmp/f2.txt"});
		assertFalse(file2.exists());
		assertTrue(new File(new File(tmpFolder,"repo"),"F2TXT_0").exists());
	}
	
	@Test //this was a bug in the last version
	public void testAddTwoThings() {
		testAdd();
		assertTrue(file2.exists());
		Main.parse(new String[]{"add" , "-n", "MYNAME2", "-d" , "teh other description", "-m" , "tmp/repo", "tmp/f2.txt"});
		assertFalse(file2.exists());
		assertTrue(new File(new File(new File(tmpFolder,"repo"),IRepository.HIDDENDIRECTORYNAME),IDatabase.DBFILENAME).exists());
		assertTrue(new File(new File(tmpFolder,"repo"),"MYNAME").exists());
	}
	
	@Test
	public void testDelete(){
		testAdd();
		assertTrue(new File(new File(tmpFolder,"repo"),"MYNAME").exists());
		assertTrue(new File(new File(new File(tmpFolder,"repo"),"MYNAME"),"f1.txt").exists());
		Main.parse(new String[]{"delete","tmp/repo","MYNAME"});
		assertFalse(new File(new File(new File(tmpFolder,"repo"),"MYNAME"),"f1.txt").exists());
		assertFalse(new File(new File(tmpFolder,"repo"),"MYNAME").exists());
	}
	
	@Test
	public void testDeleteThenAddAgain() throws IOException{
		testDelete();
		FileUtils.moveFile(file2, file1); //giving add something to work with
		testAdd();
		assertTrue(new File(new File(tmpFolder,"repo"),"MYNAME").exists());
		assertTrue(new File(new File(new File(tmpFolder,"repo"),"MYNAME"),"f1.txt").exists());
	}
	
	@Test
	public void testExport(){
		testAdd();
		Main.parse(new String[]{"export","tmp/repo","MYNAME","tmp/exp"});
		assertTrue(new File(new File(tmpFolder,"exp"),"f1.txt").exists());
	}
	
	@Test(expected=RuntimeException.class)
	public void testDeleteNonexistant(){
		testAdd();
		Main.parse(new String[]{"delete","tmp/repo","asldifuahsd"});
	}
	
	@Test(expected=RuntimeException.class)
	public void testNonCapsInName() {
		Main.parse(new String[]{"add" , "-n", "MyNAME", "-d" , "teh description", "-m" , "tmp/repo", "tmp/f1.txt"});
	}
	
//	//will hang since it's a while loop
//	@Test
//	public void startServer(){
//		Main.parse(new String[]{"server", "tmp/repo","dist/test.properties"});
//	}

}

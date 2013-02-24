package com.xbockr.dr.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xbockr.dr.repository.meta.MetaEngine;
import com.xbockr.dr.repository.meta.MetaInfo;
import com.xbockr.dr.repository.meta.db.IDatabase;

public class RepositoryTest {

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

	@Test(expected = RuntimeException.class)
	public void createInNonemptyFolder() {
		new Repository(folder, true);
	}

	@Test(expected = RuntimeException.class)
	public void createWhenNotCreating() {
		new Repository(new File(tmpFolder, "repo"),
				false);
	}

	@Test(expected = RuntimeException.class)
	public void createWithinRepository() {
		File repoFolder = new File(tmpFolder, "repo");
		try {
			new Repository(repoFolder, true);
		} catch (RuntimeException e) {
			fail();
		}
		new Repository(new File(repoFolder, "repo2"),
				true);
	}

	@Test
	public void createTest() {
		File repoFolder = new File(tmpFolder, "repo");
		assertFalse(repoFolder.exists());
		new Repository(repoFolder, true);
		assertTrue(repoFolder.exists() && repoFolder.isDirectory());
		assertTrue(new File(new File(repoFolder,
				IRepository.HIDDENDIRECTORYNAME), IDatabase.DBFILENAME)
				.exists());
	}

	@Test(expected = RuntimeException.class)
	public void addTwiceTest() {
		File repoFolder = new File(tmpFolder, "repo");
		MetaInfo metaInfo = MetaInfo.createFromAdd("ASDF", "oh the file");
		try {
			new Repository(repoFolder, true).add(
					metaInfo, file1, false);
		} catch (RuntimeException e) {
			fail();
		}
		new Repository(repoFolder, true).add(
				metaInfo, file1, false);
	}

	@Test(expected = RuntimeException.class)
	public void addInvalidTest() {
		File repoFolder = new File(tmpFolder, "repo");
		MetaInfo metaInfo = MetaInfo.createFromAdd("ASDF", "oh the file");
		new Repository(repoFolder, true).add(
				metaInfo, new File("./tmp/asdkfj/afuy"), false);
	}

	@Test
	public void addMoveTest() {
		File repoFolder = new File(tmpFolder, "repo");
		MetaInfo metaInfo = MetaInfo.createFromAdd("ASDF", "oh the file");
		assertTrue(file1.exists());
		new Repository(repoFolder, true).add(
				metaInfo, file1, true);
		assertFalse(file1.exists());
		assertTrue(new MetaEngine(repoFolder).exists(MetaInfo.createFromAdd(
				"ASDF", "lalla")));
	}

	@Test
	public void addCopyTest() {
		File repoFolder = new File(tmpFolder, "repo");
		MetaInfo metaInfo = MetaInfo.createFromAdd("ASDF", "oh the file");
		assertTrue(file1.exists());
		new Repository(repoFolder, true).add(
				metaInfo, file1, false);
		assertTrue(file1.exists());
		assertTrue(new MetaEngine(repoFolder).exists(MetaInfo.createFromAdd(
				"ASDF", "lalla")));
	}
	
	@Test
	public void deleteTest(){
		MetaInfo metaInfo = MetaInfo.createFromAdd("ASDF", "oh the file");
		File repoFolder = new File(tmpFolder, "repo");
		File whithinFolder = new File(repoFolder,metaInfo.getName());
		File file = new File(whithinFolder,file1.getName());
		Repository repository = new Repository(repoFolder, true);
		assertTrue(file1.exists());
		assertFalse(whithinFolder.exists());
		assertFalse(file.exists());
		repository.add(metaInfo, file1, true);
		assertFalse(file1.exists());
		assertTrue(whithinFolder.exists());
		assertTrue(file.exists());
		assertTrue(repository.getMetaEngine().exists(metaInfo));
		repository.delete(metaInfo.getName());
		assertFalse(repository.getMetaEngine().exists(metaInfo));
		assertFalse(whithinFolder.exists());
		assertFalse(file.exists());
	}
	
	@Test
	public void exportTest(){
		MetaInfo metaInfo = MetaInfo.createFromAdd("ASDF", "oh the file");
		File repoFolder = new File(tmpFolder, "repo");
		Repository repository = new Repository(repoFolder, true);
		repository.add(metaInfo, folder, true);
		assertFalse(folder.exists());
		assertTrue(repository.getMetaEngine().exists(metaInfo));
		File exportFolder = new File(tmpFolder, "exp");
		File fifief = new File(new File(exportFolder, folder.getName()),fileInFolder.getName());
		assertFalse(fifief.exists());
		repository.export(metaInfo.getName(), exportFolder);
		assertTrue(fifief.exists());
	}
	
	@Test(expected=RuntimeException.class)
	public void exportToFile(){
		addCopyTest();
		new Repository(new File(tmpFolder, "repo"), false).export("asdf", file2);
	}
}

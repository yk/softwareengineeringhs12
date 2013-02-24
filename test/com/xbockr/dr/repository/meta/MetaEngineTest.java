package com.xbockr.dr.repository.meta;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xbockr.dr.repository.IRepository;
import com.xbockr.dr.repository.meta.db.Database.DBException;

public class MetaEngineTest {

	private static final File tmpFolder = new File("./tmp"), hiddenFolder = new File(tmpFolder,IRepository.HIDDENDIRECTORYNAME);

	@Before
	public void setUp() throws Exception {
		hiddenFolder.mkdirs();
	}

	@Test
	// test: couldn't create database
	public void existsErrorTest() {
		try {
			MetaInfo mi = MetaInfo.createFromAdd("TEST.TXT", "this is a test");
			hiddenFolder.delete();
			new MetaEngine(tmpFolder).exists(mi);
			fail();
		} catch (RuntimeException e) {
			assertEquals("Error creating the database", e.getMessage());
		}
	}

	@Test
	public void addTest() throws DBException {
		MetaInfo mi1 = MetaInfo.createFromAdd("TEST", "this is a lovely file");
		MetaEngine mE = new MetaEngine(tmpFolder);
		mE.add(mi1);
		assertTrue(mE.exists(mi1));
		assertTrue(new MetaEngine(tmpFolder).exists(mi1));

	}
	
	@Test
	public void addTwiceTest() throws DBException {
		MetaInfo mi1 = MetaInfo.createFromAdd("TEST", "this is a lovely file");
		MetaInfo mi2 = MetaInfo.createFromAdd("TEST", "this is not the same");
		MetaEngine mE = new MetaEngine(tmpFolder);
		mE.add(mi1);
		assertTrue(mE.exists(mi1));
		assertTrue(new MetaEngine(tmpFolder).exists(mi1));
		assertTrue(new MetaEngine(tmpFolder).exists(mi2));

	}
	
	@Test
	public void deleteTest() throws DBException{
		addTest();
		MetaEngine mE = new MetaEngine(tmpFolder);
		assertTrue(mE.exists(MetaInfo.createFromAdd("TEST", "asdf")));
		mE.delete("TEST");
		assertFalse(mE.exists(MetaInfo.createFromAdd("TEST", "asdf")));
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(tmpFolder);
	}

}

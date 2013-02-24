package com.xbockr.dr.repository.meta.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xbockr.dr.repository.IRepository;
import com.xbockr.dr.repository.meta.db.Database.DBException;

public class DatabaseTest {
	private final static File pathFolder = new File("./tmp"),
			hiddenFolder = new File(pathFolder, IRepository.HIDDENDIRECTORYNAME);

	@Before
	public void setUp() throws Exception {
		hiddenFolder.mkdirs();
	}

	@After
	public void tearDown() throws Exception {
	FileUtils.deleteDirectory(pathFolder);	
	}

	@Test
	public void createDatabaseTest() {
		File f = new File("./tmp/.xbockr/.db");
		assertFalse(f.exists());
		new Database(pathFolder);
		assertTrue(f.exists());
	}
	
	@Test(expected=SQLException.class)
	public void crapStatementTest() throws SQLException{
		try {
			new Database(pathFolder).executeStatement("insert into JDALFJDSKF values(2,3,4)");
		} catch (DBException e) {
			throw e.getCause();
		}
	}
	
	@Test
	public void correctStatementTest() throws DBException{
		String statement = "insert into MetaInfo(name,description,originalName,timestamp,numberOfFiles,size) values('%s','%s','%s',%d,%d,%d)";
		statement = String.format(statement,"supername",
				"superdescription", "superoriginalname",
				new Date().getTime(), 3,
				23850972);
		new Database(pathFolder).executeUpdate(statement);
	}
}

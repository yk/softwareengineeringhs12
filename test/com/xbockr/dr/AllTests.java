package com.xbockr.dr;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.xbockr.dr.control.AcceptanceTests;
import com.xbockr.dr.control.MainTest;
import com.xbockr.dr.control.ServerTest;
import com.xbockr.dr.filesystem.FileSystemTest;
import com.xbockr.dr.repository.RepositoryTest;
import com.xbockr.dr.repository.meta.MetaEngineTest;
import com.xbockr.dr.repository.meta.MetaInfoTest;
import com.xbockr.dr.repository.meta.db.DatabaseTest;
import com.xbockr.dr.util.HelpMsgPrinterTest;
import com.xbockr.dr.util.MetaInfoListPrinterTest;
import com.xbockr.dr.util.OutHelper;

@RunWith(Suite.class)
@SuiteClasses({ MainTest.class, RepositoryTest.class, FileSystemTest.class,
		MetaEngineTest.class, MetaInfoTest.class, DatabaseTest.class,
		MetaInfoListPrinterTest.class, HelpMsgPrinterTest.class,
		ServerTest.class, AcceptanceTests.class })
public class AllTests {
	@BeforeClass
	public static void setUp() {
		OutHelper.toStdOut(false);
	}
}

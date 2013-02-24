package com.xbockr.dr.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.xbockr.dr.repository.meta.MetaInfo;

public class MetaInfoListPrinterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void alignTest() {
		List<MetaInfo> miL = new ArrayList<MetaInfo>();
		miL.add(MetaInfo.createFromDatabase("MYNAME", "mydesc", "myorig.my", 88888888888L, 19L, 22L));
		MetaInfoListPrinter printer = new MetaInfoListPrinter(miL);
		String s = printer.prettyPrint();
		s = s.split("\n")[2];
		String exp = " MYNAME | myorig.my     | 1972-10-25 20:21:28 |              19 |   22 | mydesc      ";
		assertEquals(exp, s);
	}

}

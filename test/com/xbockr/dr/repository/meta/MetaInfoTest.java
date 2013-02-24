package com.xbockr.dr.repository.meta;

import static org.junit.Assert.*;

import org.junit.Test;

public class MetaInfoTest {
	// only the static creation methods need to be tested for now, since
	// MetaInfo is pretty much an information container without logic
	@Test
	public void testCreateFromAdd() {
		MetaInfo mi = MetaInfo.createFromAdd("TEST", "this is a test");
		assertEquals("TEST", mi.getName());
		assertEquals("this is a test", mi.getDescription());
	}
	@Test
	public void testCreateFromDatabase() {
		MetaInfo mi = MetaInfo.createFromDatabase("TEST", "this is a test","originalName", 123412341234L,12L,1234L);
		assertEquals("TEST", mi.getName());
		assertEquals("this is a test", mi.getDescription());
		assertEquals("originalName", mi.getOriginalName());
		assertEquals(123412341234L, mi.getTimestamp().longValue());
		assertEquals(12L, mi.getNumberOfFiles().longValue());
		assertEquals(1234L, mi.getSize().longValue());
		
	}
}

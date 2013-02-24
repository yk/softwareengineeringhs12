package com.xbockr.dr.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HelpMsgPrinterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		String hm = HelpMsg.getHelpMsg();
		String exp = "data-repository version 0.5\n"
				+ "usage:	data-repository	add [-n name] [-d description] [-m] REPOSITORY DATASET\n"
				+ "			export REPOSITORY DATASETNAME EXPORTPATH\n"
				+ "			delete REPOSITORY DATASETNAME\n"
				+ "			replace [-d description] [-m] REPOSITORY DATASETNAME DATASET\n"
				+ "			list [-p] REPOSITORY \n"
				+ "			server REPOSITORY PROPERTYFILE\n"
				 + "			help\n" + "\n"
				+ "REPOSITORY:		repository path\n"
				+ "DATASET:		file/folder to add to repository\n"
				+ "DATASETNAME:		name of dataset in repository\n"
				+ "EXPORTPATH:		path to export dataset to\n"
				+ "PROPERTYFILE:	property file for server\n";
		assertEquals(exp, hm);
	}

}

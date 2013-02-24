package com.xbockr.dr.server;

import java.io.File;
import java.util.Properties;

public class NoDetection implements ICompletenessDetection {

	@Override
	public boolean isComplete(File dataSet) {
		return true;
	}

	@Override
	public void init(Properties properties) {
	}

}

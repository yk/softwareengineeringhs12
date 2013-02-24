package com.xbockr.dr.server;

import java.io.File;
import java.util.Properties;

public interface ICompletenessDetection {
	boolean isComplete(File dataSet);

	void init(Properties properties);
}

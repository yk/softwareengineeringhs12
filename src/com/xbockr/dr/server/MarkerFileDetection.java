package com.xbockr.dr.server;

import java.io.File;
import java.util.Properties;

public class MarkerFileDetection implements ICompletenessDetection {
	private String prefix;

	@Override
	public void init(Properties properties) {
		this.prefix = properties.getProperty("completeness-detection.prefix");
	}

	@Override
	public boolean isComplete(File dataSet) {
		if (dataSet.getName().startsWith(this.prefix)) {
			// marker file, don't move to repository
			return false;
		}
		File marker = new File(dataSet.getParentFile(),this.prefix + dataSet.getName());
		if (marker.exists() && marker.isFile()) {
			marker.delete();
			return true;
		} else {
			return false;
		}
	}

}

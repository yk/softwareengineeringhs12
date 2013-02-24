package com.xbockr.dr.server;

import java.io.File;
import java.util.Date;
import java.util.Properties;

public class UnchangedModificationDateDetection implements ICompletenessDetection {
	private long quietPeriod;
	
	@Override
	public void init(Properties properties){
		String quietPeriodString = properties.getProperty("completeness-detection.quiet-period-in-seconds");
		this.quietPeriod = Long.parseLong(quietPeriodString)*1000;
	}

	@Override
	public boolean isComplete(File dataSet) {
		if (dataSet.lastModified() < (new Date().getTime() - quietPeriod)) {
			return true;
		} else {
			return false;
		}	
	}

}

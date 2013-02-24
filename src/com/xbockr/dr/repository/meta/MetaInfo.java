package com.xbockr.dr.repository.meta;

import java.util.Date;

import com.xbockr.dr.repository.RepositoryInputValidator;

public class MetaInfo {
	public static MetaInfo createFromAdd(String name, String description) {
		RepositoryInputValidator.checkArgName(name);
		RepositoryInputValidator.checkArgDescription(description);
		MetaInfo metaInfo = new MetaInfo(name, description, "",
				new Date().getTime(), 0L, 0L);
		return metaInfo;
	}

	private String name, description, originalName;
	private Long timestamp, numberOfFiles, size;

	protected MetaInfo(String name, String description, String originalName,
			Long timestamp, Long numberOfFiles, Long size) {
		super();
		this.name = name;
		this.description = description;
		this.originalName = originalName;
		this.timestamp = timestamp;
		this.numberOfFiles = numberOfFiles;
		this.size = size;
	}

	
	public String getName() {
		return name;
	}

	
	public String getDescription() {
		return description;
	}

	
	public String getOriginalName() {
		return originalName;
	}

	
	public Long getTimestamp() {
		return timestamp;
	}

	
	public Long getNumberOfFiles() {
		return numberOfFiles;
	}

	
	public Long getSize() {
		return size;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	
	public void setDescription(String description) {
		this.description = description;
	}

	
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	
	public void setNumberOfFiles(Long numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}

	
	public void setSize(Long size) {
		this.size = size;
	}
	
	public static MetaInfo createFromDatabase(String name,String description,String originalName,long timestamp, long numberOfFiles, long size){
		return new MetaInfo(name, description, originalName, timestamp, numberOfFiles, size);
	}

}

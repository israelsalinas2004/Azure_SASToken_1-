package com.example.models;

import java.io.Serializable;
import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;


//@Getter
//@Setter
//@ToString
//@NoArgsConstructor
public class AzureFileItem {
	
	//Simply add @NoArgsConstructor does not help you, an error will show:
    private String fileName;
    private OffsetDateTime createdOn;
    private OffsetDateTime lastModified;
  
    
    public AzureFileItem(){}
    
    
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public OffsetDateTime getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(OffsetDateTime createdOn) {
		this.createdOn = createdOn;
	}
	public OffsetDateTime getLastModified() {
		return lastModified;
	}
	public void setLastModified(OffsetDateTime lastModified) {
		this.lastModified = lastModified;
	}
	
    
}

package com.akjava.gwt.templatetext.client;

import java.util.ArrayList;
import java.util.List;

public class TemplateStoreData {
public static final int TYPE_SINGLE=0;
public static final int TYPE_PAIR=1;
public static final int TYPE_FIRT_KEY=2;
private boolean multiOutput;
private int inputType;

private List<TemplateData> datas=new ArrayList<TemplateData>();

public List<TemplateData> getDatas() {
	return datas;
}
public TemplateData get(int index){
	return datas.get(index);
}
public int size(){
	return datas.size();
}
public void remove(TemplateData data){
	datas.remove(data);
}
public void add(TemplateData data){
	datas.add(data);
}


public static class TemplateData{
	private String header;
	private String footer;
	private String row;
	private String fileName;
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getFooter() {
		return footer;
	}
	public void setFooter(String footer) {
		this.footer = footer;
	}
	public String getRow() {
		return row;
	}
	public void setRow(String row) {
		this.row = row;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}

public boolean isMultiOutput() {
	return multiOutput;
}
public void setMultiOutput(boolean multiOutput) {
	this.multiOutput = multiOutput;
}
public int getInputType() {
	return inputType;
}
public void setInputType(int inputType) {
	this.inputType = inputType;
}



}

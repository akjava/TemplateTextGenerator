package com.akjava.gwt.templatetext.client;

public class TemplateStoreData {
public static final int TYPE_SINGLE=0;
public static final int TYPE_PAIR=1;
public static final int TYPE_FIRT_KEY=2;
private boolean multiOutput;
private int inputType;
private String header;
private String footer;
private String row;
private String fileName;
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

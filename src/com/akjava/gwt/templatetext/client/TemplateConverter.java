package com.akjava.gwt.templatetext.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.html5.client.file.ui.FileNameAndText;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.TemplateUtils;
import com.google.common.collect.Lists;

/**
 * TODO independ separator
 * @author aki
 *
 */
public class TemplateConverter {
	public static List<FileNameAndText> convertAsLine(String header,String footer,String row,String input,String fileName,boolean multiple) {
		if(multiple){
			return convertAsLineMulti(header, footer, row, input,fileName);
		}else{
			FileNameAndText fn=convertAsLineSingle(header, footer, row, input,"");
			if(fn==null){
				return new ArrayList<FileNameAndText>();
			}else{
				return Lists.newArrayList(fn);
			}
		}
	}
	
	public static List<FileNameAndText> convertAsPair(String header,String footer,String row,String input,String fileName,boolean multiple) {
		if(multiple){
			return convertAsPairMulti(header, footer, row, input,fileName);
		}else{
			FileNameAndText fn=convertAsPairSingle(header, footer, row, input,"");
			if(fn==null){
				return new ArrayList<FileNameAndText>();
			}else{
				return Lists.newArrayList(fn);
			}
		}
	}
	
	public static List<FileNameAndText> convertAsFirstKey(String header,String footer,String row,String input,String fileName,boolean multiple) {
		if(multiple){
			return convertAsFirstKeyMulti(header, footer, row, input,fileName);
		}else{
			FileNameAndText fn=convertAsFirstKeySingle(header, footer, row, input,"");
			if(fn==null){
				return new ArrayList<FileNameAndText>();
			}else{
				return Lists.newArrayList(fn);
			}
		}
	}
	
	public static List<FileNameAndText> convertAsFirstKeyMulti(String header,String footer,String row,String input,String fileName) {
		String template=header+row+footer;
		List<FileNameAndText> result=new ArrayList<FileNameAndText>();
		
		List<List<String>> csvs=CSVUtils.csvToListList(input, true, false);
		if(csvs.size()==0){
			return new ArrayList<FileNameAndText>();
		}
		List<String> keys=csvs.get(0);
		for(int i=1;i<csvs.size();i++){
			List<String> csv=csvs.get(i);
			Map<String,String> map=new HashMap<String, String>();
			for(int j=0;j<keys.size();j++){
				String key=keys.get(j);
				String value="";
				if(csv.size()>j){
					value=csv.get(j);
				}
				map.put(key, value);
			}
			String name=TemplateUtils.createAdvancedText(fileName, map);
			result.add(new FileNameAndText(name, TemplateUtils.createAdvancedText(template, map)));
		}
		return result;
	}
	public static FileNameAndText convertAsPairSingle(String header,String footer,String row,String input,String fileName) {
		if(input.isEmpty()){
			return null;
		}
		String[] lines=CSVUtils.splitLines(input);
		Map<String,String> map=new HashMap<String, String>();
		for(String line:lines){
			if(line.isEmpty()){
				continue;
			}
			String[] csv=CSVUtils.splitAtFirst(line, "\t");
			if(csv.length==2){
				map.put(csv[0], csv[1]);
			}else{
				map.put(csv[0], "");
			}
		}
		
		String template=header+row+footer;
		String name=TemplateUtils.createAdvancedText(fileName,map);
		return new FileNameAndText(name,TemplateUtils.createAdvancedText(template, map));
		
	}
	public static List<FileNameAndText> convertAsPairMulti(String header,String footer,String row,String input,String fileName) {
		if(input.isEmpty()){
			return new ArrayList<FileNameAndText>();
		}
		List<FileNameAndText> result=new ArrayList<FileNameAndText>();
		String[] lines=CSVUtils.splitLines(input);
		
		for(String line:lines){
			Map<String,String> map=new HashMap<String, String>();
			if(line.isEmpty()){
				continue;
			}
			String[] csv=CSVUtils.splitAtFirst(line, "\t");
			if(csv.length==2){
				map.put(csv[0], csv[1]);
			}else{
				map.put(csv[0], "");
			}
			String template=header+row+footer;
			String name=TemplateUtils.createAdvancedText(fileName,map);
			result.add(new FileNameAndText(name,TemplateUtils.createAdvancedText(template, map)));
		}
		
		return result;
	}
	public static FileNameAndText convertAsLineSingle(String header,String footer,String row,String input,String fileName) {
		if(input.isEmpty()){
			return null;
		}
		List<String> lines=CSVUtils.splitLinesWithGuava(input);
		String template=row;
		String body="";
		for(String line:lines){
			if(line.isEmpty()){
				continue;
			}
			
			Map<String,String> map=new HashMap<String, String>();
			map.put("value", line);
			body+=TemplateUtils.createAdvancedText(template, map);
			}
	return new FileNameAndText(fileName,header+body+footer);
	}
	public static List<FileNameAndText> convertAsLineMulti(String header,String footer,String row,String input,String fileName) {
		if(input.isEmpty()){
			return new ArrayList<FileNameAndText>();
		}
		List<FileNameAndText> result=new ArrayList<FileNameAndText>();
		List<String> lines=CSVUtils.splitLinesWithGuava(input);
		String template=header+row+footer;
		
		for(String line:lines){
			if(line.isEmpty()){
				continue;
			}
			
			Map<String,String> map=new HashMap<String, String>();
			map.put("value", line);
			String name=TemplateUtils.createAdvancedText(fileName, map);
			String body=TemplateUtils.createAdvancedText(template, map);
			result.add(new FileNameAndText(name,body));
			}
	return result;
	}
	public static FileNameAndText convertAsFirstKeySingle(String header,String footer,String row,String input,String fileName) {
		String template=row;
		String body="";
		
		List<List<String>> csvs=CSVUtils.csvToListList(input, true, false);
		
		if(csvs.size()==0){
			return null;
		}
	
		
		List<String> keys=csvs.get(0);
		for(int i=1;i<csvs.size();i++){
			List<String> csv=csvs.get(i);
			Map<String,String> map=new HashMap<String, String>();
			for(int j=0;j<keys.size();j++){
				String key=keys.get(j);
				String value="";
				if(csv.size()>j){
					value=csv.get(j);
				}
				map.put(key, value);
			}
			body+=TemplateUtils.createAdvancedText(template, map);
		}
		
		return new FileNameAndText(fileName,header+body+footer);
	}
}

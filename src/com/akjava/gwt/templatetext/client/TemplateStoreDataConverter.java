package com.akjava.gwt.templatetext.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.templatetext.client.TemplateStoreData.TemplateData;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;

public class TemplateStoreDataConverter extends Converter<TemplateStoreData,String>{
	public static final String META_HEADER="##HEADER";
	public static final String META_FOOTER="##FOOTER";
	public static final String META_ROW="##ROW";
	public static final String META_FILENAME="##FILENAME,";
	public static final String META_MULTI="##MULTI,";
	public static final String META_INPUT="##INPUT,";
	
	@Override
	protected TemplateStoreData doBackward(String text) {
		List<String> lines=CSVUtils.splitLinesWithGuava(text);
		String tmp=null;
		String lastMeta=null;
		TemplateStoreData result=new TemplateStoreData();
		TemplateData data=null;
		for(String line:lines){
			if(line.startsWith(META_MULTI)){
				boolean multi=ValuesUtils.toBoolean(line.substring(META_MULTI.length()), false);
				result.setMultiOutput(multi);
			}else if(line.startsWith(META_INPUT)){
				int input=ValuesUtils.toInt(line.substring(META_INPUT.length()), 0);
				result.setInputType(input);
			}else if(line.startsWith(META_FILENAME)){
				if(tmp!=null && lastMeta!=null){
					textToStoreDataSetValue(data,lastMeta,tmp);
					tmp=null;
				}
				lastMeta=null;
				
				String name=line.substring(META_FILENAME.length());
				
				data=new TemplateData();
				result.add(data);
				data.setFileName(name);
				
			}else if(line.equals(META_HEADER)){
				if(tmp!=null && lastMeta!=null){
					textToStoreDataSetValue(data,lastMeta,tmp);
					tmp=null;
				}
				lastMeta=META_HEADER;
			}else if(line.equals(META_FOOTER)){
				if(tmp!=null && lastMeta!=null){
					textToStoreDataSetValue(data,lastMeta,tmp);
					tmp=null;
				}
				lastMeta=META_FOOTER;
			}else if(line.equals(META_ROW)){
				if(tmp!=null && lastMeta!=null){
					textToStoreDataSetValue(data,lastMeta,tmp);
					tmp=null;
				}
				lastMeta=META_ROW;
			}else{
				if(tmp==null){
					tmp=line;
				}else{
					tmp+="\r\n"+line;
				}
			}
		}
		if(tmp!=null && lastMeta!=null){
			textToStoreDataSetValue(data,lastMeta,tmp);
		}
		
		return result;
	}

	@Override
	protected String doForward(TemplateStoreData storedata) {
		List<String> values=new ArrayList<String>();
		values.add(META_MULTI+""+storedata.isMultiOutput());
		values.add(META_INPUT+""+storedata.getInputType());
		
		for(int i=0;i<storedata.size();i++){
		TemplateData data=storedata.get(i);
		values.add(META_FILENAME+""+(data.getFileName()==null?"":data.getFileName()));
		values.add(META_HEADER);
		values.add(data.getHeader()==null?"":data.getHeader());
		values.add(META_FOOTER);
		values.add(data.getFooter()==null?"":data.getFooter());
		values.add(META_ROW);
		values.add(data.getRow()==null?"":data.getRow());
		}
		
		System.out.println(Joiner.on("\r\n").join(values));
		
		return Joiner.on("\r\n").join(values);
	}
	
	public static void textToStoreDataSetValue(TemplateData data,String meta,String value){
		if(meta.equals(META_HEADER)){
			data.setHeader(value);
		}else if(meta.equals(META_FOOTER)){
			data.setFooter(value);
		}else if(meta.equals(META_ROW)){
			data.setRow(value);
		}
	}

}

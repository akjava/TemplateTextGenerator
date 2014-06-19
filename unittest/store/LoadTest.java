package store;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import com.akjava.gwt.templatetext.client.TemplateStoreData;
import com.akjava.gwt.templatetext.client.TemplateStoreData.TemplateData;
import com.akjava.gwt.templatetext.client.TemplateStoreDataConverter;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class LoadTest extends TestCase{

	
	public String loadTest(String name){
	URL url = Resources.getResource("store/"+name);
	try {
		return Resources.toString(url, Charsets.UTF_8);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	return null;
	}
	
	public void test1(){
		String expected=loadTest("test1.txt");
		TemplateStoreData data=new TemplateStoreData();
		data.add(new TemplateData());
		assertEquals(expected,new TemplateStoreDataConverter().convert(data));
	}
	
	public void test2(){
		String expected=loadTest("test2.txt");
		TemplateStoreData data=new TemplateStoreData();
		data.setMultiOutput(true);
		data.setInputType(TemplateStoreData.TYPE_PAIR);
		
		TemplateData child=new TemplateData();
		data.add(child);
		child.setFileName("test.txt");
		child.setHeader("hello\r\nhello");
		child.setFooter("foot toot");
		child.setRow("rowrow");
		assertEquals(expected,new TemplateStoreDataConverter().convert(data));
	}
	
	//multiple empty
	public void test4(){
		String expected=loadTest("test4.txt");
		TemplateStoreData data=new TemplateStoreData();
		data.add(new TemplateData());
		data.add(new TemplateData());
		assertEquals(expected,new TemplateStoreDataConverter().convert(data));
	}
	
	public void test5(){
		String expected=loadTest("test5.txt");
		TemplateStoreData data=new TemplateStoreData();
		data.setMultiOutput(true);
		data.setInputType(TemplateStoreData.TYPE_SINGLE);
		for(int i=1;i<=3;i++){
		TemplateData child=new TemplateData();
		data.add(child);
		child.setFileName(i+".txt");
		child.setHeader("head"+i);
		child.setFooter("foot"+i);
		child.setRow("row"+i);
		}
		assertEquals(expected,new TemplateStoreDataConverter().convert(data));
	}
	
	public void test6(){
		String expected=loadTest("test6.txt");
		TemplateStoreData data=new TemplateStoreData();
		data.setMultiOutput(true);
		data.setInputType(TemplateStoreData.TYPE_SINGLE);
		for(int i=1;i<=3;i++){
		TemplateData child=new TemplateData();
		data.add(child);
		//child.setFileName(i+".txt");
		//child.setHeader("head"+i);
		//child.setFooter("foot"+i);
		child.setRow("row"+i);
		}
		assertEquals(expected,new TemplateStoreDataConverter().convert(data));
	}
}

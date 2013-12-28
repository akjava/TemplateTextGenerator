package store;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import com.akjava.gwt.templatetext.client.TemplateStoreData;
import com.akjava.gwt.templatetext.client.TemplateStoreDataBuilder;
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
		assertEquals(expected,TemplateStoreDataBuilder.toStoreText(data));
	}
	
	public void test2(){
		String expected=loadTest("test2.txt");
		TemplateStoreData data=new TemplateStoreData();
		data.setMultiOutput(true);
		data.setInputType(TemplateStoreData.TYPE_PAIR);
		data.setFileName("test.txt");
		data.setHeader("hello\r\nhello");
		data.setFooter("foot toot");
		data.setRow("rowrow");
		assertEquals(expected,TemplateStoreDataBuilder.toStoreText(data));
	}
}

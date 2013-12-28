package converter;

import java.util.List;

import com.akjava.gwt.html5.client.file.ui.FileNameAndText;
import com.akjava.gwt.templatetext.client.TemplateConverter;

import junit.framework.TestCase;

public class AsLineTest extends TestCase{
	

	/**
	 * empty input return null;
	 */
	public void testEmptySingle(){
		//String line="";
		FileNameAndText result=TemplateConverter.convertAsLineSingle("header", "footer", "row", "", "filename");
		assertTrue(result==null);
	}

	/**
	 * 
	 */
	public void testSingle1(){
		String header="header";
		String footer="footer";
		String row="row";
		String input="input";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsLineSingle(header,footer,row,input,fileName);
		assertEquals("filename:"+"headerrowfooter",result.toString());
	}
	
	/**
	 * 
	 */
	public void testSingle2(){
		String header="header";
		String footer="footer";
		String row="row";
		String input="input\n" +
				"input";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsLineSingle(header,footer,row,input,fileName);
		assertEquals("filename:"+"headerrowrowfooter",result.toString());
	}
	
	/**
	 * 
	 */
	public void testSingle3(){
		String header="header";
		String footer="footer";
		String row="${value}";
		String input="input\n" +
				"hello";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsLineSingle(header,footer,row,input,fileName);
		assertEquals("filename:"+"headerinputhellofooter",result.toString());
	}
	
	/**
	 *only replace ${value} line mode but not effect on header,footer,filename when single mode
	 */
	public void testSingle4(){
		String header="${value}";
		String footer="${value}";
		String row="${value}";
		String input="input\n" +
				"hello";
		String fileName="${value}";
		FileNameAndText result=TemplateConverter.convertAsLineSingle(header,footer,row,input,fileName);
		assertEquals("${value}:"+"${value}inputhello${value}",result.toString());
	}
	
	/**
	 * single line can contain separator(tab)
	 */
	public void testSingle5(){
		String header="header";
		String footer="footer";
		String row="tab\tline";
		String input="input";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsLineSingle(header,footer,row,input,fileName);
		assertEquals("filename:"+"header" +
				"tab\tline" +
				"footer",result.toString());
	}
	
	/**
	 * empty input return empty list;
	 */
	public void testEmptyMulti(){
		//String line="";
		List<FileNameAndText> result=TemplateConverter.convertAsLineMulti("header", "footer", "row", "", "filename");
		assertTrue(result.size()==0);
	}
	
	/**
	 * 
	 */
	public void testMulti1(){
		String header="header";
		String footer="footer";
		String row="row";
		String input="input";
		String fileName="filename";
		assertTrue(TemplateConverter.convertAsLineMulti(header,footer,row,input,fileName).size()==1);
	}
	
	/**
	 *
	 * 
	 */
	public void testMulti2(){
		String header="header";
		String footer="footer";
		String row="row";
		String input="input\n" +
				"input";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsLineMulti(header,footer,row,input,fileName).get(1);
		assertEquals("filename:"+"headerrowfooter",result.toString());
	}
	
	/**
	 * 1 line is used as key and after that use it as row
	 * 
	 */
	public void testMulti3(){
		String header="header";
		String footer="footer";
		String row="${value}";
		String input="input\n" +
				"hello";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsLineMulti(header,footer,row,input,fileName).get(1);
		assertEquals("filename:"+"headerhellofooter",result.toString());
	}
	
	/**
	 * 1 line is used as key and after that use it as row
	 * replace header and footer and fileName
	 */
	public void testMulti4(){
		String header="${value}";
		String footer="${value}";
		String row="${value}";
		String input="input\n" +
				"hello";
		String fileName="${value}";
		FileNameAndText result=TemplateConverter.convertAsLineMulti(header,footer,row,input,fileName).get(1);
		assertEquals("hello:"+"hellohellohello",result.toString());
	}
	
	/**
	 * single line can contain separator
	 */
	public void testMulti5(){
		String header="header";
		String footer="footer";
		String row="tab\tline";
		String input="input";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsLineMulti(header,footer,row,input,fileName).get(0);
		assertEquals("filename:"+"header" +
				"tab\tline" +
				"footer",result.toString());
	}

	
}

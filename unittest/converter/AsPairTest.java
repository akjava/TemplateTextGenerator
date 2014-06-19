package converter;

import java.util.List;

import com.akjava.gwt.html5.client.file.ui.FileNameAndText;
import com.akjava.gwt.templatetext.client.TemplateConverter;

import junit.framework.TestCase;

public class AsPairTest extends TestCase{
	

	/**
	 * empty input return null;
	 */
	public void testEmptySingle(){
		//String line="";
		FileNameAndText result=TemplateConverter.convertAsPairSingle("header", "footer", "row", "", "filename");
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
		FileNameAndText result=TemplateConverter.convertAsPairSingle(header,footer,row,input,fileName);
		assertEquals("filename:"+"headerrowfooter",result.toString());
	}
	
	/**
	 * 
	 */
	public void testSingle2(){
		String header="header";
		String footer="footer";
		String row="${value}";
		String input="value"; 
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsPairSingle(header,footer,row,input,fileName);
		assertEquals("filename:"+"headerfooter",result.toString());
	}
	
	/**
	 * 
	 */
	public void testSingle3(){
		String header="header";
		String footer="footer";
		String row="${input}";
		String input="input\t" +
				"hello";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsPairSingle(header,footer,row,input,fileName);
		assertEquals("filename:"+"headerhellofooter",result.toString());
	}
	
	/**
	 *only replace ${value} line mode but not effect on header,footer,filename when single mode
	 */
	public void testSingle4(){
		String header="${value}";
		String footer="${value}";
		String row="${value}";
		String input="value\t" +
				"hello";
		String fileName="${value}";
		FileNameAndText result=TemplateConverter.convertAsPairSingle(header,footer,row,input,fileName);
		assertEquals("hello:"+"hellohellohello",result.toString());
	}
	
	/**
	 * pair can contain separator(tab)
	 */
	public void testSingle5(){
		String header="header";
		String footer="footer";
		String row="${tab}";
		String input="tab\tline\tline2";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsPairSingle(header,footer,row,input,fileName);
		assertEquals("filename:"+"header" +
				"line\tline2" +
				"footer",result.toString());
	}
	
	/**
	 * empty input return empty list;
	 */
	public void testEmptyMulti(){
		//String line="";
		List<FileNameAndText> result=TemplateConverter.convertAsPairMulti("header", "footer", "row", "", "filename");
		assertTrue(result.size()==0);
	}
	
	/**
	 * pair is always create 
	 */
	public void testMulti1(){
		String header="header";
		String footer="footer";
		String row="row";
		String input="input\ninput";
		String fileName="filename";
		assertTrue(TemplateConverter.convertAsPairMulti(header,footer,row,input,fileName).size()==2);
	}
	
	/**
	 *
	 * 
	 */
	public void testMulti2(){
		String header="header";
		String footer="footer";
		String row="row";
		String input="input\tinput\n" +
				"input\tinput";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsPairMulti(header,footer,row,input,fileName).get(1);
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
		String input="value\n" +
				"hello";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsPairMulti(header,footer,row,input,fileName).get(1);
		assertEquals("filename:"+"header${value}footer",result.toString());
	}
	
	/**
	 * 1 line is used as key and after that use it as row
	 * replace header and footer and fileName
	 */
	public void testMulti4(){
		String header="${value}";
		String footer="${value}";
		String row="${value}";
		String input="value\t" +
				"hello";
		String fileName="${value}";
		FileNameAndText result=TemplateConverter.convertAsPairMulti(header,footer,row,input,fileName).get(0);
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
		FileNameAndText result=TemplateConverter.convertAsPairMulti(header,footer,row,input,fileName).get(0);
		assertEquals("filename:"+"header" +
				"tab\tline" +
				"footer",result.toString());
	}

	
}

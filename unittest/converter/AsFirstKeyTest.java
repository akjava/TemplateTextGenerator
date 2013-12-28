package converter;

import java.util.List;

import junit.framework.TestCase;

import com.akjava.gwt.html5.client.file.ui.FileNameAndText;
import com.akjava.gwt.templatetext.client.TemplateConverter;

public class AsFirstKeyTest extends TestCase {

	/**
	 * empty input return null;
	 */
	public void testEmptySingle(){
		//String line="";
		FileNameAndText result=TemplateConverter.convertAsFirstKeySingle("header", "footer", "row", "", "filename");
		assertTrue(result==null);
	}

	/**
	 * 1 line is used as key and not contain row
	 * single type not effect fileName
	 */
	public void testSingle1(){
		String header="header";
		String footer="footer";
		String row="row";
		String input="input";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsFirstKeySingle(header,footer,row,input,fileName);
		assertEquals("filename:"+"headerfooter",result.toString());
	}
	
	/**
	 * 1 line is used as key and after that use it as row
	 * single type not effect fileName
	 */
	public void testSingle2(){
		String header="header";
		String footer="footer";
		String row="row";
		String input="input\n" +
				"input";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsFirstKeySingle(header,footer,row,input,fileName);
		assertEquals("filename:"+"headerrowfooter",result.toString());
	}
	
	/**
	 * 1 line is used as key and after that use it as row
	 * single type not effect fileName
	 */
	public void testSingle3(){
		String header="header";
		String footer="footer";
		String row="${input}";
		String input="input\n" +
				"hello";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsFirstKeySingle(header,footer,row,input,fileName);
		assertEquals("filename:"+"headerhellofooter",result.toString());
	}
	
	/**
	 * 1 line is used as key and after that use it as row
	 * single type not effect fileName
	 * no effect on header and footer
	 */
	public void testSingle4(){
		String header="${input}";
		String footer="${input}";
		String row="${input}";
		String input="input\n" +
				"hello";
		String fileName="${input}";
		FileNameAndText result=TemplateConverter.convertAsFirstKeySingle(header,footer,row,input,fileName);
		assertEquals("${input}:"+"${input}hello${input}",result.toString());
	}
	
	/**
	 * empty input return empty list;
	 */
	public void testEmptyMulti(){
		//String line="";
		List<FileNameAndText> result=TemplateConverter.convertAsFirstKeyMulti("header", "footer", "row", "", "filename");
		assertTrue(result.size()==0);
	}
	
	/**
	 * 1 line is used as key and not converted anything
	 */
	public void testMulti1(){
		String header="header";
		String footer="footer";
		String row="row";
		String input="input";
		String fileName="filename";
		assertTrue(TemplateConverter.convertAsFirstKeyMulti(header,footer,row,input,fileName).size()==0);
	}
	
	/**
	 * 1 line is used as key and after that use it as row
	 * 
	 */
	public void testMulti2(){
		String header="header";
		String footer="footer";
		String row="row";
		String input="input\n" +
				"input";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsFirstKeyMulti(header,footer,row,input,fileName).get(0);
		assertEquals("filename:"+"headerrowfooter",result.toString());
	}
	
	/**
	 * 1 line is used as key and after that use it as row
	 * 
	 */
	public void testMulti3(){
		String header="header";
		String footer="footer";
		String row="${input}";
		String input="input\n" +
				"hello";
		String fileName="filename";
		FileNameAndText result=TemplateConverter.convertAsFirstKeyMulti(header,footer,row,input,fileName).get(0);
		assertEquals("filename:"+"headerhellofooter",result.toString());
	}
	
	/**
	 * 1 line is used as key and after that use it as row
	 * replace header and footer and fileName
	 */
	public void testMulti4(){
		String header="${input}";
		String footer="${input}";
		String row="${input}";
		String input="input\n" +
				"hello";
		String fileName="${input}";
		FileNameAndText result=TemplateConverter.convertAsFirstKeyMulti(header,footer,row,input,fileName).get(0);
		assertEquals("hello:"+"hellohellohello",result.toString());
	}
	
}

package com.akjava.gwt.templatetext.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageDataList;
import com.akjava.gwt.lib.client.datalist.SimpleDataListItemControler;
import com.akjava.gwt.lib.client.datalist.SimpleTextData;
import com.akjava.gwt.lib.client.datalist.functions.CsvArrayToHeadAndValueFunction;
import com.akjava.gwt.lib.client.datalist.functions.HeadAndValueToCsvFunction;
import com.akjava.lib.common.csv.CSVReader;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class TemplateDataList extends SimpleDataListItemControler{
private TemplateTextGenerator generator;
private SimpleTextData copiedValue;
private HorizontalPanel uploadPanel;
private FileUploadForm uploadForm;
	public TemplateDataList(StorageDataList dataList,TemplateTextGenerator generator) {
		super(dataList);
		this.generator=generator;
	}

	@Override
	public SimpleTextData createSaveData(String fileName) {
		String text=generator.getText();
		SimpleTextData current=getSimpleDataListWidget().getSelection().getData();
		if(current==null){
		return new SimpleTextData(-1,fileName,text);
		}else{
			return new SimpleTextData(-1,fileName,text,current.getCdate());	//copy cdate
		}
	}

	@Override
	public SimpleTextData createNewData(String fileName) {
		return new SimpleTextData(-1,fileName,"");
	}

	@Override
	public void loadData(Optional<SimpleTextData> hv) {
		if(hv.isPresent()){
			//add url
			
			generator.setUrl(hv.get().getId());
			generator.loadTemplateText(hv.get().getData());
			generator.doConvert();
		}
	}

	@Override
	public void exportDatas(List<SimpleTextData> list) {
		String exportText=generateExportText(list);
		Anchor anchor=new HTML5Download().generateTextDownloadLink(exportText,getKeyName()+".csv","Download data",true);
		getSimpleDataListWidget().add(anchor);
	}
	
	private String getKeyName() {
		return getDataList().getKey();
	}

	private String generateExportText(List<SimpleTextData> list){
		List<String> lines=FluentIterable.from(list).transform(new HeadAndValueToCsvFunction()).toList();
		String exportText=Joiner.on("\r\n").join(lines);
		return exportText;
	}
	
	public String generateExportText(){
		List<SimpleTextData> list=this.getDataList().getDataList();
		return generateExportText(list);
	}

	@Override
	public void importData() {
		if(getDataList().getDataList().size()>0){
		boolean confirm=Window.confirm("Import datas:add current datas with ignoring id,if you keep id use restore and replace all data.");
		if(!confirm){
			return;
		}
		}
		
		uploadForm = FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				List<SimpleTextData> list=textToSimpleTextData(value);
				for(SimpleTextData hv:list){
					getDataList().addData(hv.getName(), hv.getData());
				}
				
				uploadPanel.removeFromParent();
				updateList();
			}
		}, false);
		
		
		createFormPanel("Import Datas:",uploadForm);
		getSimpleDataListWidget().add(uploadPanel);
	}

	@Override
	public void clearAll() {
		boolean confirm=Window.confirm("Clear all data?this operation can not undo\nyou should ExportAll first.");
		if(!confirm){
			return;
		}
		List<SimpleTextData> hvs= getDataList().getDataList();
		for(SimpleTextData hv:hvs){
			getDataList().clearData(hv.getId());
		}
		getDataList().setCurrentId(0);//restart id
		
		getSimpleDataListWidget().unselect();
		updateList();
		
	}

	@Override
	public void copy(Object object) {
		copiedValue=(SimpleTextData) object;
	}

	@Override
	public void paste() {
		if(copiedValue!=null){
		getDataList().addData(copiedValue.getName()+" copy", copiedValue.getData());
		updateList();
		}
	}

	@Override
	public void recoverLastSaved(SimpleTextData hv) {
		copy(hv);
		paste();
	}

	/**
	 * restore keep id
	 */
	@Override
	public void restore() {
		if(getDataList().getDataList().size()>0){
			boolean confirm=Window.confirm("Restore datas:clear current datas and restore csv datas.");
			if(!confirm){
				return;
			}
			}
		
		uploadForm = FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String value) {
				execRestore(value);
			}
		}, false);
		
		
		createFormPanel("Restore layers:",uploadForm);
		getSimpleDataListWidget().add(uploadPanel);
		
	}
	public void execRestore(String value){
		//clear first
		List<SimpleTextData> hvs= getDataList().getDataList();
		for(SimpleTextData hv:hvs){
			getDataList().clearData(hv.getId());
		}
		//getDataList().setCurrentId(0);
		
		int max=0;
		
		List<SimpleTextData> list=textToSimpleTextData(value);
		
		//do offset
		//LogUtils.log("force offset");
		//doOffset(parts.getKey(),list);
		
		GWT.log("upload-size:"+list.size());
		for(SimpleTextData hv:list){
			if(hv.getId()>max){
				max=hv.getId();
			}
			getDataList().updateData(hv.getId(),hv.getName(), hv.getData());
		}
		
		getDataList().setCurrentId(max+1);
		
		if(uploadPanel!=null){
			uploadPanel.removeFromParent();
		}
		updateList();
	}

	@Override
	public void doDoubleClick(int clientX, int clientY) {
		LogUtils.log("double-click:"+clientX+","+clientY);
	}
	
	private void createFormPanel(String text,FileUploadForm form){
		if(uploadPanel==null){
		uploadPanel=new HorizontalPanel();
		uploadPanel.add(new Label(text));
		uploadPanel.add(form);
		Button bt=new Button("close",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				uploadPanel.setVisible(false);
			}
		});
		uploadPanel.add(bt);
		}else{
			uploadPanel.setVisible(true);
		}
	}


	//TODO move somewhere
	public static List<SimpleTextData> textToSimpleTextData(String text){
		//List<SimpleTextData> list=new ArrayList<SimpleTextData>();
		CSVReader reader=new CSVReader(text,'\t','"',true);
		try {
			List<String[]> csvs=reader.readAll();
			return FluentIterable.from(csvs).transform(new CsvArrayToHeadAndValueFunction()).toList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Window.alert(e.getMessage());
		}
		return null;
	}

}

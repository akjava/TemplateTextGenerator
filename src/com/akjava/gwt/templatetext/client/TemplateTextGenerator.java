package com.akjava.gwt.templatetext.client;

import java.util.List;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.ui.FileNameAndText;
import com.akjava.gwt.html5.client.file.ui.FileNameAndTextCell;
import com.akjava.gwt.html5.client.file.webkit.DirectoryCallback;
import com.akjava.gwt.html5.client.file.webkit.FileEntry;
import com.akjava.gwt.html5.client.file.webkit.FilePathCallback;
import com.akjava.gwt.html5.client.file.webkit.Item;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageDataList;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.widget.PasteValueReceiveArea;
import com.akjava.gwt.lib.client.widget.TabInputableTextArea;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TemplateTextGenerator implements EntryPoint {


	private static final String KEY_TEMPLATE_LAST_DATA = "TemplateTextGenerate_LAST_DATA";
	private static final String KEY_TEMPLATE_LAST_INPUT = "TemplateTextGenerate_LAST_INPUT";
	StorageControler storageControler=new StorageControler();
	private TextBox fileNameBox;
	private TextArea headerArea;
	private TextArea footerArea;
	private TextArea rowArea;
	private TextArea templateArea;
	private TextArea outputArea;
	private TabInputableTextArea inputArea;
	private Label descriptionLabel;
	private RadioButton singleBt;
	private RadioButton pairBt;
	private RadioButton firstBt;
	private SingleSelectionModel<FileNameAndText> selectionModel;
	private CellList<FileNameAndText> cellList;
	private CheckBox multiCheck;
	VerticalPanel downloadLinks=new VerticalPanel();
	private TemplateDataList templateDataList;
	public void onModuleLoad() {
		HorizontalPanel root=new HorizontalPanel();
		RootPanel.get().add(root);
		
		VerticalPanel leftVertical=new VerticalPanel();
		root.add(leftVertical);
		createLeftPanels(leftVertical);
		
		
		 
		
		 
		 //right
		 VerticalPanel rightVertical=new VerticalPanel();
		 root.add(rightVertical);
		 createRightPanels(rightVertical);
		 
		//center file list
		 VerticalPanel centerVertical=new VerticalPanel();
		 root.add(centerVertical);
		 
		 ScrollPanel scroll=new ScrollPanel();
		 scroll.setSize("300px", "800px");
		 
		 FileNameAndTextCell cell=new FileNameAndTextCell();
		 cellList = new CellList<FileNameAndText>(cell);
		 cellList.setPageSize(900);
		 scroll.setWidget(cellList);
		 centerVertical.add(scroll);
		 
		 selectionModel = new SingleSelectionModel<FileNameAndText>();
		 cellList.setSelectionModel(selectionModel);
		 selectionModel.addSelectionChangeHandler(new Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				downloadLinks.clear();
				FileNameAndText select=selectionModel.getSelectedObject();
				if(select!=null){
					setOutputText(select.getText());
					downloadLinks.add(select.createDownloadLink("download "+select.getName()));
				}else{
					setOutputText("");
				}
				
			}
		});
		 
		 //to avoid something initial error
		 Scheduler.get().scheduleDeferred(new Command() {
			    public void execute () {
			    	 //load data from last convert
					 String template=storageControler.getValue(KEY_TEMPLATE_LAST_DATA,"");
					 templateArea.setText(template);
					 loadTemplateText(template);
					 inputArea.setText(storageControler.getValue(KEY_TEMPLATE_LAST_INPUT,""));
					 doConvert();
			    }});
		
		 
		 templateDataList = new TemplateDataList(new StorageDataList(storageControler,"templateText"),this);
		 
		 root.add(templateDataList.getSimpleDataListWidget());
		
	}
	private void createRightPanels(Panel parentPanel) {
		TabPanel tab=new TabPanel();
		parentPanel.add(tab);
		
		VerticalPanel outputPanel=new VerticalPanel();
		outputPanel.add(downloadLinks);
		outputArea = new TextArea();
		outputArea.setSize("600px","450px");
		outputPanel.add(outputArea);
		tab.add(outputPanel,"Output");
		
		
		VerticalPanel templatePanel=new VerticalPanel();
		
		//load button
		/*
		 Button load=new Button("Load",new ClickHandler() {//really need?maybe no need use clipboard
				@Override
				public void onClick(ClickEvent event) {
					loadTemplateText(templateArea.getText());
				}
			});
			templatePanel.add(load);
			*/
		 
		 
		templateArea = new TextArea();
		templatePanel.add(templateArea);
		templateArea.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				templateArea.selectAll();
			}
		});
		templateArea.setReadOnly(true);
		templateArea.setSize("600px","450px");
		tab.add(templatePanel,"Template");
		tab.selectTab(0);
		
		VerticalPanel helps=new VerticalPanel();
		parentPanel.add(helps);
		helps.add(new Label("${value}:normal"));
		helps.add(new Label("${u+value}:upper camel"));
		helps.add(new Label("${l+value}:lower camel"));
		helps.add(new Label("${U+value}:upper case"));
		helps.add(new Label("${L+value}:upper case"));
		helps.add(new Label("${name+value}:name only of fileName"));
		helps.add(new Label("${ext+value}: ext only of fileName"));
		helps.add(new Label("${_+value}: replace ' ' & '-' to underbar"));
	}
	private void createLeftPanels(Panel parentPanel){
		//copy paste box
		PasteValueReceiveArea pasteArea=new PasteValueReceiveArea();
		 pasteArea.setStylePrimaryName("readonly");
		 pasteArea.setText("Click(Focus) & Paste Here");
		 parentPanel.add(pasteArea);
		 pasteArea.setSize("600px", "60px");
		 pasteArea.setFocus(true);
		 pasteArea.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				loadTemplateText(event.getValue());
				doConvert();
			}
			 
		});
		 
		//file name box
		HorizontalPanel fileNamesPanel=new HorizontalPanel();
		parentPanel.add(fileNamesPanel);
		fileNamesPanel.add(new Label("FileName"));
		fileNameBox = new TextBox();
		fileNameBox.setWidth("250px");
		fileNamesPanel.add(fileNameBox);
		//header box
		
		headerArea = new TextArea();
		headerArea.setSize("600px","150px");
		parentPanel.add(new Label("Header"));
		parentPanel.add(headerArea);
		
		rowArea = new TextArea();
		rowArea.setSize("600px","150px");
		parentPanel.add(new Label("Row/Body"));
		parentPanel.add(rowArea);
		
		footerArea = new TextArea();
		footerArea.setSize("600px","150px");
		parentPanel.add(new Label("Footer"));
		parentPanel.add(footerArea);
		
		
		
		//button
		 Button convert=new Button("Convert",new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					doConvert();
				}
			});
		 parentPanel.add(convert);
		 
		 parentPanel.add(new Label("Input"));
		 
		 HorizontalPanel hpanel=new HorizontalPanel();
		 parentPanel.add(hpanel);
		 singleBt = new RadioButton("type");
		 singleBt.setText("Single line");
		 hpanel.add(singleBt);
		 singleBt.setValue(true);
		 
		 pairBt = new RadioButton("type");
		 pairBt.setText("key and value pair");
		 hpanel.add(pairBt);
		 
		 firstBt = new RadioButton("type");
		 firstBt.setText("First line use as Key");
		 hpanel.add(firstBt);
		 
		 inputArea = new TabInputableTextArea();
		 inputArea.setSize("600px","150px");
		 descriptionLabel = new Label();
		 parentPanel.add(descriptionLabel);
		 parentPanel.add(inputArea);
		 
		 parentPanel.add(new Label("Output"));
		 HorizontalPanel hpanel2=new HorizontalPanel();
		 parentPanel.add(hpanel2);
		 multiCheck = new CheckBox("multi");
		 hpanel2.add(multiCheck);
		 
		 
		
		 inputArea.addDropHandler(new DropHandler() {

				@Override
				public void onDrop(DropEvent event) {

					event.preventDefault();

					inputArea.setText("");
					final FilePathCallback callback = new FilePathCallback() {
						@Override
						public void callback(File file,String path) {
							String name=file!=null?file.getFileName():"";
							String old = inputArea.getText();
							inputArea.setText(old + name+"\n");

						}
					};

					final JsArray<Item> items = FileUtils.transferToItem(event
							.getNativeEvent());
					GWT.log("length:" + items.length());
					if (items.length() > 0) {
						for (int i = 0; i < items.length(); i++) {
							

							FileEntry entry = items.get(i).webkitGetAsEntry();

							entryCallback(entry,callback,"");

						}

					}

					//doneDrop();
					doConvert();
				}
			});
		
	}
	public void entryCallback(final FileEntry entry,final FilePathCallback callback,String path){
		if (entry.isFile()) {
			entry.file(callback,path);
		} else if (entry.isDirectory()) {
			entry.getReader().readEntries(
					new DirectoryCallback() {
						@Override
						public void callback(
								JsArray<FileEntry> entries) {
							callback.callback(null, entry.getFullPath());
							for (int j = 0; j < entries
									.length(); j++) {
								entryCallback(entries.get(j),callback,entry.getFullPath());
							}
						}
					});
		}
	}
	public String getText(){
		return templateArea.getText();
	}
	public void loadTemplateText(String text) {
		if(text==null || text.isEmpty()){
			return;
		}
		TemplateStoreData data=TemplateStoreDataBuilder.textToStoreData(text);
		multiCheck.setValue(data.isMultiOutput());
		if(data.getInputType()==TemplateStoreData.TYPE_SINGLE){
			singleBt.setValue(true);
		}else if(data.getInputType()==TemplateStoreData.TYPE_PAIR){
			pairBt.setValue(true);
		}else if(data.getInputType()==TemplateStoreData.TYPE_FIRT_KEY){
			firstBt.setValue(true);
		}
		fileNameBox.setText(data.getFileName()==null?"":data.getFileName());
		
		headerArea.setText(data.getHeader()==null?"":data.getHeader());
		footerArea.setText(data.getFooter()==null?"":data.getFooter());
		rowArea.setText(data.getRow()==null?"":data.getRow());
	}
	
	protected void doConvert() {
		boolean multi=multiCheck.getValue();
		List<FileNameAndText> result=null;
		if(singleBt.getValue()){
			result=TemplateConverter.convertAsLine(headerArea.getText(),footerArea.getText(),rowArea.getText(),inputArea.getText(),fileNameBox.getText(),multi);
		}else if(pairBt.getValue()){
			result=TemplateConverter.convertAsPair(headerArea.getText(),footerArea.getText(),rowArea.getText(),inputArea.getText(),fileNameBox.getText(),multi);
		}else{
			result=TemplateConverter.convertAsFirstKey(headerArea.getText(),footerArea.getText(),rowArea.getText(),inputArea.getText(),fileNameBox.getText(),multi);
		}
		
		for(FileNameAndText fn:result){
			if(fn.getName().isEmpty()){
				fn.setName("generated.txt");
			}
		}
		
		
		cellList.setRowData(0, result);
		if(result.size()>0){
			selectionModel.setSelected(result.get(0), true);
		}
		
		TemplateStoreData data=new TemplateStoreData();
		data.setFileName(fileNameBox.getText());
		data.setMultiOutput(multiCheck.getValue());
		if(singleBt.getValue()){
			data.setInputType(TemplateStoreData.TYPE_SINGLE);
		}else if(pairBt.getValue()){
			data.setInputType(TemplateStoreData.TYPE_PAIR);
		}else{
			data.setInputType(TemplateStoreData.TYPE_FIRT_KEY);
		}
		data.setHeader(headerArea.getText());
		data.setFooter(footerArea.getText());
		data.setRow(rowArea.getText());
		templateArea.setText(TemplateStoreDataBuilder.toStoreText(data));
		try {
			storageControler.setValue(KEY_TEMPLATE_LAST_DATA, templateArea.getText());
			storageControler.setValue(KEY_TEMPLATE_LAST_INPUT, inputArea.getText());
		} catch (StorageException e) {
			LogUtils.log(e.getMessage());
		}

		if(templateDataList.getSimpleDataListWidget().getSelection()!=null){
		String old=templateDataList.getSimpleDataListWidget().getSelection().getData().getData();
		if(!old.equals(templateArea.getText())){
			templateDataList.getSimpleDataListWidget().getSelection().getData().setData(templateArea.getText());
			templateDataList.getSimpleDataListWidget().setModified(true);
		}
		
		}
		
	}
	private void setOutputText(String text) {
		outputArea.setText(text);
	}

}

package com.akjava.gwt.templatetext.client;

import java.util.ArrayList;
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
import com.akjava.gwt.lib.client.experimental.WidgetList;
import com.akjava.gwt.lib.client.widget.PasteValueReceiveArea;
import com.akjava.gwt.lib.client.widget.TabInputableTextArea;
import com.akjava.gwt.templatetext.client.TemplateStoreData.TemplateData;
import com.google.common.base.Objects;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
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
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FocusWidget;
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
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TemplateTextGenerator implements EntryPoint {
	 interface Driver extends SimpleBeanEditorDriver< TemplateData,  TemplateDataEditor> {}
	 Driver driver = GWT.create(Driver.class);

	private static final String KEY_TEMPLATE_LAST_DATA = "TemplateTextGenerate_LAST_DATA";
	private static final String KEY_TEMPLATE_LAST_INPUT = "TemplateTextGenerate_LAST_INPUT";
	StorageControler storageControler=new StorageControler();

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
		
		 cellListProvider.addDataDisplay(cellList);
		
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
		HorizontalPanel buttonPanel=new HorizontalPanel();
		buttonPanel.setWidth("100%");
		buttonPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		Button selectAll=new Button("Select All Text",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				outputArea.selectAll();
			}
		});
		buttonPanel.add(selectAll);
		outputPanel.add(buttonPanel);
		outputArea = new TextArea();
		outputArea.setReadOnly(true);
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
		tab.add(templatePanel,"Loaded Template");
		tab.selectTab(0);
		
		VerticalPanel helps=new VerticalPanel();
		helps.setSpacing(8);
		parentPanel.add(helps);
		helps.add(new Label("Tips"));
		helps.add(new Label("${value}:normal"));
		helps.add(new Label("${u+value}:upper camel"));
		helps.add(new Label("${l+value}:lower camel"));
		helps.add(new Label("${U+value}:upper case"));
		helps.add(new Label("${L+value}:upper case"));
		helps.add(new Label("${name+value}:name only of fileName"));
		helps.add(new Label("${ext+value}: ext only of fileName"));
		helps.add(new Label("${_+value}: replace ' ' & '-' to underbar"));
	}
	private String appName="TemplateTextGenerator";
	private String version="v1.0";
	
	public class TemplateDataEditor extends VerticalPanel implements Editor<TemplateData>{
		 TextBox fileNameEditor;
		 TextArea headerEditor;
		 TextArea footerEditor;
		 TextArea rowEditor;
		public TemplateDataEditor(){

			//file name box
			HorizontalPanel fileNamesPanel=new HorizontalPanel();
			fileNamesPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			this.add(fileNamesPanel);
			fileNamesPanel.add(new Label("FileName Template"));
			fileNameEditor = new TextBox();
			fileNameEditor.setWidth("250px");
			fileNamesPanel.add(fileNameEditor);
			//header box
			
			headerEditor = new TextArea();
			headerEditor.setSize("600px","150px");
			this.add(new Label("Header Template"));
			this.add(headerEditor);
			
			rowEditor = new TextArea();
			rowEditor.setSize("600px","150px");
			this.add(new Label("Row/Body Template"));
			this.add(rowEditor);
			
			footerEditor = new TextArea();
			footerEditor.setSize("600px","150px");
			this.add(new Label("Footer Template"));
			this.add(footerEditor);
		}
	}
	private TemplateDataEditor dataEditor;
	private void createLeftPanels(Panel parentPanel){
		//copy paste box
		PasteValueReceiveArea pasteArea=new PasteValueReceiveArea();
		 pasteArea.setStylePrimaryName("clipbg");
		// pasteArea.setStylePrimaryName("readonly");
		 pasteArea.setText(appName+" "+version+"\t\t-- Clipboard Receiver -- \nClick(Focus) & Paste Template-Text Here");
		 parentPanel.add(pasteArea);
		 pasteArea.setSize("600px", "30px");
		 pasteArea.setFocus(true);
		 pasteArea.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				loadTemplateText(event.getValue());
				doConvert();
			}
			 
		});
		 
		 HorizontalPanel listPanel=new HorizontalPanel();
		
		 listPanel.setWidth("100%");
		 parentPanel.add(listPanel);
		 
		 templateDataListContainer = new WidgetList<TemplateData>(){
			@Override
			public void onSelect(TemplateData data) {
				if(templateDataListContainer.getSelection()!=null && templateDataListContainer.getSelection()!=data){
					driver.flush();//always flush first
				}
				driver.edit(data);
			}

			@Override
			public FocusPanel createWidget(TemplateData data) {
				FocusPanel panel=new FocusPanel();
				int index=currentStoredata.getDatas().indexOf(data)+1;
				Label label=new Label(""+index);
				label.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
				label.setWidth("40px");
				panel.add(label);
				
				return panel;
			}
		 };
		 templateDataListContainer.setWidth("450px");
		 
		 listPanel.add(templateDataListContainer);
		 
		 HorizontalPanel listButtons=new HorizontalPanel();
		 //listButtons.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		 //listButtons.setBorderWidth(1);
		 
		 //listButtons.setWidth("100%");
		 listPanel.add(listButtons);
		 
		 Button addBt=new Button("Add",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				driver.flush();
				
				if(currentStoredata.getDatas().size()<8){//max - 8
				TemplateData data=new TemplateData();
				currentStoredata.add(data);
				templateDataListContainer.updateData();
				templateDataListContainer.select(data);
				}
			}
		});
		 listButtons.add(addBt);
		 
		 Button removeBt=new Button("Remove",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					TemplateData cdata=driver.flush();
					if(currentStoredata.getDatas().size()>1){
					templateDataListContainer.removeData(cdata);
					templateDataListContainer.select(currentStoredata.getDatas().get(0));
					}
				}
			});
		 listButtons.add(removeBt);
		 
		 
		 
		 //add listWidget here
		 dataEditor=new TemplateDataEditor();
		 dataEditor.getElement().getStyle().setBackgroundColor("#eee");
		 parentPanel.add(dataEditor);
		 driver.initialize(dataEditor);
		 
		
		
		
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
		 pairBt.setText("key and value pair line(tab-sv)");
		 hpanel.add(pairBt);
		 
		 firstBt = new RadioButton("type");
		 firstBt.setText("First line column use as Key(tab-sv)");
		 hpanel.add(firstBt);
		 
		 inputArea = new TabInputableTextArea();
		 inputArea.setSize("600px","150px");
		 descriptionLabel = new Label();
		 parentPanel.add(descriptionLabel);
		 parentPanel.add(inputArea);
		 
		 parentPanel.add(new Label("Output"));
		 HorizontalPanel hpanel2=new HorizontalPanel();
		 parentPanel.add(hpanel2);
		 multiCheck = new CheckBox("multiple output file or merged single text(No effect FileName)");
		 multiCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				//TODO re support future
				//fileNameBox.setEnabled(event.getValue());
			}
			 
		});
		 hpanel2.add(multiCheck);
		 
		 
		 inputArea.setStyleName("textbg");
		 
		
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
		if(text==null ){
			return;
		}
		
		if(text.isEmpty()){//initial case
			currentStoredata=new TemplateStoreData();
			currentStoredata.setInputType(TemplateStoreData.TYPE_SINGLE);
			currentStoredata.setMultiOutput(false);
			TemplateData data=new TemplateData();
			data.setRow("${value}");
			data.setFileName("${value}.txt");
			data.setHeader("");
			currentStoredata.add(data);
		}else{
			currentStoredata = new TemplateStoreDataConverter().reverse().convert(text);
		}
		
		
		//TODO should replace to editor? but i'dont know hot to multiple
		
		multiCheck.setValue(currentStoredata.isMultiOutput());
		if(currentStoredata.getInputType()==TemplateStoreData.TYPE_SINGLE){
			singleBt.setValue(true);
		}else if(currentStoredata.getInputType()==TemplateStoreData.TYPE_PAIR){
			pairBt.setValue(true);
		}else if(currentStoredata.getInputType()==TemplateStoreData.TYPE_FIRT_KEY){
			firstBt.setValue(true);
		}
		
		//TODO future support
		//fileNameBox.setEnabled(multiCheck.getValue());
		
		templateDataListContainer.setData(currentStoredata.getDatas());//update widget
		
		if(currentStoredata.size()>0){
			templateDataListContainer.select(currentStoredata.get(0));
		}else{
			LogUtils.log("not support 0 child data");
		}
		
		
	}
	private ListDataProvider<FileNameAndText> cellListProvider=new ListDataProvider<FileNameAndText>();

	private TemplateStoreData currentStoredata;

	private WidgetList<TemplateData> templateDataListContainer;
	protected void doConvert() {
		
		driver.flush();//store right now;
		
		boolean multi=multiCheck.getValue();
		
		List<FileNameAndText> allFiles=new ArrayList<FileNameAndText>();
		for(int i=0;i<currentStoredata.size();i++){
			TemplateData data=currentStoredata.get(i);
		List<FileNameAndText> result=null;
		
		String header=Objects.firstNonNull(data.getHeader(), "");
		String row=Objects.firstNonNull(data.getRow(), "");
		String footer=Objects.firstNonNull(data.getFooter(), "");
		String fileName=Objects.firstNonNull(data.getFileName(), "");
		
		LogUtils.log("fileName:"+fileName);
		if(singleBt.getValue()){
			result=TemplateConverter.convertAsLine(header,footer,row,inputArea.getText(),fileName,multi);
		}else if(pairBt.getValue()){
			result=TemplateConverter.convertAsPair(header,footer,row,inputArea.getText(),fileName,multi);
		}else{
			result=TemplateConverter.convertAsFirstKey(header,footer,row,inputArea.getText(),fileName,multi);
		}
		
		for(FileNameAndText fn:result){
			if(fn.getName().isEmpty()){
				fn.setName("generated.txt");
			}
			
			allFiles.add(fn);
		}
		
		
		
		}
		
		
		//cellList.setRowCount(0);
		
		//i have no idea why old list still exist on cellList.getRowContainer()
		//clear children.
		int child=cellList.getRowContainer().getChildCount();
		for(int i=child-1;i>=0;i--){//clear first
			cellList.getRowContainer().getChild(i).removeFromParent();
		}
		
		
		LogUtils.log(cellList.asWidget());
		
		cellListProvider.setList(allFiles);
		
		if(allFiles.size()>0){
			selectionModel.setSelected(allFiles.get(0), true);
		}
		
		
		
		currentStoredata.setMultiOutput(multiCheck.getValue());
		if(singleBt.getValue()){
			currentStoredata.setInputType(TemplateStoreData.TYPE_SINGLE);
		}else if(pairBt.getValue()){
			currentStoredata.setInputType(TemplateStoreData.TYPE_PAIR);
		}else{
			currentStoredata.setInputType(TemplateStoreData.TYPE_FIRT_KEY);
		}
		
		
		
		templateArea.setText(new TemplateStoreDataConverter().convert(currentStoredata));
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

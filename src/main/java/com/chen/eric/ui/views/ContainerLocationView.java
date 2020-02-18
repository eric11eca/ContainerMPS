package com.chen.eric.ui.views;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.klaudeta.PaginatedGrid;

import com.chen.eric.backend.Bay;
import com.chen.eric.backend.Block;
import com.chen.eric.backend.Location;
import com.chen.eric.backend.StorageArea;
import com.chen.eric.backend.service.DataContainer;
import com.chen.eric.ui.MainLayout;
import com.chen.eric.ui.components.FlexBoxLayout;
import com.chen.eric.ui.components.ListItem;
import com.chen.eric.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.chen.eric.ui.layout.size.Bottom;
import com.chen.eric.ui.util.FontSize;
import com.chen.eric.ui.util.LumoStyles;
import com.chen.eric.ui.util.UIUtils;
import com.chen.eric.ui.util.css.FlexDirection;
import com.chen.eric.ui.util.css.WhiteSpace;
import com.chen.eric.ui.views.dashboard.WrapperCard;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.DebouncePhase;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Container Location")
@Route(value = "stored-at", layout = MainLayout.class)
@SuppressWarnings("serial")
@CssImport(value = "styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class ContainerLocationView extends SplitViewFrame{
	private Grid<Location> containerLocationGrid;
	private Grid<StorageArea> storageGrid;
	private ListDataProvider<Location> dataProvider;
	private ListDataProvider<StorageArea> storageDataProvider;
	
	private Tabs blockTabs;
	private WrapperCard storageOverviewWrapper;
	private WrapperCard storageContentWrapper;
	private WrapperCard locationWrapper;

	private Row storageAreaDetail;
	private StorageArea areaToBeDisplayed ;
	private VerticalLayout blockContent;
	private WrapperCard blocksWrapper;
	
	private Board board;
	private Chart areaCapacity;
	
	private StorageArea tempStorageArea;
	private Integer storageID;
	private Integer selectedStorage = 100;

	private DataContainer dataContainer = DataContainer.getInstance();
	private static Map<Integer, String[]> areaMap = new HashMap<>();

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		initAreaMap();
		setViewContent(createContent());
	}
	
	private void initialContent() {
		dataContainer.getStorageAreaRecords();
		areaToBeDisplayed = dataContainer.storageAreaRecords.get("100");
		dataContainer.initStorageArea("100");
	}
	
	private Component createContent() {
		board = new Board();
		storageAreaDetail = new Row();
		
		initialContent();
		createCurrCapacityChart();
		createAreaOverview();
		createStorageContent();
		createContainerLocationGrid();
		createStorageDetail();
		
		board.add(storageAreaDetail);
		board.addRow(storageContentWrapper, locationWrapper);

		FlexBoxLayout content = new FlexBoxLayout(board);
		content.setAlignItems(FlexComponent.Alignment.CENTER);
		content.setFlexDirection(FlexDirection.COLUMN);
		return content;
	}
	
	private static void initAreaMap() {
		areaMap.put(100, new String[]{"Normal1", "blue"});
		areaMap.put(101, new String[]{"Normal2", "cyan"});
		areaMap.put(102, new String[]{"Normal3", "cornflowerblue"});
		areaMap.put(103, new String[]{"Hazard", "orange"});
		areaMap.put(104, new String[]{"Reefer", "black"});
		areaMap.put(105, new String[]{"Illegal", "red"});
	}
	
	private Button areaButtonFactory(int areaID) {
		Button areaButton = UIUtils.createPrimaryButton(areaMap.get(areaID)[0]);
		areaButton.getStyle().set("backgroundColor", areaMap.get(areaID)[1]);
		areaButton.addClickListener(e -> {
			selectedStorage = areaID;
			String storageID = String.valueOf(areaID);
			dataContainer.getLocationRecordsByParams("StorageID", storageID);
			areaToBeDisplayed = dataContainer.storageAreaRecords.get(storageID);

			createStorageDetail();
			board.remove(storageAreaDetail);
			board.addComponentAtIndex(0, storageAreaDetail);
		});
		return areaButton;
	}
	
	private void createAreaOverview() {
		HorizontalLayout zone1 = new HorizontalLayout();
		HorizontalLayout zone2 = new HorizontalLayout();
		
		Button B11 = areaButtonFactory(100);
		Button B12 = areaButtonFactory(101);
		Button B13 = areaButtonFactory(102);
		Button B21 = areaButtonFactory(103);
		Button B22 = areaButtonFactory(104);
		Button B23 = areaButtonFactory(105);
		
		zone1.add(B11, B12, B13);
		zone2.add(B21, B22, B23);
		
		zone1.setAlignItems(Alignment.BASELINE);
		zone1.setSpacing(true);
		zone1.setPadding(true);
		zone1.setMargin(true);
		
		zone2.setAlignItems(Alignment.BASELINE);
		zone2.setSpacing(true);
		zone2.setPadding(true);
		zone2.setMargin(true);
		
		storageOverviewWrapper = new WrapperCard("wrapper", 
				new Component[] {zone1, zone2, areaCapacity}, "card", "space-m");
	}
	
	private void createStorageDetail() {
		dataContainer.initStorageArea(
			String.valueOf(areaToBeDisplayed.getStorageID()));
		
		if (storageAreaDetail.getComponentCount()>0) {
			storageAreaDetail.removeAll();
		}
		
		if (blockTabs == null) {
			createBlockTabs();
		}
		createBlockContent(areaToBeDisplayed, 0);
		
		blocksWrapper = new WrapperCard("wrapper", 
				new Component[] {blockTabs,blockContent}, "card", "space-m");
		storageAreaDetail.add(storageOverviewWrapper, blocksWrapper);
	}
	
	private void createBlockTabs() {
		Tab block1Tab = new Tab("Block One");
		Tab block2Tab = new Tab("Block Two");

		Map<Tab, Integer> tabsToBlock = new HashMap<>();
		tabsToBlock.put(block1Tab, 0);
		tabsToBlock.put(block2Tab, 1);

		blockTabs = new Tabs(block1Tab, block2Tab);
		blockTabs.addSelectedChangeListener(e -> {
		    int blockIndex = tabsToBlock.get(blockTabs.getSelectedTab());
		    createBlockContent(areaToBeDisplayed, blockIndex);
		    
		    blocksWrapper = new WrapperCard("wrapper", 
					new Component[] {blockTabs, blockContent}, "card", "space-m");
		  
		    storageAreaDetail.removeAll();
		    storageAreaDetail.add(storageOverviewWrapper, blocksWrapper);
		    
		    board.remove(storageAreaDetail);
			board.addComponentAtIndex(0, storageAreaDetail);
		});
	}
	
	private void createBlockContent(StorageArea area, int blockIndex) {
		Block block = area.getBlock(blockIndex);
		blockContent = new VerticalLayout();
		
		PaginatedGrid<Bay> blockLayer = new PaginatedGrid<>();
		List<Bay> concactnated = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			concactnated.addAll(block.getTier(i));
		}
		blockLayer.setSizeFull();
		blockLayer.setDataProvider(DataProvider.ofCollection(concactnated));
		blockLayer.addComponentColumn(this::slotLayout).setAutoWidth(true);
		blockLayer.setPageSize(6);
		blockLayer.setPaginatorSize(2);
		
		blockContent.add(blockLayer);
	}
	
	private Component slotLayout(Bay bay) {
		HorizontalLayout slots =  new HorizontalLayout(drawSlot(bay,0),drawSlot(bay,1),
				drawSlot(bay,2),drawSlot(bay,3),drawSlot(bay,4));
		slots.setSpacing(false);
		slots.setAlignItems(Alignment.BASELINE);
		return slots;
	}
	
	private Component drawSlot(Bay bay, int rowIndex) {
		Location loc = bay.getContainer(rowIndex);	
		Button slot = UIUtils.createPrimaryButton("Empty   ");
		slot.setSizeUndefined();
		slot.getStyle().set("backgroundColor", areaMap.get(selectedStorage)[1]);
		
		if (loc.getContainerID() != null) {
			slot.getStyle().set("backgroundColor", "darkslateblue");
			slot.setText(String.valueOf(loc.getContainerID()));
		}
		
		slot.addClickListener(e->{
			updateLocation(loc, slot).open();
		});
		return slot;
	}
	
	private Dialog updateLocation(Location location, Button slot) {
		Dialog dialog = new Dialog();
		Location newLocation = new Location();
		newLocation.copyIndices(location);
		
		NumberField getID = new NumberField();
		getID.setLabel("ContainerID");
		getID.addValueChangeListener(e-> 
			newLocation.setContainerID(e.getValue().intValue()));
		
		DatePicker startDate = new DatePicker();
		startDate.setLabel("Start Date");
		startDate.addValueChangeListener(e ->
			newLocation.setStartDate(
				Date.valueOf(e.getValue())));
		
		DatePicker endDate = new DatePicker();
		endDate.setLabel("End Date");
		endDate.addValueChangeListener(e ->
			newLocation.setEndDate(
				Date.valueOf(endDate.getValue())));
		
		Button insert = UIUtils.createPrimaryButton("Insert");
		insert.addClickListener(e->{	
			int code = dataContainer.insertLocationRecords(newLocation);
			if (code == 0) {
				Notification.show("Successfully Placed the Container!", 4000, Notification.Position.BOTTOM_CENTER);
				dataContainer.getLocationRecords();
		        dataProvider = DataProvider.ofCollection(dataContainer.locationRecords.values());
		        containerLocationGrid.setDataProvider(dataProvider);
		        createStorageDetail();
		        board.remove(storageAreaDetail);
				board.addComponentAtIndex(0, storageAreaDetail);
		        dialog.close();
			} else if (code == 1) {
				Notification.show("The given containerID already exits!", 4000, Notification.Position.BOTTOM_CENTER);
			} else if (code == 2) {
				Notification.show("The given owner dose not exist!", 4000, Notification.Position.BOTTOM_CENTER);
			} else {
				Notification.show("ERROR: Insertion FAILED!", 4000, Notification.Position.BOTTOM_CENTER);
			}
		});
		
		/*Button update = UIUtils.createPrimaryButton("Update");
		update.addClickListener(e->{
			int code = dataContainer.updateLocationRecords(newLocation, newLocation.getContainerID());
			if (code == 0) {
				Notification.show("Successfully Update the Location!", 4000, Notification.Position.BOTTOM_CENTER);
				dataContainer.getLocationRecords();
		        dataProvider = DataProvider.ofCollection(dataContainer.locationRecords.values());
		        containerLocationGrid.setDataProvider(dataProvider);
		        createStorageDetail();
		        content.addToSecondary(storageAreaDetail);
		        slot.getStyle().set("backgroundColor", "cyan");
		        dialog.close();
			} else if (code == 1) {
				Notification.show("The given containerID already exits!", 4000, Notification.Position.BOTTOM_CENTER);
			} else if (code == 2) {
				Notification.show("The given owner dose not exist!", 4000, Notification.Position.BOTTOM_CENTER);
			} else {
				Notification.show("ERROR: Update FAILED!", 4000, Notification.Position.BOTTOM_CENTER);
			}
		});*/
		
		Button cancel = UIUtils.createTertiaryButton("Cancel");
		cancel.addClickListener(e->{
			dialog.close();
		});
		
		HorizontalLayout content = new HorizontalLayout(getID, startDate, endDate); 
		content.setSpacing(true);
		content.setPadding(true);
		HorizontalLayout footer = new HorizontalLayout(insert, cancel); 
		footer.setSpacing(true);
		footer.setPadding(true);
		
		dialog.add(content, footer);
		dialog.setSizeFull();
		return dialog;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HorizontalLayout createToolBar() {
		TextField searchBar = new TextField();
        searchBar.setPlaceholder("Search...");
        searchBar.setWidth("100%");
        searchBar.setValueChangeMode(ValueChangeMode.EAGER);
        searchBar.setPrefixComponent(VaadinIcon.SEARCH.create());
        Icon closeIcon = new Icon("lumo", "cross");
        closeIcon.setVisible(false);
        ComponentUtil.addListener(closeIcon, ClickEvent.class,
                (ComponentEventListener) e -> searchBar.clear());
        searchBar.setSuffixComponent(closeIcon);
        
        searchBar.getElement().addEventListener("value-changed", event -> {
            closeIcon.setVisible(!searchBar.getValue().isEmpty());
            dataProvider.addFilter(
                    l -> StringUtils.containsIgnoreCase(String.valueOf(l.getContainerID()),
                            searchBar.getValue()));
        }).debounce(300, DebouncePhase.TRAILING);
        
        HorizontalLayout toolBar = new HorizontalLayout(searchBar);
        return toolBar;
	}

	private void createContainerLocationGrid() {
		dataContainer.getLocationRecords();
		dataProvider = DataProvider.ofCollection(dataContainer.locationRecords.values());
		
		containerLocationGrid = new Grid<>();
		containerLocationGrid.setDataProvider(dataProvider);
		
		containerLocationGrid.addColumn(Location::getContainerID)
				.setAutoWidth(true)
				.setResizable(true)
				.setHeader("Container ID");
		containerLocationGrid.addComponentColumn(this::createLocation)
				.setAutoWidth(true)
				.setResizable(true)
				.setHeader("Area;Block;\nTire;Bay;Row");
		containerLocationGrid.addColumn(vessel -> UIUtils.formatSqlDate(vessel.getStartDate()))
				.setAutoWidth(true)
				.setResizable(true)
				.setComparator(Location::getStartDate)
				.setHeader("Start Date");
		containerLocationGrid.addColumn(vessel -> UIUtils.formatSqlDate(vessel.getEndDate()))
				.setAutoWidth(true)
				.setResizable(true)
				.setComparator(Location::getEndDate)
				.setHeader("End Date");
		containerLocationGrid.addColumn(new ComponentRenderer<>(this::createRemoveButton))
				.setAutoWidth(true)
				.setResizable(true)
				.setTextAlign(ColumnTextAlign.CENTER);
		
		locationWrapper = new WrapperCard("wrapper", 
				new Component[] {createToolBar(), containerLocationGrid}, "card", "space-m"); 
	}

	private Component createLocation(Location location) {
		Label locationVec = UIUtils.createLabel(FontSize.M, "["+
				String.valueOf(location.getStorageID().intValue())+"; "+
				String.valueOf(location.getBlockIndex().intValue())+"; "+
				String.valueOf(location.getBayIndex().intValue())+"; "+
				String.valueOf(location.getTierIndex().intValue())+"; "+
				String.valueOf(location.getRowIndex().intValue())+"]");
		return locationVec;
	}
	
	private Button createRemoveButton(Location location) {
		Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            int code = dataContainer.deleteLocationRecords(location.getContainerID());
            if (code == 0) {
            	dataContainer.getLocationRecords();
            	dataProvider = DataProvider.ofCollection(dataContainer.locationRecords.values());
        		containerLocationGrid.setDataProvider(dataProvider);
        		Notification.show("Succesfully deleted the storage area" ,4000, Notification.Position.BOTTOM_CENTER);
            } else {
            	Notification.show("ERROR: DELETION FALIED" ,4000, Notification.Position.BOTTOM_CENTER);
            }
        });
        button.setClassName("delete-button");
        button.addThemeName("small");
        return button;
    }

	
	
	
	
	private void createStorageContent() {
		Button addStorageArea = UIUtils.createPrimaryButton("Add StorageArea");
		addStorageArea.addClickListener(e-> {
			createAddStorageArea().open();
	    });
	        
		storageContentWrapper = new WrapperCard("wrapper", 
				new Component[] {addStorageArea, createStorageGrid()}, "card", "space-m"); 
	}

	private Grid<StorageArea> createStorageGrid() {
		dataContainer.getStorageAreaRecords();
		storageDataProvider = DataProvider.ofCollection(dataContainer.storageAreaRecords.values());
		storageGrid = new Grid<>();
		storageGrid.setDataProvider(storageDataProvider);
		storageGrid.setSizeFull();
		
		storageGrid.addColumn(StorageArea::getStorageID)
				.setAutoWidth(true)
				.setSortable(true)
				.setHeader("ID");
		storageGrid.addColumn(StorageArea::getType)
				.setAutoWidth(true)
				.setSortable(true)
				.setHeader("Type");
		storageGrid.addColumn(StorageArea::getCapacity)
				.setAutoWidth(true)
				.setSortable(true)
				.setHeader("Capacity");
		storageGrid.addColumn(StorageArea::getStoragePrice)
				.setAutoWidth(true)
				.setSortable(true)
				.setHeader("Fee");
		storageGrid.addColumn(new ComponentRenderer<>(this::buttonBar))
				.setAutoWidth(true)
				.setResizable(true)
				.setTextAlign(ColumnTextAlign.CENTER);
		return storageGrid;
	}
	
	private Div buttonBar(StorageArea storageArea) {
		return new Div(createStorageUpdateButton(storageArea), 
				createStorageRemoveButton(storageArea));
	}
	
	private Button createStorageRemoveButton(StorageArea storageArea) {
		Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
           int code = dataContainer.deleteStorageAreaRecords(storageArea.getStorageID());
           if (code == 0) {
           	dataContainer.getStorageAreaRecords();
           	storageDataProvider = DataProvider.ofCollection(dataContainer.storageAreaRecords.values());
       		storageGrid.setDataProvider(storageDataProvider);
       		Notification.show("Succesfully deleted the customer" ,4000, Notification.Position.BOTTOM_CENTER);
           } else {
           	Notification.show("ERROR: DELETION FALIED" ,4000, Notification.Position.BOTTOM_CENTER);
           }
       });
       button.setClassName("delete-button");
       button.addThemeName("small");
       return button;
	}
	
	private void createCurrCapacityChart() {
		areaCapacity = new Chart();
		Configuration config = areaCapacity.getConfiguration();
		config.setTitle("Current Number of Containers per Area");
		config.getChart().setType(ChartType.COLUMN);
		XAxis x = new XAxis();
		x.setCrosshair(new Crosshair());
		x.setCategories("Normal1", "Normal2", "Normal3", "Hazard", "Refeer","IIlegal");
		config.addxAxis(x);
		YAxis y = new YAxis();
        y.setMin(0);
        config.addyAxis(y);

        config.addSeries(new ListSeries("Current Capacity", 700, 500, 600, 300, 200, 100));
        config.addSeries(new ListSeries("Allwoed Capacity", 800, 800, 800, 600, 600, 500));
	}
	
	private Button createStorageUpdateButton(StorageArea storageArea) {
		Button button = new Button(new Icon(VaadinIcon.REFRESH), clickEvent -> {
			Dialog dialog = createStorageDetails(storageArea);
			dialog.open();
       });
		button.setClassName("delete-button");
       button.addThemeName("small");
       return button;
   }
	
	private Dialog createAddStorageArea() {
		Dialog addPanel = new Dialog();
		StorageArea newStorageArea = new StorageArea(0,"Normal",0,0.0);
		
		TextField updateID = new TextField();
		updateID.setWidth("100%");
		updateID.setLabel("StorageArea ID");
		updateID.addValueChangeListener(e-> {
			newStorageArea.setStorageID(Integer.valueOf(e.getValue()));
		});
		
		NumberField updateCapacity = new NumberField();
		updateCapacity.setWidth("100%");
		updateCapacity.setLabel("Capacity");
		updateCapacity.addValueChangeListener(e-> {
			newStorageArea.setCapacity(e.getValue().intValue());
		});
		
		Select<String> typePicker = new Select<>();
		typePicker.setLabel("Type");
		typePicker.setItems("Normal", "Reefer", "Hazard", "Illegal", "Livestock");
		typePicker.setWidth("100%");
		typePicker.addValueChangeListener(
       		e -> newStorageArea.setType(e.getValue()));
       
		NumberField updatePrice = new NumberField();
       updatePrice.setWidth("100%");
       updatePrice.setLabel("Storage Price");
       updatePrice.addValueChangeListener(e-> {
       	newStorageArea.setStoragePrice(e.getValue());
		});
		
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e->{
			if (newStorageArea.getStorageID() == null) {
				Notification.show("Container ID cannot be empty!", 4000, Notification.Position.BOTTOM_CENTER);
			} else {
				int code = dataContainer.insertStorageAreaRecords(newStorageArea);
				
				if (code == 0) {
					Notification.show("Succesfully Inserted the Container!", 4000, Notification.Position.BOTTOM_CENTER);
					dataContainer.getStorageAreaRecords();
			        storageDataProvider = DataProvider.ofCollection(dataContainer.storageAreaRecords.values());
			        storageGrid.setDataProvider(storageDataProvider);
			        addPanel.close();
				} else if (code == 1) {
					Notification.show("The given containerID already exits!", 4000, Notification.Position.BOTTOM_CENTER);
				} else if (code == 2) {
					Notification.show("The given owner dose not exist!", 4000, Notification.Position.BOTTOM_CENTER);
				} else {
					Notification.show("ERROR: Insertion FAILED!", 4000, Notification.Position.BOTTOM_CENTER);
				}
			}
		});
		
		detailsDrawerFooter.addCancelListener(e->{
			addPanel.close();
		});
		
		VerticalLayout content = new VerticalLayout(
				updateID, updateCapacity, typePicker, detailsDrawerFooter);
		addPanel.add(content);
		return addPanel;
	}

	private Dialog createStorageDetails(StorageArea storageArea) {
		Dialog detailDiaLog = new Dialog();
		
		tempStorageArea = new StorageArea();
		storageID = storageArea.getStorageID();
		
		TextField updateID = new TextField();
		updateID.setWidth("100%");
		updateID.setLabel("StorageArea ID");
		updateID.setPlaceholder(String.valueOf(storageArea.getStorageID()));
		updateID.addValueChangeListener(e-> {
			tempStorageArea.setStorageID(Integer.valueOf(e.getValue()));
		});
		
		NumberField updateCapacity = new NumberField();
		updateCapacity.setWidth("100%");
		updateCapacity.setLabel("Capacity");
		updateCapacity.setPlaceholder(String.valueOf(storageArea.getCapacity()));
		updateCapacity.addValueChangeListener(e-> {
			tempStorageArea.setCapacity(e.getValue().intValue());
		});
		
		Select<String> typePicker = new Select<>();
		typePicker.setLabel("Type");
		typePicker.setPlaceholder(storageArea.getType());
		typePicker.setItems("Normal", "Reefer", "Hazard", "Illegal", "Livestock");
		typePicker.setWidth("100%");
		typePicker.addValueChangeListener(
       		e -> tempStorageArea.setType(e.getValue()));
       
		NumberField updatePrice = new NumberField();
       updatePrice.setWidth("100%");
       updatePrice.setLabel("Storage Price");
       updatePrice.setPlaceholder(String.valueOf(storageArea.getStoragePrice()));
       updatePrice.addValueChangeListener(e-> {
    	   tempStorageArea.setStoragePrice(e.getValue());
		});
		
		ListItem status = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.PACKAGE), updateID, "StorageArea ID");
		
		status.getContent().setAlignItems(FlexComponent.Alignment.BASELINE);
		status.getContent().setSpacing(Bottom.XS);
		
		ListItem capacity = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.GRID_BEVEL),
				updateCapacity , "Capacity");
		ListItem typepick = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.DOLLAR),
				typePicker, "Type");
		ListItem price = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.CLIPBOARD),
				updatePrice, "Owner");

		for (ListItem item : new ListItem[]{
				status, capacity, typepick, price}) {
			item.setReverse(true);
			item.setWhiteSpace(WhiteSpace.PRE_LINE);
		}
		
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e -> {
			if (tempStorageArea != null && storageID != null) {
				int code = dataContainer.updateStorageAreaRecords(tempStorageArea, storageID);
				if (code == 0) {
					dataContainer.getStorageAreaRecords();
					storageDataProvider = DataProvider.ofCollection(dataContainer.storageAreaRecords.values());
					storageGrid.setDataProvider(storageDataProvider);
					Notification.show("Succesfully Updated the Data! WITH CODE: " + code, 4000, Notification.Position.BOTTOM_CENTER);
				} else if (code == 1) {
					Notification.show("This Vessel Does Not Exist");
				} else {
					Notification.show("ERROR: UPDATE FAILED!");
				}
			}
		});
		
		detailsDrawerFooter.addCancelListener(e-> detailDiaLog.close());

		Div details = new Div(status, capacity, typepick, price, detailsDrawerFooter);
		details.addClassName(LumoStyles.Padding.Vertical.S);
		
		detailDiaLog.add(details);
		return detailDiaLog;
	}
}

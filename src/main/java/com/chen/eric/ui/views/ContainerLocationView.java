package com.chen.eric.ui.views;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.chen.eric.backend.Bay;
import com.chen.eric.backend.Block;
import com.chen.eric.backend.Location;
import com.chen.eric.backend.StorageArea;
import com.chen.eric.backend.Tier;
import com.chen.eric.backend.service.DataContainer;
import com.chen.eric.ui.MainLayout;
import com.chen.eric.ui.components.Badge;
import com.chen.eric.ui.components.ListItem;
import com.chen.eric.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.chen.eric.ui.layout.size.Bottom;
import com.chen.eric.ui.util.FontSize;
import com.chen.eric.ui.util.LumoStyles;
import com.chen.eric.ui.util.UIUtils;
import com.chen.eric.ui.util.css.WhiteSpace;
import com.chen.eric.ui.util.css.lumo.BadgeColor;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import com.vaadin.flow.component.splitlayout.SplitLayout;
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
public class ContainerLocationView extends SplitViewFrame{
	private Grid<Location> containerLocationGrid;
	private Grid<StorageArea> storageGrid;
	private ListDataProvider<Location> dataProvider;
	private ListDataProvider<StorageArea> storageDataProvider;
	
	private Tabs blockTabs;
	private VerticalLayout storageAreaOverView;
	private VerticalLayout storageAreaDetail;
	private StorageArea areaToBeDisplayed ;
	private VerticalLayout blockContent;
	
	private SplitLayout content;
	
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
		initialContent();
		content = new SplitLayout();
		content.setSplitterPosition(42);
		createAreaOverview();
		content.addToPrimary(storageAreaOverView);
		createStorageDetail();
		content.addToSecondary(storageAreaDetail);
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
			content.addToSecondary(storageAreaDetail);
			content.setSplitterPosition(40);
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
		
		zone2.setAlignItems(Alignment.BASELINE);
		zone2.setSpacing(true);
		zone2.setPadding(true);
		
		VerticalLayout block = new VerticalLayout();
		block.getStyle().set("border", "2px solid #9E9E9E");
		block.setWidth("400px");
		block.add(zone1, zone2);
		
		storageAreaOverView = new VerticalLayout(block, createStorageContent(), 
				createToolBar(), createContainerLocationGrid());
	}
	
	private void createStorageDetail() {
		dataContainer.initStorageArea(
			String.valueOf(areaToBeDisplayed.getStorageID()));
		
		storageAreaDetail = new VerticalLayout();
		storageAreaDetail.setAlignItems(Alignment.START);
		
		createBlockTabs();
		createBlockContent(areaToBeDisplayed, 0);
		
		storageAreaDetail.add(blockTabs);
		storageAreaDetail.add(blockContent);
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
		    storageAreaDetail.remove(blockContent);
		    createBlockContent(areaToBeDisplayed, blockIndex);
		    storageAreaDetail.add(blockContent);
		});
	}
	
	private void createBlockContent(StorageArea area, int blockIndex) {
		Block block = area.getBlock(blockIndex);
		blockContent = new VerticalLayout();
		String[] tireName = new String[] {"Tier1", "Tier2", "Tier3"};
		for (int i = 0; i < 3; i++) {
			Grid<Bay> blockLayer = createTireGrid(tireName[i], block.getTier(i));
			blockContent.add(blockLayer);
		}
		blockContent.setSpacing(true);	
	}

	private Grid<Bay> createTireGrid(String layerName, Tier tier) {
		Grid<Bay> blockTire = new Grid<>();
		blockTire.setDataProvider(DataProvider.ofCollection(tier));
		
		blockTire.addComponentColumn(c-> drawSlot(c,0))
			.setAutoWidth(true).setHeader(new Badge(layerName, BadgeColor.SUCCESS));
		blockTire.addComponentColumn(c-> drawSlot(c,1)).setAutoWidth(true);
		blockTire.addComponentColumn(c-> drawSlot(c,2)).setAutoWidth(true);
		blockTire.addComponentColumn(c-> drawSlot(c,3)).setAutoWidth(true);
		blockTire.addComponentColumn(c-> drawSlot(c,4)).setAutoWidth(true);
		
		blockTire.setHeightByRows(true);
		blockTire.setWidth("680px");
		return blockTire;
	}
	
	private Component drawSlot(Bay bay, int rowIndex) {
		Location loc = bay.getContainer(rowIndex);	
		Button slot = UIUtils.createPrimaryButton("Empty");
		slot.getStyle().set("width", "100px");
		slot.getStyle().set("hegiht", "70%");
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
				Notification.show("Succesfully Placed Container!", 4000, Notification.Position.BOTTOM_CENTER);
				dataContainer.getLocationRecords();
		        dataProvider = DataProvider.ofCollection(dataContainer.locationRecords.values());
		        containerLocationGrid.setDataProvider(dataProvider);
		        createStorageDetail();
		        content.addToSecondary(storageAreaDetail);
		        dialog.close();
			} else if (code == 1) {
				Notification.show("The given containerID already exits!", 4000, Notification.Position.BOTTOM_CENTER);
			} else if (code == 2) {
				Notification.show("The given owner dose not exist!", 4000, Notification.Position.BOTTOM_CENTER);
			} else {
				Notification.show("ERROR: Insertion FAILED!", 4000, Notification.Position.BOTTOM_CENTER);
			}
		});
		
		Button update = UIUtils.createPrimaryButton("Update");
		update.addClickListener(e->{
			int code = dataContainer.updateLocationRecords(newLocation, newLocation.getContainerID());
			if (code == 0) {
				Notification.show("Succesfully Update the Loacation!", 4000, Notification.Position.BOTTOM_CENTER);
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
		});
		
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
        toolBar.setAlignItems(Alignment.BASELINE);
        toolBar.setSpacing(true);
        toolBar.setPadding(true);
        
        return toolBar;
	}

	private Grid<Location> createContainerLocationGrid() {
		dataContainer.getLocationRecords();
		dataProvider = DataProvider.ofCollection(dataContainer.locationRecords.values());
		
		containerLocationGrid = new Grid<>();
		containerLocationGrid.setDataProvider(dataProvider);
		containerLocationGrid.setHeightByRows(true);
		containerLocationGrid.setWidthFull();
		
		containerLocationGrid.addColumn(Location::getContainerID)
				.setAutoWidth(true)
				.setResizable(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Container ID");
		containerLocationGrid.addColumn(Location::getStorageID)
				.setAutoWidth(true)
				.setResizable(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Type");
		containerLocationGrid.addComponentColumn(this::createLocation)
				.setAutoWidth(true)
				.setResizable(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Detailed Location");
		containerLocationGrid.addColumn(vessel -> UIUtils.formatSqlDate(vessel.getStartDate()))
				.setAutoWidth(true)
				.setResizable(true)
				.setFlexGrow(0)
				.setComparator(Location::getStartDate)
				.setHeader("Start Date");
		containerLocationGrid.addColumn(vessel -> UIUtils.formatSqlDate(vessel.getEndDate()))
				.setAutoWidth(true)
				.setResizable(true)
				.setComparator(Location::getEndDate)
				.setFlexGrow(0)
				.setHeader("End Date");
		containerLocationGrid.addColumn(new ComponentRenderer<>(this::createRemoveButton))
				.setFlexGrow(0)
				.setWidth("130px")
				.setResizable(true)
				.setResizable(true)
				.setTextAlign(ColumnTextAlign.CENTER);
		return containerLocationGrid;
	}

	private Component createLocation(Location location) {
		Label blockIndex = UIUtils.createLabel(FontSize.M, 
				"Block:"+String.valueOf(location.getBlockIndex().intValue()));
        Label bayIndex = UIUtils.createLabel(FontSize.M,
        		" Bay:"+String.valueOf(location.getBayIndex().intValue()));
        Label tierIndex = UIUtils.createLabel(FontSize.M, 
        		" Tier:"+String.valueOf(location.getTierIndex().intValue()));
        Label slotIndex = UIUtils.createLabel(FontSize.M, 
        		" Row:"+String.valueOf(location.getRowIndex().intValue())); 

		HorizontalLayout locationLayer = new HorizontalLayout(
				blockIndex, bayIndex, tierIndex, slotIndex);
		locationLayer.setAlignItems(Alignment.BASELINE);
		return locationLayer;
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

	
	
	
	
	private Component createStorageContent() {
		Button addStorageArea = UIUtils.createPrimaryButton("Add StorageArea");
		addStorageArea.addClickListener(e-> {
			createAddStorageArea().open();
	    });
	        
		VerticalLayout content = new VerticalLayout(addStorageArea, createStorageGrid());
		return content;
	}

	private Grid<StorageArea> createStorageGrid() {
		dataContainer.getStorageAreaRecords();
		storageDataProvider = DataProvider.ofCollection(dataContainer.storageAreaRecords.values());
		storageGrid = new Grid<>();
		storageGrid.setDataProvider(storageDataProvider);
		storageGrid.setHeightByRows(true);
		storageGrid.setWidth("480px");
		
		storageGrid.addColumn(StorageArea::getStorageID)
				.setWidth("80px")
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("ID");
		storageGrid.addColumn(StorageArea::getType)
				.setWidth("80px")
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Type");
		storageGrid.addColumn(StorageArea::getCapacity)
				.setWidth("120px")
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Capacity");
		storageGrid.addColumn(StorageArea::getStoragePrice)
				.setWidth("80px")
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Fee");
		storageGrid.addColumn(new ComponentRenderer<>(this::buttonBar))
				.setFlexGrow(0)
				.setWidth("80px")
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

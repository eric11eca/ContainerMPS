package com.chen.eric.ui.views;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import com.chen.eric.backend.Bay;
import com.chen.eric.backend.Block;
import com.chen.eric.backend.Location;
import com.chen.eric.backend.StorageArea;
import com.chen.eric.backend.Tier;
import com.chen.eric.backend.service.DataContainer;
import com.chen.eric.ui.MainLayout;
import com.chen.eric.ui.components.Badge;
import com.chen.eric.ui.components.FlexBoxLayout;
import com.chen.eric.ui.components.ListItem;
import com.chen.eric.ui.components.detailsdrawer.DetailsDrawer;
import com.chen.eric.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.chen.eric.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.chen.eric.ui.components.navigation.bar.AppBar;
import com.chen.eric.ui.layout.size.Bottom;
import com.chen.eric.ui.layout.size.Horizontal;
import com.chen.eric.ui.layout.size.Top;
import com.chen.eric.ui.util.LumoStyles;
import com.chen.eric.ui.util.UIUtils;
import com.chen.eric.ui.util.css.BoxSizing;
import com.chen.eric.ui.util.css.WhiteSpace;
import com.chen.eric.ui.util.css.lumo.BadgeColor;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout.Orientation;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.DebouncePhase;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@PageTitle("Storage")
@Route(value = "storage", layout = MainLayout.class)
public class StorageAreaView extends SplitViewFrame{
	private Grid<StorageArea> grid;
	private ListDataProvider<StorageArea> dataProvider;
	private DetailsDrawer detailsDrawer;
	private String filter = "";
	private StorageArea tempStorageArea;
	private Integer storageID;
	
	private HorizontalLayout storageAreaDetail;
	private StorageArea areaToBeDisplayed;
	
	private DataContainer dataContainer = DataContainer.getInstance();

	private static Map<String, String> areaMap = new HashMap<>();

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		initAreaMap();
		dataContainer.getStorageAreaRecords();
		dataContainer.initStorageAreas();
		setViewContent(createContent());
		setViewDetails(createDetailsDrawer());
	}

	private Component createContent() {
		SplitLayout storageAreaVisual = new SplitLayout(createAreaOverview(), storageAreaDetail());
		SplitLayout splitContent = new SplitLayout(storageAreaVisual, createGrid());
		splitContent.setOrientation(Orientation.VERTICAL);
		FlexBoxLayout content = new FlexBoxLayout(
				new VerticalLayout(createToolBar(), splitContent));
		content.setBoxSizing(BoxSizing.BORDER_BOX);
		content.setHeightFull();
		content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
		return content;
	}
	
	private static void initAreaMap() {
		areaMap.put("Normal1", "100");
		areaMap.put("Normal2", "101");
		areaMap.put("Normal3", "102");
		areaMap.put("Hazard", "103");
		areaMap.put("Reefer", "104");
		areaMap.put("Illegal", "105");
	}
	
	private VerticalLayout createAreaOverview() {
		HorizontalLayout zone1 = new HorizontalLayout();
		HorizontalLayout zone2 = new HorizontalLayout();
		
		Button B11 = UIUtils.createPrimaryButton("Normal1");
		Button B12 = UIUtils.createPrimaryButton("Normal2");
		Button B13 = UIUtils.createPrimaryButton("Normal3");
		Button B21 = UIUtils.createPrimaryButton("Hazard");
		Button B22 = UIUtils.createPrimaryButton("Refeer");
		Button B23 = UIUtils.createPrimaryButton("Illegal");
		
		B21.getStyle().set("backgroundColor", "orange");
		B22.getStyle().set("backgroundColor", "black");
		B23.getStyle().set("backgroundColor", "green");
		
		B11.addClickListener(e-> {
			String storageID = areaMap.get(B11.getText());
			dataContainer.getLocationRecordsByParams("StorageID", storageID);
			areaToBeDisplayed = dataContainer.storageAreaRecords.get(storageID);
		});
		
		B12.addClickListener(e-> {
			String storageID = areaMap.get(B12.getText());
			dataContainer.getLocationRecordsByParams("StorageID", storageID);
			areaToBeDisplayed = dataContainer.storageAreaRecords.get(storageID);
		});
		
		B13.addClickListener(e-> {
			String storageID = areaMap.get(B13.getText());
			dataContainer.getLocationRecordsByParams("StorageID", storageID);
			areaToBeDisplayed = dataContainer.storageAreaRecords.get(storageID);
		});
		
		B21.addClickListener(e-> {
			String storageID = areaMap.get(B21.getText());
			dataContainer.getLocationRecordsByParams("StorageID", storageID);
			areaToBeDisplayed = dataContainer.storageAreaRecords.get(storageID);
		});
		
		B22.addClickListener(e-> {
			String storageID = areaMap.get(B22.getText());
			dataContainer.getLocationRecordsByParams("StorageID", storageID);
			areaToBeDisplayed = dataContainer.storageAreaRecords.get(storageID);
		});
		
		B23.addClickListener(e-> {
			String storageID = areaMap.get(B23.getText());
			dataContainer.getLocationRecordsByParams("StorageID", storageID);
			areaToBeDisplayed = dataContainer.storageAreaRecords.get(storageID);
		});
		
		zone1.add(B11, B12, B13);
		zone2.add(B21, B22, B23);
		
		zone1.setAlignItems(Alignment.BASELINE);
		zone1.setSpacing(true);
		zone1.setPadding(true);
		
		zone2.setAlignItems(Alignment.BASELINE);
		zone2.setSpacing(true);
		zone2.setPadding(true);
		
		VerticalLayout block = new VerticalLayout(zone1, zone2);
		return block;
	}
	
	private Component storageAreaDetail() {
		AppBar appBar = MainLayout.get().getAppBar();
		
		for (String block : new String[] {"Block1, Block2"}) {
			appBar.addTab(block);
		}
		
		appBar.addTabSelectionListener(e -> {
			if (e.getSelectedTab().getLabel().equals("Block1")) {
				createStorageDetial(areaToBeDisplayed, 0);
			} else {
				createStorageDetial(areaToBeDisplayed, 1);
			}
		});
		
		appBar.centerTabs();
		
		createStorageDetial(areaToBeDisplayed, 0);
		return new VerticalLayout(appBar, storageAreaDetail);
	}
	
	private void createStorageDetial(StorageArea area, int blockIndex) {
		storageAreaDetail = new HorizontalLayout();
		storageAreaDetail.add(blockContent(area.getBlock(blockIndex)));
		storageAreaDetail.setAlignItems(Alignment.BASELINE);
		storageAreaDetail.setSpacing(true);
	}
	
	private Component blockContent(Block block) {
		HorizontalLayout blockcontent = new HorizontalLayout();
		
		for (int i = 0; i < 3; i++) {
			Grid<Bay> blockLayer = createTireGrid("Tire1", block.getTier(i));
			blockcontent.add(blockLayer);
		}
		
		blockcontent.setAlignItems(Alignment.BASELINE);
		blockcontent.setSpacing(true);	
		return blockcontent;
	}

	private Grid<Bay> createTireGrid(String layerName, Tier tier) {
		Grid<Bay> blockTire = new Grid<>();
		blockTire.setDataProvider(DataProvider.ofCollection(tier));
		
		blockTire.addComponentColumn(c-> drawSlot(c,1))
			.setWidth("80px").setHeader(new Badge(layerName, BadgeColor.SUCCESS));
		blockTire.addComponentColumn(c-> drawSlot(c,2)).setWidth("80px");
		blockTire.addComponentColumn(c-> drawSlot(c,3)).setWidth("80px");
		blockTire.addComponentColumn(c-> drawSlot(c,4)).setWidth("80px");
		blockTire.addComponentColumn(c-> drawSlot(c,5)).setWidth("80px");
		
		blockTire.setHeightByRows(true);
		blockTire.setWidth("850px");
		return blockTire;
	}
	
	private Component drawSlot(Bay bay, int rowIndex) {
		Location loc = bay.getContainer(rowIndex);
		
		Button slot = UIUtils.createButton(
				String.valueOf(loc.getContainerID()),
				ButtonVariant.LUMO_ICON);
		slot.setWidth("10px");
		if (loc.getContainerID() != null) {
			slot.getStyle().set("backgroundColor", "yellow");
		}
		
		slot.addClickListener(e->{
			slot.getStyle().set("backgroundColor", "red");
			updateLocation(loc).open();
		});
		return slot;
	}
	
	private Dialog updateLocation(Location location) {
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
			dataContainer.insertLocationRecords(location);
		});
		
		Button update = UIUtils.createPrimaryButton("Update");
		update.addClickListener(e->{
			dataContainer.updateLocationRecords(newLocation, newLocation.getContainerID());
		});
		
		Button cancel = UIUtils.createTertiaryButton("Cancel");
		cancel.addClickListener(e->{
			dialog.close();
		});
		
		HorizontalLayout footer = new HorizontalLayout(insert, cancel); 
		
		dialog.add(getID, startDate, endDate, footer);
		return dialog;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HorizontalLayout createToolBar() {
		TextField searchBar = new TextField();
        searchBar.setPlaceholder("Search...");
        searchBar.setWidth("50%");
        searchBar.setValueChangeMode(ValueChangeMode.EAGER);
        searchBar.setPrefixComponent(VaadinIcon.SEARCH.create());
        Icon closeIcon = new Icon("lumo", "cross");
        closeIcon.setVisible(false);
        ComponentUtil.addListener(closeIcon, ClickEvent.class,
                (ComponentEventListener) e -> searchBar.clear());
        searchBar.setSuffixComponent(closeIcon);
        
        Select<String> searchFilter = new Select<>();
        searchFilter.setItems("ContainerID", "isPayed", "Owner", "Weight", "Volume", "Type");
        searchFilter.setLabel("Search Filter");
        searchFilter.addValueChangeListener(e -> filter = e.getValue());

        searchBar.getElement().addEventListener("value-changed", event -> {
            closeIcon.setVisible(!searchBar.getValue().isEmpty());  
            
            if (filter.isEmpty() || searchBar.getValue().isEmpty()) {
            	dataContainer.getStorageAreaRecords();
            } else {
            	dataContainer.getStorageAreaRecordsByParams(filter, searchBar.getValue());
            }
            
	        dataProvider = DataProvider.ofCollection(dataContainer.storageAreaRecords.values());
	        grid.setDataProvider(dataProvider);
        }).debounce(300, DebouncePhase.TRAILING);
        
        Button addContainer = UIUtils.createPrimaryButton("Add Container");
        addContainer.setWidthFull();
        addContainer.addClickListener(e-> {
        	createAddContainer().open();
        });
        
        HorizontalLayout toolBar = new HorizontalLayout(addContainer, searchFilter, searchBar);
        toolBar.setAlignItems(Alignment.BASELINE);
        toolBar.setSpacing(true);
        toolBar.setPadding(true);
        
        return toolBar;
	}

	private Grid<StorageArea> createGrid() {
		dataContainer.getStorageAreaRecords();
		dataProvider = DataProvider.ofCollection(dataContainer.storageAreaRecords.values());
		grid = new Grid<>();
		grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));
		grid.setDataProvider(dataProvider);
		grid.setHeightByRows(true);
		grid.setWidthFull();
		
		grid.addColumn(StorageArea::getStorageID)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Container ID");
		grid.addColumn(StorageArea::getType)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Type");
		grid.addColumn(StorageArea::getCapacity)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Owner");
		grid.addColumn(StorageArea::getStoragePrice)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Service Fee");
		grid.addColumn(new ComponentRenderer<>(this::createRemoveButton))
				.setFlexGrow(0).setWidth("130px")
				.setResizable(true)
				.setTextAlign(ColumnTextAlign.CENTER);
		return grid;
	}
	
	private Button createRemoveButton(StorageArea storageArea) {
		Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            int code = dataContainer.deleteVesselRecords(storageArea.getStorageID());
            if (code == 0) {
            	dataContainer.getStorageAreaRecords();
            	dataProvider = DataProvider.ofCollection(dataContainer.storageAreaRecords.values());
        		grid.setDataProvider(dataProvider);
        		Notification.show("Succesfully deleted the customer" ,4000, Notification.Position.BOTTOM_CENTER);
            } else {
            	Notification.show("ERROR: DELETION FALIED" ,4000, Notification.Position.BOTTOM_CENTER);
            }
        });
        button.setClassName("delete-button");
        button.addThemeName("small");
        return button;
    }
	
	private Dialog createAddContainer() {
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
			        dataProvider = DataProvider.ofCollection(dataContainer.storageAreaRecords.values());
			        grid.setDataProvider(dataProvider);
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

	private DetailsDrawer createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("StorageArea Details");
		detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
		
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e -> {
			if (tempStorageArea != null && storageID != null) {
				int code = dataContainer.updateStorageAreaRecords(tempStorageArea, storageID);
				if (code == 10) {
					dataContainer.getStorageAreaRecords();
					dataProvider = DataProvider.ofCollection(dataContainer.storageAreaRecords.values());
					grid.setDataProvider(dataProvider);
					Notification.show("Succesfully Updated the Data! WITH CODE: " + code, 4000, Notification.Position.BOTTOM_CENTER);
				} else if (code == 1) {
					Notification.show("This Vessel Does Not Exist");
				} else {
					Notification.show("ERROR: UPDATE FAILED!");
				}
			}
		});
			
		detailsDrawer.setHeader(detailsDrawerHeader);
		detailsDrawer.setFooter(detailsDrawerFooter);
		return detailsDrawer;
	}

	private void showDetails(StorageArea storageArea) {
		tempStorageArea = new StorageArea();
		storageID = storageArea.getStorageID();
		detailsDrawer.setContent(createDetails(storageArea));
		detailsDrawer.show();
	}

	private Component createDetails(StorageArea storageArea) {
		TextField updateID = new TextField();
		updateID.setWidth("100%");
		updateID.setLabel("StorageArea ID");
		updateID.setPlaceholder(String.valueOf(storageArea.getStorageID()));
		updateID.addValueChangeListener(e-> {
			storageArea.setStorageID(Integer.valueOf(e.getValue()));
		});
		
		NumberField updateCapacity = new NumberField();
		updateCapacity.setWidth("100%");
		updateCapacity.setLabel("Capacity");
		updateCapacity.setPlaceholder(String.valueOf(storageArea.getCapacity()));
		updateCapacity.addValueChangeListener(e-> {
			storageArea.setCapacity(e.getValue().intValue());
		});
		
		Select<String> typePicker = new Select<>();
		typePicker.setLabel("Type");
		typePicker.setPlaceholder(storageArea.getType());
		typePicker.setItems("Normal", "Reefer", "Hazard", "Illegal", "Livestock");
		typePicker.setWidth("100%");
		typePicker.addValueChangeListener(
        		e -> storageArea.setType(e.getValue()));
        
		NumberField updatePrice = new NumberField();
        updatePrice.setWidth("100%");
        updatePrice.setLabel("Storage Price");
        updatePrice.setPlaceholder(String.valueOf(storageArea.getStoragePrice()));
        updatePrice.addValueChangeListener(e-> {
        	storageArea.setStoragePrice(e.getValue());
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

		Div details = new Div(status, capacity, typepick, price);
		details.addClassName(LumoStyles.Padding.Vertical.S);
		return details;
	}	
}

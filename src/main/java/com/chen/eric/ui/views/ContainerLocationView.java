package com.chen.eric.ui.views;

import java.sql.Date;
import java.time.LocalDate;

import com.chen.eric.backend.Container;
import com.chen.eric.backend.Location;
import com.chen.eric.backend.service.DataContainer;
import com.chen.eric.ui.MainLayout;
import com.chen.eric.ui.components.FlexBoxLayout;
import com.chen.eric.ui.components.ListItem;
import com.chen.eric.ui.components.detailsdrawer.DetailsDrawer;
import com.chen.eric.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.chen.eric.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.chen.eric.ui.layout.size.Bottom;
import com.chen.eric.ui.layout.size.Horizontal;
import com.chen.eric.ui.layout.size.Top;
import com.chen.eric.ui.util.LumoStyles;
import com.chen.eric.ui.util.UIUtils;
import com.chen.eric.ui.util.css.BoxSizing;
import com.chen.eric.ui.util.css.WhiteSpace;
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
	private Grid<Location> grid;
	private ListDataProvider<Location> dataProvider;
	private DetailsDrawer detailsDrawer;
	private String filter = "";
	private Container tempLocation;
	private Integer containerID;
	
	private DataContainer dataContainer = DataContainer.getInstance();

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		setViewContent(createContent());
		setViewDetails(createDetailsDrawer());
	}

	private Component createContent() {
		FlexBoxLayout content = new FlexBoxLayout(
				new VerticalLayout(createToolBar(), createGrid()));
		content.setBoxSizing(BoxSizing.BORDER_BOX);
		content.setHeightFull();
		content.setPadding(Horizontal.RESPONSIVE_X, Top.RESPONSIVE_X);
		return content;
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
            	dataContainer.getLocationRecords();
            } else {
            	dataContainer.getLocationRecordsByParams(filter, searchBar.getValue());
            }
            
	        dataProvider = DataProvider.ofCollection(dataContainer.locationRecords.values());
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

	private Grid<Location> createGrid() {
		dataContainer.getContainerRecords();
		dataProvider = DataProvider.ofCollection(dataContainer.locationRecords.values());
		grid = new Grid<>();
		grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));
		grid.setDataProvider(dataProvider);
		grid.setHeightByRows(true);
		grid.setWidthFull();
		
		grid.addColumn(Location::getContainerID)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Container ID");
		grid.addColumn(Location::getStorageID)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Type");
		grid.addComponentColumn(this::createLocation)
				.setWidth("360px")
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Detialed Location")
				.setTextAlign(ColumnTextAlign.END);
		grid.addColumn(vessel -> UIUtils.formatSqlDate(vessel.getStartDate()))
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setComparator(Location::getStartDate)
				.setHeader("Start Date");
		grid.addColumn(vessel -> UIUtils.formatSqlDate(vessel.getEndDate()))
				.setAutoWidth(true)
				.setComparator(Location::getEndDate)
				.setFlexGrow(0)
				.setHeader("End Date");
		grid.addColumn(new ComponentRenderer<>(this::createRemoveButton))
				.setFlexGrow(0).setWidth("130px")
				.setResizable(true)
				.setTextAlign(ColumnTextAlign.CENTER);
		return grid;
	}

	private Component createLocation(Location location) {
		Label blockIndex = UIUtils.createAmountLabel(location.getBlockIndex());
        Label bayIndex = UIUtils.createAmountLabel(location.getBayIndex());
        Label tierIndex = UIUtils.createAmountLabel(location.getTireIndex());
        Label slotIndex = UIUtils.createAmountLabel(location.getRowIndex()); 

		HorizontalLayout locationLayer = new HorizontalLayout(
				blockIndex, bayIndex, tierIndex, slotIndex);
		locationLayer.setAlignItems(Alignment.BASELINE);
		return locationLayer;
	}
	
	private Button createRemoveButton(Location location) {
		Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            int code = dataContainer.deleteVesselRecords(location.getContainerID());
            if (code == 0) {
            	dataContainer.getLocationRecords();
            	dataProvider = DataProvider.ofCollection(dataContainer.locationRecords.values());
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
		Location newLocation = new Location(0,0,0,0,0,0,
				Date.valueOf("1900-01-01"),Date.valueOf("1900-01-01"));
		
		TextField updatePrimeID = new TextField();
		updatePrimeID.setWidth("50%");
		updatePrimeID.setLabel("Container ID");
		updatePrimeID.addValueChangeListener(e-> {
			newLocation.setContainerID(Integer.valueOf(e.getValue()));
		});
		
		TextField updateSecID = new TextField();
		updateSecID.setWidth("50%");
		updateSecID.setLabel("Storage Area ID");
		updateSecID.addValueChangeListener(e-> {
			newLocation.setStorageID(Integer.valueOf(e.getValue()));
		});
        
		NumberField updateBlockIndex = new NumberField();
        updateBlockIndex.setWidth("30%");
        updateBlockIndex.setLabel("Block Index");
        updateBlockIndex.addValueChangeListener(e-> {
        	newLocation.setBlockIndex(e.getValue().intValue());
		});

        NumberField updateBayIndex = new NumberField();
        updateBayIndex.setWidth("30%");
        updateBayIndex.setLabel("Bay Index");
        updateBayIndex.addValueChangeListener(e-> {
        	newLocation.setBayIndex(e.getValue().intValue());
		});
        
        NumberField updateTierIndex = new NumberField();
        updateTierIndex.setWidth("30%");
        updateTierIndex.setLabel("Tier Index");
        updateTierIndex.addValueChangeListener(e-> {
        	newLocation.setTierIndex(e.getValue().intValue());
		});
        
        NumberField updateRowIndex = new NumberField();
        updateRowIndex.setWidth("20%");
        updateRowIndex.setLabel("Row Index");
        updateRowIndex.addValueChangeListener(e-> {
        	newLocation.setRowIndex(e.getValue().intValue());
		});
        
        DatePicker startDatePicker = new DatePicker();
		startDatePicker.setLabel("Start Date");
		startDatePicker.setClearButtonVisible(true);
		startDatePicker.addValueChangeListener(e->{
			LocalDate date = startDatePicker.getValue();
			newLocation.setStartDate(Date.valueOf(date));
		});
		
		DatePicker endDatePicker = new DatePicker();
		endDatePicker.setLabel("End Date");
		endDatePicker.setClearButtonVisible(true);
		endDatePicker.addValueChangeListener(e->{
			LocalDate date = endDatePicker.getValue();
			newLocation.setEndDate(Date.valueOf(date));
		});
        
        HorizontalLayout idLayer = new HorizontalLayout(updatePrimeID, updateSecID);
        idLayer.setAlignItems(Alignment.BASELINE);

		HorizontalLayout locationLayer = new HorizontalLayout(
				updateBlockIndex, updateTierIndex, updateRowIndex, updateBlockIndex);
		locationLayer.setAlignItems(Alignment.BASELINE);
		
		HorizontalLayout dateLayer = new HorizontalLayout(startDatePicker, endDatePicker);
		dateLayer.setAlignItems(Alignment.BASELINE);
		
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e->{
			if (newLocation.getContainerID() == null) {
				Notification.show("Container ID cannot be empty!", 4000, Notification.Position.BOTTOM_CENTER);
			} else {
				int code = dataContainer.insertLocationRecords(newLocation);
				
				if (code == 0) {
					Notification.show("Succesfully Inserted the Container!", 4000, Notification.Position.BOTTOM_CENTER);
					dataContainer.getLocationRecords();
			        dataProvider = DataProvider.ofCollection(dataContainer.locationRecords.values());
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
				idLayer, locationLayer, dateLayer, detailsDrawerFooter);
		addPanel.add(content);
		return addPanel;
	}

	private DetailsDrawer createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Vessel Details");
		detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
		
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e -> {
			if (tempLocation != null && containerID != null) {
				int code = dataContainer.updateContainerRecords(tempLocation, containerID);
				if (code == 10) {
					dataContainer.getContainerRecords();
					dataProvider = DataProvider.ofCollection(dataContainer.locationRecords.values());
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

	private void showDetails(Location location) {
		tempLocation = new Container();
		containerID = location.getContainerID();
		detailsDrawer.setContent(createDetails(location));
		detailsDrawer.show();
	}

	private Component createDetails(Location location) {
		TextField updatePrimeID = new TextField();
		updatePrimeID.setWidth("50%");
		updatePrimeID.addValueChangeListener(e-> {
			location.setContainerID(Integer.valueOf(e.getValue()));
		});
		
		TextField updateSecID = new TextField();
		updateSecID.setWidth("50%");
		updateSecID.addValueChangeListener(e-> {
			location.setStorageID(Integer.valueOf(e.getValue()));
		});
        
		NumberField updateBlockIndex = new NumberField();
        updateBlockIndex.setWidth("30%");
        updateBlockIndex.setLabel("Block Index");
        updateBlockIndex.addValueChangeListener(e-> {
        	location.setBlockIndex(e.getValue().intValue());
		});

        NumberField updateBayIndex = new NumberField();
        updateBayIndex.setWidth("30%");
        updateBayIndex.setLabel("Bay Index");
        updateBayIndex.addValueChangeListener(e-> {
        	location.setBayIndex(e.getValue().intValue());
		});
        
        NumberField updateTierIndex = new NumberField();
        updateTierIndex.setWidth("30%");
        updateTierIndex.setLabel("Tier Index");
        updateTierIndex.addValueChangeListener(e-> {
        	location.setTierIndex(e.getValue().intValue());
		});
        
        NumberField updateRowIndex = new NumberField();
        updateRowIndex.setWidth("20%");
        updateRowIndex.setLabel("Row Index");
        updateRowIndex.addValueChangeListener(e-> {
        	location.setRowIndex(e.getValue().intValue());
		});
        
        DatePicker startDatePicker = new DatePicker();
		startDatePicker.setClearButtonVisible(true);
		startDatePicker.addValueChangeListener(e->{
			LocalDate date = startDatePicker.getValue();
			location.setStartDate(Date.valueOf(date));
		});
		
		DatePicker endDatePicker = new DatePicker();
		endDatePicker.setClearButtonVisible(true);
		endDatePicker.addValueChangeListener(e->{
			LocalDate date = endDatePicker.getValue();
			location.setEndDate(Date.valueOf(date));
		});
        
        HorizontalLayout idLayer = new HorizontalLayout(updatePrimeID, updateSecID);
        idLayer.setAlignItems(Alignment.BASELINE);

		HorizontalLayout locationLayer = new HorizontalLayout(
				updateBlockIndex, updateTierIndex, updateRowIndex, updateBlockIndex);
		locationLayer.setAlignItems(Alignment.BASELINE);
		
		HorizontalLayout dateLayer = new HorizontalLayout(startDatePicker, endDatePicker);
		dateLayer.setAlignItems(Alignment.BASELINE);
		
		ListItem primeID = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.PACKAGE), updatePrimeID, "Container ID");
		
		primeID.getContent().setAlignItems(FlexComponent.Alignment.BASELINE);
		primeID.getContent().setSpacing(Bottom.XS);
		
		ListItem secID = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.PACKAGE), updateSecID, "Storage Area ID");
		ListItem loc = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.GRID_BEVEL), locationLayer, "Location");
		ListItem startDate = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.CALENDAR), dateLayer, "Start Date");
		ListItem endDate = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.CALENDAR), dateLayer, "End Date");

		for (ListItem item : new ListItem[]{primeID, secID, loc, startDate, endDate}) {
			item.setReverse(true);
			item.setWhiteSpace(WhiteSpace.PRE_LINE);
		}

		Div details = new Div(primeID, secID, loc, startDate, endDate);
		details.addClassName(LumoStyles.Padding.Vertical.S);
		return details;
	}	
}

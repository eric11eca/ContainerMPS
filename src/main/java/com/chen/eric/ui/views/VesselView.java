package com.chen.eric.ui.views;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.chen.eric.backend.Vessel;
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
import com.helger.commons.csv.CSVReader;
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
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.DebouncePhase;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@PageTitle("Vessel")
@Route(value = "", layout = MainLayout.class)
public class VesselView extends SplitViewFrame {

	private Grid<Vessel> grid;
	private ListDataProvider<Vessel> dataProvider;
	private DetailsDrawer detailsDrawer;
	private File tempFile;
	private String filter = "";
	private Vessel tempVessel;
	private Integer vesselID;
	
	private DetailsDrawerHeader detailsDrawerHeader;
	private DetailsDrawerFooter detailsDrawerFooter;
		
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
        searchFilter.setItems("VesselID", "Capacity", "DepartureDate", 
        		"ArrivalDate", "Destination_Country", "DepartedFrom_Country");
        searchFilter.setLabel("Search Filter");
        searchFilter.addValueChangeListener(e -> filter = e.getValue());

        searchBar.getElement().addEventListener("value-changed", event -> {
            closeIcon.setVisible(!searchBar.getValue().isEmpty());  
            
            if (filter.isEmpty() || searchBar.getValue().isEmpty()) {
            	dataContainer.getVesselRecords();
            } else {
            	dataContainer.getVesselRecordsByParams(filter, searchBar.getValue());
            }
            
	        dataProvider = DataProvider.ofCollection(dataContainer.vesselRecords.values());
	        grid.setDataProvider(dataProvider);
        }).debounce(300, DebouncePhase.TRAILING);
        
        Button addVessel = UIUtils.createPrimaryButton("Add Vessel");
        addVessel.setWidthFull();
        addVessel.addClickListener(e->{
        	createAddPanel().open();
        });
        
        HorizontalLayout toolBar = new HorizontalLayout(addVessel, searchFilter, searchBar);
        toolBar.setAlignItems(Alignment.BASELINE);
        toolBar.setSpacing(true);
        toolBar.setPadding(true);
        
        return toolBar;
	}

	private Grid<Vessel> createGrid() {
		dataContainer.getVesselRecords();
		dataProvider = DataProvider.ofCollection(dataContainer.vesselRecords.values());
		grid = new Grid<>();
		grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));
		grid.setDataProvider(dataProvider);
		grid.setWidthFull();
		
		grid.addColumn(Vessel::getVesselID)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Vessel ID");
		grid.addColumn(new ComponentRenderer<>(this::createCapacity))
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Capacity")
				.setTextAlign(ColumnTextAlign.END);
		grid.addColumn(vessel -> UIUtils.formatSqlDate(vessel.getDepartDate()))
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setHeader("Depart Date");
		grid.addColumn(vessel -> UIUtils.formatSqlDate(vessel.getArivalDate()))
				.setAutoWidth(true)
				.setComparator(Vessel::getArivalDate)
				.setFlexGrow(0)
				.setHeader("Arrival Date");
		grid.addColumn(this::createDepartureLocation)
				.setWidth("280px")
				.setResizable(true)
				.setFlexGrow(0)
				.setHeader("Departure Location");
		grid.addColumn(this::createDestionation)
				.setWidth("280px")
				.setResizable(true)
				.setFlexGrow(0)
				.setHeader("Destination");
		grid.addColumn(new ComponentRenderer<>(this::createRemoveButton))
				.setFlexGrow(0).setWidth("130px")
				.setResizable(true)
				.setTextAlign(ColumnTextAlign.CENTER);
		return grid;
	}

	private String createDepartureLocation(Vessel vessel) {
		return UIUtils.formatWorldAddress(
				vessel.getDepartedFromCountry(),
				vessel.getDepartedFromState(),
				vessel.getDepartedFromCity());
	}

	private String createDestionation(Vessel vessel) {
		return UIUtils.formatWorldAddress(
					vessel.getDestinationCountry(),
					vessel.getDestinationState(),
					vessel.getDestinationCity());
	}

	private Component createCapacity(Vessel vessel) {
		Integer capacity = vessel.getCapacity();
		return UIUtils.createAmountLabel(capacity);
	}
	
	private Button createRemoveButton(Vessel vessel) {
		Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            int code = dataContainer.deleteVesselRecords(vessel.getVesselID());
            if (code == 0) {
            	dataContainer.getVesselRecords();
        		dataProvider = DataProvider.ofCollection(dataContainer.vesselRecords.values());
        		grid.setDataProvider(dataProvider);
        		Notification.show("Succefully Deleted the vessel!", 4000, Notification.Position.BOTTOM_CENTER);
            } else {
            	Notification.show("EEROR: DELETION FAILED!");
            }       
        });
        button.setClassName("delete-button");
        button.addThemeName("small");
        return button;
    }
	
	private Dialog createAddPanel() {
		Vessel newVessel = new Vessel(null, 0, "", "", "", "", "", "",
				Date.valueOf("1900-01-01"), Date.valueOf("1900-01-01"));
		
		TextField updateID = new TextField();
		updateID.setLabel("Vessel ID");
		updateID.setWidth("50%");
		updateID.addValueChangeListener(e-> {
			newVessel.setVesselID(Integer.valueOf(e.getValue()));
		});
		
		
		TextField updateCapacity = new TextField();
		updateCapacity.setWidth("50%");
		updateCapacity.setLabel("Capacity");
		updateCapacity.addValueChangeListener(e-> {
			newVessel.setCapacity(Integer.valueOf(e.getValue()));
		});
		
		DatePicker departureDatePicker = new DatePicker();
		departureDatePicker.setLabel("Departure Date");
		departureDatePicker.setClearButtonVisible(true);
		departureDatePicker.addValueChangeListener(e->{
			LocalDate date = departureDatePicker.getValue();
			newVessel.setDepartDate(Date.valueOf(date));
		});
		
		DatePicker arrivalDatePicker = new DatePicker();
		arrivalDatePicker.setLabel("Aarrival Date");
		arrivalDatePicker.setClearButtonVisible(true);
		arrivalDatePicker.addValueChangeListener(e->{
			LocalDate date = arrivalDatePicker.getValue();
			newVessel.setArivalDate(Date.valueOf(date));
		});
		
		Select<String> destinationCountryPicker = new Select<>();
		destinationCountryPicker.setItems("U.S.", "China", "Russia", "Japan", "Australia", 
        		"Canada", "South Korea", "Tiland", "Indonesia", "Chili");
		destinationCountryPicker.setLabel("Destination Country");
		destinationCountryPicker.setWidth("50%");
		destinationCountryPicker.addValueChangeListener(
        		e -> newVessel.setDestinationCountry(e.getValue()));
        
        TextField updateDestinationState = new TextField();
        updateDestinationState.setWidth("50%");
        updateDestinationState.setLabel("Destination State");
        updateDestinationState.addValueChangeListener(e-> {
			newVessel.setDestinationState(e.getValue());
		});
		
		TextField updateDestinationCity = new TextField();
		updateDestinationCity.setWidth("30%");
		updateDestinationCity.setLabel("Destination City");
		updateDestinationCity.addValueChangeListener(e-> {
			newVessel.setDestinationCity(e.getValue());
		});
        
        Select<String> departCountryPicker = new Select<>();
        departCountryPicker.setItems("U.S.", "China", "Russia", "Japan", "Australia", 
        		"Canada", "South Korea", "Tiland", "Indonesia", "Chili");
        departCountryPicker.setLabel("Departure Country");
        departCountryPicker.setWidth("30%");
        departCountryPicker.addValueChangeListener(
        		e -> newVessel.setDepartedFromCountry(e.getValue()));
        
        TextField updateDepartState = new TextField();
        updateDepartState.setWidth("30%");
        updateDepartState.setLabel("Departure State");
        updateDepartState.addValueChangeListener(e-> {
			newVessel.setDepartedFromState(e.getValue());
		});
		
		TextField updateDepartCity = new TextField();
		updateDepartCity.setWidth("30%");
		updateDepartCity.setLabel("Departure City");
		updateDepartCity.addValueChangeListener(e-> {
			newVessel.setDepartedFromCity(e.getValue());
		});
		
		HorizontalLayout idLayer = new HorizontalLayout(
				updateID, updateCapacity);
		
		HorizontalLayout datePicker = new HorizontalLayout(
				departureDatePicker, arrivalDatePicker);
		datePicker.setAlignItems(Alignment.BASELINE);
		
		HorizontalLayout departLocation = new HorizontalLayout(
				departCountryPicker, updateDepartState, updateDepartCity);
		departLocation.setAlignItems(Alignment.BASELINE);
		
		HorizontalLayout destination= new HorizontalLayout(
				destinationCountryPicker, updateDestinationState, updateDestinationCity);
		destination.setAlignItems(Alignment.BASELINE);
		
		Dialog panel = new Dialog();
		
		detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e->{
			if (newVessel.getVesselID() == null) {
				Notification.show("Vessel ID cannot be empty!");
			} else {
				int code = dataContainer.insertVesselRecords(newVessel);
				if (code == 0) {
					Notification.show("Succesfully Inserted the Data!", 4000, Notification.Position.BOTTOM_CENTER);
					dataContainer.getVesselRecords();
			        dataProvider = DataProvider.ofCollection(dataContainer.vesselRecords.values());
			        grid.setDataProvider(dataProvider);
					panel.close();
				} else {
					Notification.show("ERROR: Insertion FAILED!", 4000, Notification.Position.BOTTOM_CENTER);
				}
			}
		});
		
		detailsDrawerFooter.addCancelListener(e->{
			panel.close();
		});
		
		VerticalLayout content = new VerticalLayout(
				idLayer, datePicker, departLocation, destination, detailsDrawerFooter);
		panel.add(content);
		return panel;
	}

	private DetailsDrawer createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

		detailsDrawerHeader = new DetailsDrawerHeader("Vessel Details");
		detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
		
		detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e -> {
			if (tempVessel != null && vesselID != null) {
				int code = dataContainer.updateVesselRecords(tempVessel, vesselID);
				if (code == 0) {
					dataContainer.getVesselRecords();
					dataProvider = DataProvider.ofCollection(dataContainer.vesselRecords.values());
					grid.setDataProvider(dataProvider);
					Notification.show("Succesfully Updated the Data!", 4000, Notification.Position.BOTTOM_CENTER);
				} else if (code == 1) {
					Notification.show("This Vessel Does Not Exist", 4000, Notification.Position.BOTTOM_CENTER);
				} else {
					Notification.show("ERROR: UPDATE FAILED!", 4000, Notification.Position.BOTTOM_CENTER);
				}
			}
		});
			
		detailsDrawer.setHeader(detailsDrawerHeader);
		detailsDrawer.setFooter(detailsDrawerFooter);

		return detailsDrawer;
	}

	private void showDetails(Vessel vessel) {
		tempVessel = new Vessel();
		vesselID = vessel.getVesselID();
		detailsDrawer.setContent(createDetails(vessel));
		detailsDrawer.show();
	}

	private Component createDetails(Vessel vessel) {
		TextField updateID = new TextField();
		updateID.setValue(String.valueOf(vessel.getVesselID()));
		updateID.setWidth("50%");
		updateID.addValueChangeListener(e-> {
			tempVessel.setVesselID(Integer.valueOf(e.getValue()));
		});
		
		
		TextField updateCapacity = new TextField();
		updateCapacity.setWidth("50%");
		updateCapacity.setValue(String.valueOf(vessel.getCapacity()));
		updateCapacity.addValueChangeListener(e-> {
			tempVessel.setCapacity(Integer.valueOf(e.getValue()));
		});
		
		DatePicker departureDatePicker = new DatePicker();
		departureDatePicker.setValue(vessel.getDepartDate().toLocalDate());
		departureDatePicker.setClearButtonVisible(true);
		departureDatePicker.addValueChangeListener(e->{
			LocalDate date = departureDatePicker.getValue();
			tempVessel.setDepartDate(Date.valueOf(date));
		});
		
		DatePicker arrivalDatePicker = new DatePicker();
		arrivalDatePicker.setValue(vessel.getArivalDate().toLocalDate());
		arrivalDatePicker.setClearButtonVisible(true);
		arrivalDatePicker.addValueChangeListener(e->{
			LocalDate date = arrivalDatePicker.getValue();
			tempVessel.setArivalDate(Date.valueOf(date));
		});
		
		Select<String> destinationCountryPicker = new Select<>();
		destinationCountryPicker.setItems("U.S.", "China", "Russia", "Japan", "Australia", 
        		"Canada", "South Korea", "Tiland", "Indonesia", "Chili");
		destinationCountryPicker.setValue(vessel.getDestinationCountry());
		destinationCountryPicker.setWidth("30%");
		destinationCountryPicker.addValueChangeListener(
        		e -> tempVessel.setDestinationCountry(e.getValue()));
        
        TextField updateDestinationState = new TextField();
        updateDestinationState.setWidth("30%");
        updateDestinationState.setValue(vessel.getDestinationState());
        updateDestinationState.addValueChangeListener(e-> {
			tempVessel.setDestinationState(e.getValue());
		});
		
		TextField updateDestinationCity = new TextField();
		updateDestinationCity.setWidth("30%");
		updateDestinationCity.setValue(vessel.getDestinationCity());
		updateDestinationCity.addValueChangeListener(e-> {
			tempVessel.setDestinationCity(e.getValue());
		});
        
        Select<String> departCountryPicker = new Select<>();
        departCountryPicker.setItems("U.S.", "China", "Russia", "Japan", "Australia", 
        		"Canada", "South Korea", "Tiland", "Indonesia", "Chili");
        departCountryPicker.setValue(vessel.getDepartedFromCountry());
        departCountryPicker.setWidth("30%");
        departCountryPicker.addValueChangeListener(
        		e -> tempVessel.setDepartedFromCountry(e.getValue()));
        
        TextField updateDepartState = new TextField();
        updateDepartState.setWidth("30%");
        updateDepartState.setValue(vessel.getDepartedFromState());
        updateDepartState.addValueChangeListener(e-> {
			tempVessel.setDepartedFromState(e.getValue());
		});
		
		TextField updateDepartCity = new TextField();
		updateDepartCity.setWidth("30%");
		updateDepartCity.setValue(vessel.getDepartedFromCity());
		updateDepartCity.addValueChangeListener(e-> {
			tempVessel.setDepartedFromCity(e.getValue());
		});
		
		HorizontalLayout departLocation = new HorizontalLayout(
				departCountryPicker, updateDepartState, updateDepartCity);
		departLocation.setAlignItems(Alignment.BASELINE);
		
		HorizontalLayout destination= new HorizontalLayout(
				destinationCountryPicker, updateDestinationState, updateDestinationCity);
		destination.setAlignItems(Alignment.BASELINE);
		
		ListItem status = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.ANCHOR), updateID, "Vessel");
		
		status.getContent().setAlignItems(FlexComponent.Alignment.BASELINE);
		status.getContent().setSpacing(Bottom.XS);
		
		ListItem from = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.UPLOAD_ALT),
				departLocation , "Departure");
		ListItem to = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.DOWNLOAD_ALT),
				destination, "Destination");
		ListItem amount = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.SCALE),
				updateCapacity, "Capacity");
		ListItem dateArival = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.CALENDAR),
				arrivalDatePicker, "Arrival Date");
		ListItem dateDeparture = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.CALENDAR),
				departureDatePicker, "Departure Date");

		for (ListItem item : new ListItem[]{
				status, from, to, amount, dateArival, dateDeparture}) {
			item.setReverse(true);
			item.setWhiteSpace(WhiteSpace.PRE_LINE);
		}

		Div details = new Div(status, from, to, amount, dateArival, dateDeparture);
		details.addClassName(LumoStyles.Padding.Vertical.S);
		return details;
	}	
	
	public Component uploadProducts() {
    	Upload upload = new Upload(new Receiver() {
			@Override
    	      public OutputStream receiveUpload(String filename, String mimeType) {
    	        try {
    	          tempFile = File.createTempFile("temp", ".csv");
    	          return new FileOutputStream(tempFile);
    	        } catch (IOException e) {
    	          e.printStackTrace();
    	          return null;
    	        }}});
    	
    	    upload.addSucceededListener(e -> {
    	        try {
    	          DataInputStream in = new DataInputStream(new FileInputStream(tempFile));
    	          buildContainerFromCSV(in);
    	          tempFile.delete();
    	        } catch (IOException e1) {
    	          e1.printStackTrace();
    	        }
    	    });
    	    upload.setWidthFull();
    	upload.setHeight("50%");
    	return upload;
    }
    
    private void buildContainerFromCSV(DataInputStream in) {
    	CSVReader csvReader;
		try {
			csvReader = new CSVReader(new InputStreamReader(in,"utf-8"));
	    	List<String> record;
			while ((record = csvReader.readNext()) != null) {
				Vessel v = new Vessel();
				
				v.setVesselID(Integer.parseInt(record.get(0)));
				v.setCapacity(Integer.parseInt(record.get(1)));
				v.setDepartDate(Date.valueOf(record.get(2)));
				v.setArivalDate(Date.valueOf(record.get(3)));
				v.setDepartedFromCountry(record.get(4));
				v.setDepartedFromState(record.get(5));
				v.setDepartedFromCity(record.get(6));
				v.setDestinationCountry(record.get(7));
				v.setDestinationState(record.get(8));
				v.setDestinationCity(record.get(9));
				dataContainer.insertVesselRecords(v);
			}
			dataContainer.getVesselRecords();
			dataProvider = DataProvider.ofCollection(dataContainer.vesselRecords.values());
			grid.setDataProvider(dataProvider);
		} catch (IOException e) {
			e.printStackTrace();
		}	
    }
}

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
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.DebouncePhase;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@PageTitle("Vessel")
@Route(value = "", layout = MainLayout.class)
public class ContainerView extends SplitViewFrame {

	private Grid<Vessel> grid;
	private ListDataProvider<Vessel> dataProvider;
	private DetailsDrawer detailsDrawer;
	private File tempFile;
	private String filter = "";
	private Vessel tempVessel;
	private Integer vesselID;
	
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
        searchFilter.setItems("ID", "Capacity", "DepartureDate", 
        		"ArrivalDate", "DepartureCountry", "DestinationCountry");
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
        
        HorizontalLayout toolBar = new HorizontalLayout(uploadProducts(), searchFilter, searchBar);
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
		grid.setHeightByRows(true);
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
		grid.addColumn(TemplateRenderer.<Vessel>of("[[item.date]]")
				.withProperty("date", vessel -> UIUtils.formatSqlDate(vessel.getDepartDate())))
				.setAutoWidth(true)
				.setComparator(Vessel::getDepartDate)
				.setFlexGrow(0)
				.setHeader("Depart Date");
		grid.addColumn(TemplateRenderer.<Vessel>of("[[item.date]]")
				.withProperty("date", vessel -> UIUtils.formatSqlDate(vessel.getArivalDate())))
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
            dataContainer.deleteVesselRecords(vessel.getVesselID());
            dataContainer.getVesselRecords();
    		dataProvider = DataProvider.ofCollection(dataContainer.vesselRecords.values());
    		grid.setDataProvider(dataProvider);
        });
        button.setClassName("delete-button");
        button.addThemeName("small");
        return button;
    }

	private DetailsDrawer createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Vessel Details");
		detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
		
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e -> {
			if (tempVessel != null && vesselID != null) {
				int code = dataContainer.updateVesselRecords(tempVessel, vesselID);
				if (code > 10) {
					dataContainer.getVesselRecords();
					dataProvider = DataProvider.ofCollection(dataContainer.vesselRecords.values());
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
	
	private Component uploadProducts() {
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

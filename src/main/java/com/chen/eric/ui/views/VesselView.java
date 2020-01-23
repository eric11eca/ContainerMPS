package com.chen.eric.ui.views;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.chen.eric.backend.Vessel;
import com.chen.eric.backend.service.DataContainer;
import com.chen.eric.ui.MainLayout;
import com.chen.eric.ui.components.FlexBoxLayout;
import com.chen.eric.ui.components.ListItem;
import com.chen.eric.ui.components.detailsdrawer.DetailsDrawer;
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
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
@Route(value = "vessel", layout = MainLayout.class)
public class VesselView extends SplitViewFrame {

	private Grid<Vessel> grid;
	private ListDataProvider<Vessel> dataProvider;
	private DetailsDrawer detailsDrawer;
	private File tempFile;
	private String filter = "";
	
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
	
	@SuppressWarnings("unchecked")
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
        
        Checkbox byID = new Checkbox("by ID");
        byID.addValueChangeListener(event -> {
        	if (event.getValue()) {
        		filter = "vesselID";
        	} else {
        		filter = "";
        	}
        });
        
        Checkbox byCapacity = new Checkbox("by Capacity");
        byCapacity.addValueChangeListener(event -> {
        	if (event.getValue()) {
        		filter = "capacity";
        	} else {
        		filter = "";
        	}
        });
        
        Checkbox byAdate = new Checkbox("by arrival date");
        byAdate.addValueChangeListener(event -> {
        	if (event.getValue()) {
        		filter = "arrivalDate";
        	} else {
        		filter = "";
        	}
        });
        
        Checkbox byDdate = new Checkbox("by Departure Date");
        byDdate.addValueChangeListener(event -> {
        	if (event.getValue()) {
        		filter = "departDate";
        	} else {
        		filter = "";
        	}
        });
        
        Checkbox byDestCountry = new Checkbox("by Destination Country");
        byDestCountry.addValueChangeListener(event -> {
        	if (event.getValue()) {
        		filter = "destinationCountry";
        	} else {
        		filter = "";
        	}
        });
        
        Checkbox byDepartCountry = new Checkbox("by Departure Country");
        byDepartCountry.addValueChangeListener(event -> {
        	if (event.getValue()) {
        		filter = "departureCountry";
        	} else {
        		filter = ""; 
        	}
        });

        searchBar.getElement().addEventListener("value-changed", event -> {
            closeIcon.setVisible(!searchBar.getValue().isEmpty());           
            
            dataProvider.clearFilters();
            
            if (filter.equals("vesselID")) {
            	dataProvider.addFilter(
                        v -> StringUtils.containsIgnoreCase(
                        		String.valueOf(v.getVesselID()), searchBar.getValue()));
            } else if (filter.equals("capacity")) {
            	dataProvider.addFilter(
                        v -> StringUtils.containsIgnoreCase(
                        		String.valueOf(v.getCapacity()), searchBar.getValue()));
            } else if (filter.equals("arrivalDate")) {
            	dataProvider.addFilter(
                        v -> StringUtils.containsIgnoreCase(
                        		UIUtils.formatSqlDate(v.getArivalDate()), searchBar.getValue()));
            } else if (filter.equals("departDate")) {
            	dataProvider.addFilter(
                        v -> StringUtils.containsIgnoreCase(
                        		UIUtils.formatSqlDate(v.getDepartDate()), searchBar.getValue()));
            } else if (filter.equals("destinationCountry")) {
            	dataProvider.addFilter(
                        v -> StringUtils.containsIgnoreCase(
                        		v.getDestinationCountry(), searchBar.getValue()));
            } else if (filter.equals("departureCountry")) {
            	dataProvider.addFilter(
                        v -> StringUtils.containsIgnoreCase(
                        		v.getDepartedFromCountry(), searchBar.getValue()));
            }
            
        }).debounce(300, DebouncePhase.TRAILING);
        
        Div filters = new Div(byID,byCapacity,byAdate,byDdate,byDestCountry,byDepartCountry);
        
        return new HorizontalLayout(filters, searchBar);
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

	private DetailsDrawer createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Vessel Details");
		detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
		detailsDrawer.setHeader(detailsDrawerHeader);

		return detailsDrawer;
	}

	private void showDetails(Vessel vessel) {
		detailsDrawer.setContent(createDetails(vessel));
		detailsDrawer.show();
	}

	private Component createDetails(Vessel vessel) {
		ListItem status = new ListItem(
				VaadinIcon.ANCHOR.create(), String.valueOf(vessel.getVesselID()), "Vessel");

		status.getContent().setAlignItems(FlexComponent.Alignment.BASELINE);
		status.getContent().setSpacing(Bottom.XS);
		UIUtils.setTheme("Success",
				status.getPrimary());
		ListItem from = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.UPLOAD_ALT),
					createDepartureLocation(vessel), "Departure");
		ListItem to = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.DOWNLOAD_ALT),
					createDestionation(vessel), "Destination");
		ListItem amount = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.SCALE),
				UIUtils.formatAmount(vessel.getCapacity()), "Capacity");
		ListItem dateArival = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.CALENDAR),
				UIUtils.formatSqlDate(vessel.getArivalDate()), "Arrival Date");
		ListItem dateDeparture = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.CALENDAR),
				UIUtils.formatSqlDate(vessel.getDepartDate()), "Departure Date");

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
    	upload.setMaxHeight("40px");
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

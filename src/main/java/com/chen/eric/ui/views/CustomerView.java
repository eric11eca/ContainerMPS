package com.chen.eric.ui.views;

import com.chen.eric.backend.Customer;
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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.DebouncePhase;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@PageTitle("Customer")
@Route(value = "customer", layout = MainLayout.class)
public class CustomerView extends SplitViewFrame {

	private Grid<Customer> grid;
	private ListDataProvider<Customer> dataProvider;
	private DetailsDrawer detailsDrawer;
	private String filter = "";
	private Customer tempCustomer;
	private Integer customerID;
	
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
        searchFilter.setItems("CustomerID", "Company Name", "Contact Email", "Country");
        searchFilter.setLabel("Search Filter");
        searchFilter.addValueChangeListener(e -> filter = e.getValue());

        searchBar.getElement().addEventListener("value-changed", event -> {
            closeIcon.setVisible(!searchBar.getValue().isEmpty());  
            
            if (filter.isEmpty() || searchBar.getValue().isEmpty()) {
            	dataContainer.getCustomerRecords();
            } else {
            	dataContainer.getCustomerRecordsByParams(filter, searchBar.getValue());
            }
            
	        dataProvider = DataProvider.ofCollection(dataContainer.customerRecords.values());
	        grid.setDataProvider(dataProvider);
        }).debounce(300, DebouncePhase.TRAILING);
        
        Button addContainer = UIUtils.createPrimaryButton("Add Container");
        addContainer.setWidthFull();
        addContainer.addClickListener(e-> {
        	createAddCustomer().open();
        });
        
        HorizontalLayout toolBar = new HorizontalLayout(addContainer, searchFilter, searchBar);
        toolBar.setAlignItems(Alignment.BASELINE);
        toolBar.setSpacing(true);
        toolBar.setPadding(true);
        
        return toolBar;
	}

	private Grid<Customer> createGrid() {
		dataContainer.getCustomerRecords();
		dataProvider = DataProvider.ofCollection(dataContainer.customerRecords.values());
		grid = new Grid<>();
		grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(this::showDetails));
		grid.setDataProvider(dataProvider);
		grid.setHeightByRows(true);
		grid.setWidthFull();
		
		grid.addColumn(Customer::getCustomerID)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Customer ID");
		grid.addColumn(Customer::getCompanyName)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Company Name");
		grid.addColumn(Customer::getContactEmail)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Contact Email");
		grid.addColumn(Customer::getCountry)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Country");
		grid.addColumn(Customer::getState)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("State");
		grid.addColumn(new ComponentRenderer<>(this::createRemoveButton))
				.setFlexGrow(0).setWidth("130px")
				.setResizable(true)
				.setTextAlign(ColumnTextAlign.CENTER);
		return grid;
	}
	
	private Button createRemoveButton(Customer customer) {
		Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            dataContainer.deleteCustomerRecords(customer.getCustomerID());
            dataContainer.getCustomerRecords();
    		dataProvider = DataProvider.ofCollection(dataContainer.customerRecords.values());
    		grid.setDataProvider(dataProvider);
        });
        button.setClassName("delete-button");
        button.addThemeName("small");
        return button;
    }
	
	private Dialog createAddCustomer() {
		Dialog addPanel = new Dialog();
		Customer newCustomer = new Customer(0,"","","","");
		
		TextField updateID = new TextField();
		updateID.setWidth("50%");
		updateID.setLabel("Customer ID");
		updateID.addValueChangeListener(e-> {
			newCustomer.setCustomerID(Integer.valueOf(e.getValue()));
		});
		
		TextField updateName = new TextField();
		updateName.setWidth("50%");
		updateName.setLabel("Company Name");
		updateName.addValueChangeListener(e-> {
			newCustomer.setCompanyName(e.getValue());
		});
        
		EmailField updateEmail = new EmailField();
		updateEmail.setWidth("50%");
		updateEmail.setLabel("Contact Email");
		updateEmail.addValueChangeListener(e-> {
        	newCustomer.setContactEmail(e.getValue());
		});
		
		TextField updateCountry = new TextField();
		updateCountry.setWidth("30%");
		updateCountry.setLabel("Country");
		updateCountry.addValueChangeListener(e-> {
			newCustomer.setCountry(e.getValue());
		});
		
		TextField updateState = new TextField();
		updateState.setWidth("30%");
		updateState.setLabel("State");
		updateState.addValueChangeListener(e-> {
			newCustomer.setState(e.getValue());
		});
        
        
        HorizontalLayout idLayer = new HorizontalLayout(updateID, updateName, updateEmail);
        idLayer.setAlignItems(Alignment.BASELINE);

		HorizontalLayout addrLayer = new HorizontalLayout(updateCountry, updateState);
		addrLayer.setAlignItems(Alignment.BASELINE);
		
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e->{
			if (newCustomer.getCustomerID() == null) {
				Notification.show("Customer ID cannot be empty!");
			} else {
				int code = dataContainer.insertCustomerRecords(newCustomer);
				
				if (code == 0) {
					Notification.show("Succesfully Inserted the Container!", 4000, Notification.Position.BOTTOM_CENTER);
					dataContainer.getCustomerRecords();
			        dataProvider = DataProvider.ofCollection(dataContainer.customerRecords.values());
			        grid.setDataProvider(dataProvider);
			        addPanel.close();
				} else if (code == 1) {
					Notification.show("The given CustomerID already exits!", 4000, Notification.Position.BOTTOM_CENTER);
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
				idLayer, addrLayer, detailsDrawerFooter);
		addPanel.add(content);
		return addPanel;
	}

	private DetailsDrawer createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);

		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Customer Details");
		detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
		
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e -> {
			if (tempCustomer != null && customerID != null) {
				int code = dataContainer.updateCustomerRecords(tempCustomer, customerID);
				if (code == 0) {
					dataContainer.getCustomerRecords();
					dataProvider = DataProvider.ofCollection(dataContainer.customerRecords.values());
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

	private void showDetails(Customer customer) {
		tempCustomer = new Customer();
		customerID = customer.getCustomerID();
		detailsDrawer.setContent(createDetails(customer));
		detailsDrawer.show();
	}

	private Component createDetails(Customer customer) {
		TextField updateID = new TextField();
		updateID.setValue(String.valueOf(customer.getCustomerID()));
		updateID.setWidth("50%");
		updateID.addValueChangeListener(e-> {
			tempCustomer.setCustomerID(Integer.valueOf(e.getValue()));
		});
		
		TextField updateName = new TextField();
		updateName.setWidth("50%");
		updateName.setValue(String.valueOf(customer.getCompanyName()));
		updateName.addValueChangeListener(e-> {
			tempCustomer.setCompanyName(e.getValue());
		});
        
		EmailField updateEmail = new EmailField();
        updateEmail.setWidth("50%");
        updateEmail.setValue(customer.getContactEmail());
        updateEmail.addValueChangeListener(e-> {
        	tempCustomer.setContactEmail(e.getValue());
		});
		
        TextField updateCountry = new TextField();
		updateCountry.setWidth("30%");
		updateCountry.setValue(customer.getCountry());
		updateCountry.addValueChangeListener(e-> {
			tempCustomer.setCountry(e.getValue());
		});
        
		TextField updateState = new TextField();
        updateState.setWidth("20%");
        updateState.setValue(customer.getState());
        updateState.addValueChangeListener(e-> {
        	tempCustomer.setState(e.getValue());
		});
        
        HorizontalLayout idLayer = new HorizontalLayout(updateID, updateName, updateEmail);
        idLayer.setAlignItems(Alignment.BASELINE);

		HorizontalLayout addrLayer = new HorizontalLayout(updateCountry, updateState);
		addrLayer.setAlignItems(Alignment.BASELINE);
		
		ListItem status = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.PACKAGE), updateID, "Customer ID");
		
		status.getContent().setAlignItems(FlexComponent.Alignment.BASELINE);
		status.getContent().setSpacing(Bottom.XS);
		
		ListItem company = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.GRID_BEVEL),
				updateName , "Company");
		ListItem contact = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.DOLLAR),
				updateEmail, "Contact");
		ListItem address = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.CLIPBOARD),
				addrLayer, "Address");

		for (ListItem item : new ListItem[]{
				status, company, contact, address}) {
			item.setReverse(true);
			item.setWhiteSpace(WhiteSpace.PRE_LINE);
		}

		Div details = new Div(status, company, contact, address);
		details.addClassName(LumoStyles.Padding.Vertical.S);
		return details;
	}	
}

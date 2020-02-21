package com.chen.eric.ui.views;

import org.springframework.security.access.annotation.Secured;

import com.chen.eric.backend.Employee;
import com.chen.eric.backend.Role;
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
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.DebouncePhase;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@PageTitle("Employee")
@Secured({Role.SystemAdmin, Role.SuperManager, Role.HumanResource})
@Route(value = "employee", layout = MainLayout.class)
public class EmployeeView extends SplitViewFrame {
	private Grid<Employee> grid;
	private ListDataProvider<Employee> dataProvider;
	private DetailsDrawer detailsDrawer;
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
        searchFilter.setItems("None", "SSN", "Name", "Role");
        searchFilter.setLabel("Search Filter");
        searchFilter.addValueChangeListener(e -> filter = e.getValue());

        searchBar.getElement().addEventListener("value-changed", event -> {
            closeIcon.setVisible(!searchBar.getValue().isEmpty());  
            
            if (filter.isEmpty() || searchBar.getValue().isEmpty()) {
            	dataContainer.getEmployeeRecords();
            } else {
            	dataContainer.getEmployeeRecordsByParams(filter, searchBar.getValue());
            }
            
	        dataProvider = DataProvider.ofCollection(dataContainer.employeeRecords.values());
	        grid.setDataProvider(dataProvider);
        }).debounce(300, DebouncePhase.TRAILING);
        
        HorizontalLayout toolBar = new HorizontalLayout(searchFilter, searchBar);
        toolBar.setAlignItems(Alignment.BASELINE);
        toolBar.setSpacing(true);
        toolBar.setPadding(true);
        
        return toolBar;
	}

	private Grid<Employee> createGrid() {
		dataContainer.getEmployeeRecords();
		dataProvider = DataProvider.ofCollection(dataContainer.employeeRecords.values());

		grid = new Grid<>();
		grid.addSelectionListener(event -> 
			event.getFirstSelectedItem().ifPresent(this::showDetails));
		grid.setDataProvider(dataProvider);
		grid.setHeightByRows(true);
		grid.setWidthFull();

		grid.addColumn(Employee::getUserName)
			.setAutoWidth(true)
			.setFlexGrow(0)
			.setSortable(true)
			.setHeader("User Name");
		grid.addColumn(Employee::getSSN)
			.setAutoWidth(true)
			.setFlexGrow(0)
			.setSortable(true)
			.setHeader("SSN");
		grid.addColumn(Employee::getName)
			.setAutoWidth(true)
			.setFlexGrow(0)
			.setSortable(true)
			.setHeader("Name");
		grid.addColumn(Employee::getRole)
			.setAutoWidth(true)
			.setFlexGrow(0)
			.setSortable(true)
			.setHeader("Role");
		grid.addComponentColumn(this::createActive)
			.setAutoWidth(true)
			.setFlexGrow(0)
			.setSortable(true)
			.setHeader("Enabled");
		grid.addComponentColumn(this::createActionBar)
			.setAutoWidth(true)
			.setFlexGrow(0);
		
		return grid;
	}
	
	private Component createActive(Employee employee) {
		Icon icon;
		if (employee.isEnabled()) {
			icon = UIUtils.createSecondaryIcon(VaadinIcon.CHECK);
		} else {
			icon = UIUtils.createDisabledIcon(VaadinIcon.CLOSE);
		}
		return icon;
	}
	
	private Component createActionBar(Employee employee) {
		HorizontalLayout actionBar = new HorizontalLayout(
				createRolePicker(employee),
				createActiveButton(employee),
				createDeactivateButton(employee),
				createRemoveButton(employee));
		actionBar.setAlignItems(Alignment.BASELINE);
		actionBar.setPadding(true);
		actionBar.setSpacing(true);
		return actionBar;
	}
	
	private Button createActiveButton(Employee employee) {
		Button button = new Button(new Icon(VaadinIcon.PLAY), clickEvent -> {
			int code = dataContainer.updateEmployeeRecords(employee, 1);
            if (code == 0) {
				dataContainer.getEmployeeRecords();
	    		dataProvider = DataProvider.ofCollection(dataContainer.employeeRecords.values());
	    		grid.setDataProvider(dataProvider);
	    		Notification.show("Succesfully Enabled this user!", 4000, Notification.Position.BOTTOM_CENTER);
			} else if (code == 1) {
				Notification.show("This employee does not exist!");
			} else {
				Notification.show("ERROR: ACTIVATION FAILED!");
			}
        });
		if (employee.isEnabled()) {
			button.setEnabled(false);
		}
        button.addThemeName("small");
        return button;
    } 
	
	private Button createDeactivateButton(Employee employee) {
		Button button = new Button(new Icon(VaadinIcon.PAUSE), clickEvent -> {
			int code = dataContainer.updateEmployeeRecords(employee, 2);
            if (code == 0) {
				dataContainer.getEmployeeRecords();
	    		dataProvider = DataProvider.ofCollection(dataContainer.employeeRecords.values());
	    		grid.setDataProvider(dataProvider);
	    		Notification.show("Succesfully Disabled this user!", 4000, Notification.Position.BOTTOM_CENTER);
			} else if (code == 1) {
				Notification.show("This employee does not exist!", 4000, Notification.Position.BOTTOM_CENTER);
			} else {
				Notification.show("ERROR: DEACTIVATION FAILED!", 4000, Notification.Position.BOTTOM_CENTER);
			}
        });
		if (!employee.isEnabled()) {
			button.setEnabled(false);
		}
        button.addThemeName("small");
        return button;
    } 
	
	/*private Button createRollBackButton(Vessel vessel) {
		Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            dataContainer.deleteVesselRecords(vessel.getVesselID());
            dataContainer.getVesselRecords();
    		dataProvider = DataProvider.ofCollection(dataContainer.employeeRecords.values());
    		grid.setDataProvider(dataProvider);
        });
        button.setClassName("delete-button");
        button.addThemeName("small");
        return button;
    }*/
	
	private Select<String> createRolePicker(Employee employee) {
		Select<String> rolePicker = new Select<>();
		rolePicker.setItems(
			"Import Plan Manager", "Export Plan Manager", 
			"Storage Area Maintainer", "Container Infomation Recorder",
			"Vessel Dispatcher", "Container Distributor",
			"Human Resource", "Customer Communicator", 
			"Customer Financial Manager");
		rolePicker.setWidth("30%");
		rolePicker.addValueChangeListener(e -> {
				Employee tmp = new Employee();
				tmp.setSSN(employee.getSSN());
				tmp.setRole(e.getValue()); 
				if (!e.getValue().isEmpty()) {
					int code = dataContainer.updateEmployeeRecords(tmp, 3);
					if (code == 0) {
						Notification.show("Succesfully Updated this user's role!", 
								4000, Notification.Position.BOTTOM_CENTER);
						dataContainer.getEmployeeRecords();
			    		dataProvider = DataProvider.ofCollection(dataContainer.employeeRecords.values());
			    		grid.setDataProvider(dataProvider);
					} else if (code == 1) {
						Notification.show("This employee does not exist!",
								4000, Notification.Position.BOTTOM_CENTER);
					} else if (code == 2) {
						Notification.show("This employee is not activated!",
								4000, Notification.Position.BOTTOM_CENTER);
					} else {
						Notification.show("ERROR: UPDATE FAILED!",
								4000, Notification.Position.BOTTOM_CENTER);
					}
				}
				rolePicker.setValue("");
		});
        return rolePicker;
    }
	
	private Button createRemoveButton(Employee employee) {
		Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            int code = dataContainer.deleteEmployeeRecords(employee.getSSN());
            if (code == 0) {
				dataContainer.getEmployeeRecords();
	    		dataProvider = DataProvider.ofCollection(dataContainer.employeeRecords.values());
	    		grid.setDataProvider(dataProvider);
	    		Notification.show("Succesfully deleted this user!", 
						4000, Notification.Position.BOTTOM_CENTER);
			} else if (code == 1) {
				Notification.show("This employee does not exist!",
						4000, Notification.Position.BOTTOM_CENTER);
			} else {
				Notification.show("ERROR: DELETION FAILED!", 
						4000, Notification.Position.BOTTOM_CENTER);
			}
        });
        button.setClassName("delete-button");
        button.addThemeName("small");
        return button;
    } 

	private DetailsDrawer createDetailsDrawer() {
		detailsDrawer = new DetailsDrawer(DetailsDrawer.Position.RIGHT);
		DetailsDrawerHeader detailsDrawerHeader = new DetailsDrawerHeader("Employee Details");
		detailsDrawerHeader.addCloseListener(buttonClickEvent -> detailsDrawer.hide());
		detailsDrawer.setHeader(detailsDrawerHeader);
		return detailsDrawer;
	}

	private void showDetails(Employee employee) {
		detailsDrawer.setContent(createDetails(employee));
		detailsDrawer.show();
	}

	private Component createDetails(Employee employee) {
		ListItem status = new ListItem(UIUtils.createTertiaryIcon(VaadinIcon.USER),
				employee.getUserName(), "UserName");

		status.getContent().setAlignItems(FlexComponent.Alignment.BASELINE);
		status.getContent().setSpacing(Bottom.XS);

		ListItem ssn = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.LOCK), 
				String.valueOf(employee.getSSN()), "SSN");
		ListItem role = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.USERS), 
				employee.getRole(), "Role");
		ListItem name = new ListItem(
				UIUtils.createTertiaryIcon(VaadinIcon.MALE),
				employee.getName(), "Name");

		for (ListItem item : new ListItem[]{status, ssn, role, name}) {
			item.setReverse(true);
			item.setWhiteSpace(WhiteSpace.PRE_LINE);
		}

		Div details = new Div(status, ssn, role, name);
		details.addClassName(LumoStyles.Padding.Vertical.S);
		return details;
	}
}

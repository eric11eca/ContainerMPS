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
import com.chen.eric.ui.components.ListItem;
import com.chen.eric.ui.components.detailsdrawer.DetailsDrawer;
import com.chen.eric.ui.components.detailsdrawer.DetailsDrawerFooter;
import com.chen.eric.ui.components.detailsdrawer.DetailsDrawerHeader;
import com.chen.eric.ui.layout.size.Bottom;
import com.chen.eric.ui.util.LumoStyles;
import com.chen.eric.ui.util.UIUtils;
import com.chen.eric.ui.util.css.WhiteSpace;
import com.chen.eric.ui.util.css.lumo.BadgeColor;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@PageTitle("Storage")
@Route(value = "storage", layout = MainLayout.class)
public class StorageAreaView extends Dialog{
	private Grid<StorageArea> storageGrid;
	private ListDataProvider<StorageArea> storageDataProvider;
	private StorageArea tempStorageArea;
	private Integer storageID;
	private DataContainer dataContainer = DataContainer.getInstance();

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		//add(createContent());
		//add(createDetailsDrawer());
	}

	private Component createStorageContent() {
		 Button addContainer = UIUtils.createPrimaryButton("Add StorageArea");
	        addContainer.setWidthFull();
	        addContainer.addClickListener(e-> {
	        	createAddContainer().open();
	        });
		VerticalLayout content = new VerticalLayout(addContainer, createStorageGrid());
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
            int code = dataContainer.deleteVesselRecords(storageArea.getStorageID());
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
		
		DetailsDrawerFooter detailsDrawerFooter = new DetailsDrawerFooter();
		detailsDrawerFooter.addSaveListener(e -> {
			if (tempStorageArea != null && storageID != null) {
				int code = dataContainer.updateStorageAreaRecords(tempStorageArea, storageID);
				if (code == 10) {
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

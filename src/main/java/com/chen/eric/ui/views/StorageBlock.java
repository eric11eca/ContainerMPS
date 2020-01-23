package com.chen.eric.ui.views;

import java.util.ArrayList;
import java.util.List;

import com.chen.eric.backend.StorageBlockColumn;
import com.chen.eric.ui.MainLayout;
import com.chen.eric.ui.components.Badge;
import com.chen.eric.ui.util.UIUtils;
import com.chen.eric.ui.util.css.lumo.BadgeColor;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Storage Area Visualization")
@Route(value = "storage", layout = MainLayout.class)
@SuppressWarnings("serial")
public class StorageBlock extends SplitViewFrame{
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		setViewContent(createContent());
	}

	private Component createContent() {
		HorizontalLayout slotLayer1 = new HorizontalLayout();
		HorizontalLayout slotLayer2 = new HorizontalLayout();
		
		Button B11 = UIUtils.createPrimaryButton("B11");
		Button B12 = UIUtils.createPrimaryButton("B12");
		Button B13 = UIUtils.createPrimaryButton("B13");
		Button B21 = UIUtils.createPrimaryButton("B21");
		Button B22 = UIUtils.createPrimaryButton("B22");
		Button B23 = UIUtils.createPrimaryButton("B23");
		
		B11.addClickListener(
			    e -> this.setViewContent(blockContent("B11")));
		
		slotLayer1.add(B11, B12, B13);
		slotLayer2.add(B21, B22, B23);
		
		slotLayer1.setAlignItems(Alignment.BASELINE);
		slotLayer1.setSpacing(true);
		slotLayer1.setPadding(true);
		
		slotLayer2.setAlignItems(Alignment.BASELINE);
		slotLayer2.setSpacing(true);
		slotLayer2.setPadding(true);
		
		VerticalLayout block = new VerticalLayout(slotLayer1, slotLayer2);
		return block;
	}
	
	private Component blockContent(String blockID) {
		List<StorageBlockColumn> blockLayerList = new ArrayList<>();
		blockLayerList.add(new StorageBlockColumn());
		blockLayerList.add(new StorageBlockColumn());
		blockLayerList.add(new StorageBlockColumn());
		blockLayerList.add(new StorageBlockColumn());
		blockLayerList.add(new StorageBlockColumn());
		StorageBlockColumn location = new StorageBlockColumn();
		location.setSlot5(true);
		location.setSlot3(true);
		blockLayerList.add(location);
		
		Grid<StorageBlockColumn> blockLayer1 = createGrid("Row1", blockLayerList);
		Grid<StorageBlockColumn> blockLayer2 = createGrid("Row2", blockLayerList);
		Grid<StorageBlockColumn> blockLayer3 = createGrid("Row3", blockLayerList);
		Grid<StorageBlockColumn> blockLayer4 = createGrid("Row4", blockLayerList);
		
		return new VerticalLayout(blockLayer1,blockLayer2,blockLayer3,blockLayer4);
	}

	private Grid<StorageBlockColumn> createGrid(String layerName,
			List<StorageBlockColumn> blockLayerList) {
		Grid<StorageBlockColumn> blockLayer = new Grid<>();
		blockLayer.setDataProvider(DataProvider.ofCollection(blockLayerList));
		
		blockLayer.addComponentColumn(c-> drawSlot(c,"1"))
			.setWidth("80px").setHeader(new Badge(layerName, BadgeColor.SUCCESS));
		blockLayer.addComponentColumn(c-> drawSlot(c,"2")).setWidth("80px");
		blockLayer.addComponentColumn(c-> drawSlot(c,"3")).setWidth("80px");
		blockLayer.addComponentColumn(c-> drawSlot(c,"4")).setWidth("80px");
		blockLayer.addComponentColumn(c-> drawSlot(c,"5")).setWidth("80px");
		blockLayer.addComponentColumn(c-> drawSlot(c,"6")).setWidth("80px");
		blockLayer.addComponentColumn(c-> drawSlot(c,"7")).setWidth("80px");
		blockLayer.addComponentColumn(c-> drawSlot(c,"8")).setWidth("80px");
		blockLayer.addComponentColumn(c-> drawSlot(c,"9")).setWidth("80px");
		blockLayer.addComponentColumn(c-> drawSlot(c,"10")).setWidth("80px");
		
		blockLayer.setHeightByRows(true);
		blockLayer.setWidth("850px");
		return blockLayer;
	}
	
	private Component drawSlot(StorageBlockColumn column, String index) {
		Button slot = UIUtils.createButton(index, ButtonVariant.LUMO_ICON);
		slot.setWidth("10px");
		
		Boolean slotUsed = false;
		switch (index) {
			case "1":
				slotUsed = column.isSlot1();
				break;
			case "2":
				slotUsed = column.isSlot2();
				break;
			case "3":
				slotUsed = column.isSlot3();
				break;
			case "4":
				slotUsed = column.isSlot4();
				break;
			case "5":
				slotUsed = column.isSlot5();
				break;
			case "6":
				slotUsed = column.isSlot6();
				break;
			case "7":
				slotUsed = column.isSlot7();
				break;
			case "8":
				slotUsed = column.isSlot8();
				break;
			case "9":
				slotUsed = column.isSlot9();
				break;
			case "10":
				slotUsed = column.isSlot10();
				break;
		}
		
		slot.getStyle().set("color", "white");
		
		if (slotUsed) {
			slot.getStyle().set("backgroundColor", "red");
		} else {
			slot.getStyle().set("backgroundColor", "blue");
		}
		return slot;
	}
	
}

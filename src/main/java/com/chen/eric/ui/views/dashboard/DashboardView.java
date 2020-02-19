package com.chen.eric.ui.views.dashboard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;

import com.chen.eric.backend.ExportPlan;
import com.chen.eric.backend.ImportPlan;
import com.chen.eric.backend.TransPlan;
import com.chen.eric.backend.service.DataContainer;
import com.chen.eric.ui.MainLayout;
import com.chen.eric.ui.components.Badge;
import com.chen.eric.ui.components.FlexBoxLayout;
import com.chen.eric.ui.components.ListItem;
import com.chen.eric.ui.util.UIUtils;
import com.chen.eric.ui.util.css.FlexDirection;
import com.chen.eric.ui.util.css.lumo.BadgeColor;
import com.chen.eric.ui.views.ViewFrame;
import com.helger.commons.csv.CSVReader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

@SuppressWarnings("serial")
@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard")
@CssImport(value = "styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class DashboardView extends ViewFrame {
    private final H2  vesselCount = new H2();
    
    private ImportPlan currentImportPlan;
    private ExportPlan currentExportPlan;
    
    private Grid<TransPlan> planGrid;
    private ListDataProvider<TransPlan> dataProvider;
    private File tempFile;
    private BOMInputStream in;
    
    private DataContainer dataContainer = DataContainer.getInstance();
    
    private Board board; 
    private Row editPlanRow;
    
    private WrapperCard planGridWrapper;
    private WrapperCard planDetailWrapper;

    public DashboardView() {    	
    	board = new Board();
    	vesselCount.setText(String.valueOf(dataContainer.countVessel()));
        board.addRow(
                createUploadBadge("Import List", "Upload Import Container List", "badge"),          
                createUploadBadge("Export List", "Upload Export Container List", "badge success"),  
                vesselCountBadge("Vessel Count", vesselCount, "error-text", "Number of vessels docked", "badge error")
        );
    
        /*createUploadBadge("Conversion", "error-text", "User conversion rate", "badge error")
        monthlyVisitors.getConfiguration()
                .setTitle("Monthly visitors per city");
        monthlyVisitors.getConfiguration().getChart().setType(ChartType.COLUMN);
        WrapperCard monthlyVisitorsWrapper = new WrapperCard("wrapper",
                new Component[] { monthlyVisitors }, "card");
        board.add(monthlyVisitorsWrapper);*/
        
        createPlanGrid();
        updatePlanRow(true);
        
        planGridWrapper = new WrapperCard("wrapper", new Component[] {planGrid}, "card");
        editPlanRow = new Row();
        editPlanRow.add(planGridWrapper, planDetailWrapper);
        board.add(editPlanRow);
        
        FlexBoxLayout content = new FlexBoxLayout(board);
		content.setAlignItems(FlexComponent.Alignment.CENTER);
		content.setFlexDirection(FlexDirection.COLUMN);
		setViewContent(content);
    }

    
    private Dialog generatePlanPanel() {
    	Dialog dialog = new Dialog();
    	Upload uploader = uploadLists();
    	
    	TextField signature = new TextField();
    	signature.setLabel("Electric Signature(User Name");
    	
    	Button generate = UIUtils.createPrimaryButton("Execute");
    	generate.addClickListener(e -> {
    		buildContainerFromCSV(in, signature.getValue());
    	});
    	
    	Div panel = new Div();
    	panel.add(uploader, signature, generate);
    	dialog.add(panel);
    	dialog.setSizeFull();
    	
    	return dialog;
    }

    private WrapperCard createUploadBadge(String title, String description, String badgeTheme) {
        Span titleSpan = new Span(title);
        titleSpan.getElement().setAttribute("theme", badgeTheme);

        Span descriptionSpan = new Span(description);
        descriptionSpan.addClassName("secondary-text");
        
        Button importUpload = UIUtils.createTertiaryButton("Upload Import List");
        importUpload.addClickListener(e -> {
        	generatePlanPanel().open();
        });

        return new WrapperCard("wrapper", new Component[] {
        		titleSpan, importUpload, descriptionSpan}, "card", "space-m");
    }
    
    private WrapperCard vesselCountBadge(String title, H2 h2, String h2ClassName, String description, String badgeTheme) {
    	 Span titleSpan = new Span(title);
         titleSpan.getElement().setAttribute("theme", badgeTheme);

         h2.addClassName(h2ClassName);

         Span descriptionSpan = new Span(description);
         descriptionSpan.addClassName("secondary-text");

         return new WrapperCard("wrapper", new Component[] {
         		titleSpan, h2, descriptionSpan}, "card", "space-m");
    }
    
    private HorizontalLayout createPlanCard(TransPlan plan) {
    	Label planID = UIUtils.createH3PlanTitle(String.valueOf(plan.planID));
    	Label planType = UIUtils.createH3Label(plan.type.trim() + " Plan");
    	
    	Badge planStatus = new Badge(
    			plan.status, BadgeColor.getThemeByStatus(plan.status));

    	ListItem titleItem = new ListItem();
    	titleItem.setPlanListItem(planType, plan.manager);
    	ListItem idItem = new ListItem();
    	idItem.setPlanListItem(planID, UIUtils.formatSqlDate(plan.date));
    	
    	HorizontalLayout planCard = new HorizontalLayout(planStatus, titleItem, idItem);
    	planCard.setSpacing(true);
    	planCard.setPadding(true);
    	return planCard;
    }
    
    private void showPlanDetail(TransPlan plan) {
    	if (plan.type.contains("Import")) {
			currentImportPlan = getImportPlan(plan);
			updatePlanRow(true);
		} else if (plan.type.contains("Export")) {
			currentExportPlan = getExportPlan(plan);
			updatePlanRow(false);
		}
    	editPlanRow.removeAll();
    	editPlanRow.add(planGridWrapper, planDetailWrapper);
    	board.remove(editPlanRow);
    	board.add(editPlanRow);
    }
    
    private void createPlanGrid() {
    	planGrid = new Grid<>();
    	
    	planGrid.addSelectionListener(e -> {
    		e.getFirstSelectedItem().ifPresent(this::showPlanDetail);
    	});
    	
    	dataContainer.getPlanRecords();	
    	dataProvider = DataProvider.ofCollection(
    			dataContainer.transPlanRecords.values());
    	planGrid.setDataProvider(dataProvider);
    	planGrid.setWidthFull();
		
    	planGrid.addComponentColumn(this::createPlanCard)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true);
    	planGrid.addComponentColumn(this::createRemoveButton)
				.setFlexGrow(0).setWidth("130px")
				.setResizable(true)
				.setTextAlign(ColumnTextAlign.CENTER);
	}

	private Button createRemoveButton(TransPlan plan) {
		Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            int code = dataContainer.deletePlanRecords(plan.getPlanID());
            if (code == 0) {
            	dataContainer.getPlanRecords();
        		dataProvider = DataProvider.ofCollection(dataContainer.transPlanRecords.values());
        		planGrid.setDataProvider(dataProvider);
        		Notification.show("Succefully Deleted the plan!", 4000, Notification.Position.BOTTOM_CENTER);
            } else {
            	Notification.show("EEROR: DELETION FAILED!");
            }       
        });
        button.setClassName("delete-button");
        button.addThemeName("small");
        return button;
    }
    
    private ImportPlan getImportPlan(TransPlan plan) {
    	return dataContainer.importPlanRecords.get(String.valueOf(plan.planID));
    }
    
    private ExportPlan getExportPlan(TransPlan plan) {
    	return dataContainer.exportPlanRecords.get(String.valueOf(plan.planID));
    }
    
	private WrapperCard initNoPlan() {
		WrapperCard noPlan = new WrapperCard("wrapper",
		        new Component[] {new H3("No Plan Selected")}, "card");
		return noPlan;
	}
	
	private void updatePlanRow(boolean isImport) {
		if (isImport) {
			if (currentImportPlan == null) {
				planDetailWrapper = initNoPlan();
			} else {
				try {
					HorizontalLayout planDetail = new HorizontalLayout(
							createPlanEditor(true), createGrpah(true));
					planDetailWrapper = new WrapperCard("wrapper",
					        new Component[] {new H3("Import Plan"),  planDetail}, "card");
				} catch (IOException e) {
					e.printStackTrace();
					planDetailWrapper = initNoPlan();
				}
			}
		} else {
			if (currentExportPlan == null) {
				planDetailWrapper = initNoPlan();
			} else {
				try {
					HorizontalLayout planDetail = new HorizontalLayout(
							createPlanEditor(false), createGrpah(false));
					planDetailWrapper = new WrapperCard("wrapper",
					        new Component[] {new H3("Export Plan"),  planDetail}, "card");
				} catch (IOException e) {
					e.printStackTrace();
					planDetailWrapper = initNoPlan();
				}
			}
		}
	}
    
    private VerticalLayout createImportTasks() {
    	/*=== Define Checkpoints ===*/
   	 	Checkbox unloadComplet = new Checkbox();
        unloadComplet.setLabel("Unload Container");
        if (currentImportPlan.isUnLoadCompleted()) {
        	unloadComplet.setValue(true);
        }
        unloadComplet.addValueChangeListener(e -> {
        	currentImportPlan.setUnLoadCompleted(e.getValue());
        	dataContainer.updateUnLoadCompleted(
        			e.getValue(), currentImportPlan.planID, 
        			currentImportPlan.getContainerID());
        	Notification.show("Unload Container Completed",
					4000, Notification.Position.BOTTOM_CENTER);
        });
        
        Checkbox checkComplet = new Checkbox();
        checkComplet.setLabel("Custom Check");
        if (currentImportPlan.isCustomPassed()) {
        	checkComplet.setValue(true);
        }
        checkComplet.addValueChangeListener(e -> {
        	currentImportPlan.setCustomPassed(e.getValue());
	       	dataContainer.updateCustomPassed(
	       			e.getValue(), currentImportPlan.planID, 
	       			currentImportPlan.getContainerID()); 
	       	Notification.show("Cuatomer Check Completed",
					4000, Notification.Position.BOTTOM_CENTER);
        });
        
        Checkbox distirbuteComplet = new Checkbox();
        distirbuteComplet.setLabel("Distribute Container");
        if (currentImportPlan.isContainerDistributed()) {
        	distirbuteComplet.setValue(true);
        }
        distirbuteComplet.addValueChangeListener(e -> {
        	currentImportPlan.setContainerDistributed(e.getValue());
    		dataContainer.updateContainerDistributed(
    			e.getValue(), currentImportPlan.planID, 
    			currentImportPlan.getContainerID());
    		Notification.show("Distribute Container Completed",
					4000, Notification.Position.BOTTOM_CENTER);
        });
      
        VerticalLayout checkboxes = new VerticalLayout(
        		unloadComplet, checkComplet, distirbuteComplet);
        checkboxes.setSpacing(true);
        checkboxes.setAlignItems(Alignment.BASELINE);
        return checkboxes;
   }
   
   private VerticalLayout createExportTasks() {
	   Checkbox retriveComplet = new Checkbox();
       retriveComplet.setLabel("Retrive Container");
       if (currentExportPlan.isContainerRetrived()) {
    	   retriveComplet.setValue(true);
       }
       retriveComplet.addValueChangeListener(e -> {
    	   currentExportPlan.setContainerRetrived(true);
    	   dataContainer.updateContainerRetrived(
    			e.getValue(), currentExportPlan.planID, 
    			currentExportPlan.getContainerID());
    	   Notification.show("Container Retrived",
					4000, Notification.Position.BOTTOM_CENTER);
      });
       
       Checkbox billingComplet = new Checkbox();
       billingComplet.setLabel("Service Billing");
       if (currentExportPlan.isServicePayed()) {
    	   billingComplet.setValue(true);
       }
       billingComplet.addValueChangeListener(e -> {
    	   currentExportPlan.setServicePayed(true);
    	   dataContainer.updateServicePayed(
    			   e.getValue(), currentExportPlan.planID, 
    			   currentExportPlan.getContainerID());
    	   Notification.show("Container Service Billed",
    			   4000, Notification.Position.BOTTOM_CENTER);
		});
       
       Checkbox loadComplet = new Checkbox();
       loadComplet.setLabel("Load Container");
       if (currentExportPlan.isContainerRetrived()) {
    	   loadComplet.setValue(true);
       }
       loadComplet.addValueChangeListener(e -> {
    	   currentExportPlan.setLoadComplete(true);
    	   dataContainer.updateLoadCompleted(
    			   e.getValue(), currentExportPlan.planID, 
    			   currentExportPlan.getContainerID());
    	   Notification.show("Container Loading Completed",
    			   4000, Notification.Position.BOTTOM_CENTER);
       });
       
       VerticalLayout checkboxes = new VerticalLayout(
    		   retriveComplet, billingComplet, loadComplet);
       checkboxes.setSpacing(true);
       checkboxes.setAlignItems(Alignment.BASELINE);
       return checkboxes;
  }
   
   private VerticalLayout createPlanEditor(boolean isImport) {
       VerticalLayout planEditor = new VerticalLayout();
       TextField manager = new TextField();
       manager.setLabel("Plan Manager");
       manager.setPlaceholder("Completion Signature(User Name)");
      
       Button complete = UIUtils.createPrimaryButton("Plan Complete");
       complete.addClickListener(e -> {
    	   
       });
       
       if (isImport) {
    	   planEditor.add(createImportTasks());
       } else {
    	   planEditor.add(createExportTasks());
       }
       planEditor.add(manager, complete);
       
       planEditor.setWidth("40%");
       return planEditor;
   }

   private String drawExportPlan() {
	   String exportDiagram = DataContainer.exportDiagram;
	   	
	   	if (currentExportPlan != null) {
	   		exportDiagram = exportDiagram.replace("${vesselID}", String.valueOf(currentExportPlan.getLoadTo()));
	       	exportDiagram = exportDiagram.replace("${containerID}", String.valueOf(currentExportPlan.getContainerID()));
	       	exportDiagram = exportDiagram.replace("${planID}", String.valueOf(currentExportPlan.planID));
	       	exportDiagram = exportDiagram.replace("${manager}", currentExportPlan.manager);
	       	exportDiagram = exportDiagram.replace("${status}", currentExportPlan.status);
	       	exportDiagram = exportDiagram.replace("${date}", currentExportPlan.date.toString());
	   	}
	   	return exportDiagram;
   }
   
   private String drawImportPlan() {
   		String importDiagram = DataContainer.importDiagram;
   	
	   	if (currentImportPlan != null) {
	   		importDiagram = importDiagram.replace("$vesselID", String.valueOf(currentImportPlan.getUnloadFrom()));
	       	importDiagram = importDiagram.replace("$containerID", String.valueOf(currentImportPlan.getContainerID()));
	       	importDiagram = importDiagram.replace("$planID", String.valueOf(currentImportPlan.planID));
	       	importDiagram = importDiagram.replace("$manager", currentImportPlan.manager);
	       	importDiagram = importDiagram.replace("$status", currentImportPlan.status);
	       	importDiagram = importDiagram.replace("$date", currentImportPlan.date.toString());
   		}
   	
   		return importDiagram;
   	}
   
	private Image createGrpah(boolean isImport) throws IOException {
		String digram = isImport? drawImportPlan() : drawExportPlan();
		SourceStringReader reader = new SourceStringReader(digram);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		FileFormatOption format = new FileFormatOption(FileFormat.SVG);
		reader.outputImage(os, format);
		
		StreamResource resource = new StreamResource("Plan.svg", 
				() -> new ByteArrayInputStream(os.toByteArray()));
		Image image = new Image(resource, "plan");
		image.setWidth("300px");
		image.setHeight("400px");
	
		os.close();
		return image;
	}
	
	private Upload uploadLists() {
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
				in = new BOMInputStream (new FileInputStream(tempFile));
				tempFile.delete();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		upload.setWidthFull();
		return upload;
	}
	
	private void buildContainerFromCSV(BOMInputStream  in, String manager) {
		CSVReader csvReader;
		int code = 1;
		int count = 0;
		try {
			csvReader = new CSVReader(new InputStreamReader(in,"utf-8"));
	    	List<String> record;
			while ((record = csvReader.readNext()) != null) {
				int planID =  DataContainer.getRandomNumber(00000000, 99999999);
				Date planDate = Date.valueOf(LocalDate.now());
				String status = "Incomplete";
				
				Integer containerID = Integer.valueOf(record.get(0));
				Integer vesselID = Integer.valueOf(record.get(1));
				String type = record.get(2);
				
				if (type.equals("Import")) {
					TransPlan importPlan = new ImportPlan(
							planID, manager, planDate, status, type,
							containerID, vesselID, false, false, false);
					code = dataContainer.insertPlanRecords(importPlan);
				} else {					
					double totalCost = dataContainer.calculateCost(containerID);
					TransPlan exportPlan = new ExportPlan(
							planID, manager, planDate, status, type, 
							containerID, vesselID, totalCost, false, false, false);
					code = dataContainer.insertPlanRecords(exportPlan);
				}
				count += (code==1)? 1 : 0;
			}
			
			if (code == 0) {
				dataContainer.getPlanRecords();
				dataProvider = DataProvider.ofCollection(
						dataContainer.transPlanRecords.values());
				planGrid.setDataProvider(dataProvider);
				Notification.show(String.format("Succesfully Created %s plans",count), 
						4000, Notification.Position.BOTTOM_CENTER);
			} else {
				Notification.show("ERROR: Some plans are not created",
						4000, Notification.Position.BOTTOM_CENTER);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}

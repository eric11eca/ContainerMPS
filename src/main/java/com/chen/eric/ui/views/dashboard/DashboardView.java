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
import org.apache.commons.lang3.StringUtils;

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
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.DebouncePhase;
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
    
    private Grid<ImportPlan> importPlanGrid;
    private ListDataProvider<ImportPlan> importDataProvider;
    
    private Grid<ExportPlan> exportPlanGrid;
    private ListDataProvider<ExportPlan> exportDataProvider;
    
    private File tempFile;
    private BOMInputStream in;
    
    private DataContainer dataContainer = DataContainer.getInstance();
    
    private Board board; 
    private Row editImportPlanRow;
    private Row editExportPlanRow;
    
    private WrapperCard importPlanGridWrapper;
    private WrapperCard importPlanDetailWrapper;
    
    private WrapperCard exportPlanGridWrapper;
    private WrapperCard exportPlanDetailWrapper;

	private String filter = "";

	//private Chart planProgress;
	//private  WrapperCard planProgressWrapper;

    public DashboardView() {    	
    	board = new Board();
    	vesselCount.setText(String.valueOf(dataContainer.countVessel()));
        board.addRow(
                createUploadBadge("Import List", "Upload Import Container List", "badge"),          
                createUploadBadge("Export List", "Upload Export Container List", "badge success"),  
                vesselCountBadge("Vessel Count", vesselCount, "error-text", "Number of vessels docked", "badge error")
        );
    
        /*drawPlanCountChart();
        planProgressWrapper = new WrapperCard("wrapper",
                new Component[] { planProgress }, "card");
        board.add(planProgressWrapper);*/
        
        createImportPlanGrid();
        updateImportPlanRow();
        createImportPlanRow();
        board.addComponentAtIndex(1, editImportPlanRow);
        
        createExportPlanGrid();
        updateExportPlanRow();
        createExportPlanRow();
        board.addComponentAtIndex(2, editExportPlanRow);

        FlexBoxLayout content = new FlexBoxLayout(board);
		content.setAlignItems(FlexComponent.Alignment.CENTER);
		content.setFlexDirection(FlexDirection.COLUMN);
		setViewContent(content);
    }
    
    private void createImportPlanRow() {
    	 importPlanGridWrapper = new WrapperCard("wrapper", 
         		new Component[] {createToolBar(true), importPlanGrid}, "card");
         editImportPlanRow = new Row();
         editImportPlanRow.addComponentAtIndex(0, importPlanGridWrapper);
         editImportPlanRow.addComponentAtIndex(1, importPlanDetailWrapper);
         board.addComponentAtIndex(1, editImportPlanRow);
    }
    
    private void createExportPlanRow() {
    	exportPlanGridWrapper = new WrapperCard("wrapper", 
        		new Component[] {createToolBar(false), exportPlanGrid}, "card");
        editExportPlanRow = new Row();
        editExportPlanRow.addComponentAtIndex(0, exportPlanGridWrapper);
        editExportPlanRow.addComponentAtIndex(1, exportPlanDetailWrapper);
        board.addComponentAtIndex(2, editExportPlanRow);
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
        
        Button importUpload = UIUtils.createTertiaryButton("Upload CSV List Here");
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
    
    /*private void drawPlanCountChart() {
    	planProgress = new Chart();
    	Configuration configuration = planProgress.getConfiguration();

        configuration.setTitle("Plan Progress Overview");
        
        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle("Number of Completed Plans");

        Legend legend = configuration.getLegend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setVerticalAlign(VerticalAlign.MIDDLE);
        legend.setAlign(HorizontalAlign.RIGHT);

        XAxis x = configuration.getxAxis();
        x.setTitle("Hours");
		x.setCategories("8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");
        
        configuration.addSeries(new ListSeries("Planned Number of Completed Import/Export Plan", 0, 20, 40, 60, 80, 100, 120, 140, 160));
        configuration.addSeries(new ListSeries("Actual Number of Completed ImportPlan", 0, 30, 50, 60, 70, 90));
        configuration.addSeries(new ListSeries("Actual Number of Completed ExportPlan", 0, 10, 20, 40, 70, 100));
    }*/
    
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
    
    private void showImportPlanDetail(TransPlan plan) {
		currentImportPlan = getImportPlan(plan);
		editImportPlanRow.remove(importPlanDetailWrapper);
		updateImportPlanRow();
    	editImportPlanRow.addComponentAtIndex(1, importPlanDetailWrapper);
    }
    
    private void showExportPlanDetail(TransPlan plan) {
    	currentExportPlan = getExportPlan(plan);
    	editExportPlanRow.remove(exportPlanDetailWrapper);
		updateExportPlanRow();
    	editExportPlanRow.addComponentAtIndex(1, exportPlanDetailWrapper);
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HorizontalLayout createToolBar(boolean isImport) {
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
        searchFilter.setItems("None", "PlanID", "ContainerID", "Manager");
        searchFilter.setLabel("Search Filter");
        searchFilter.addValueChangeListener(e -> filter = e.getValue());

        searchBar.getElement().addEventListener("value-changed", event -> {
            closeIcon.setVisible(!searchBar.getValue().isEmpty());  
            if (isImport) {
            	importDataProvider.clearFilters();
            } else {
            	exportDataProvider.clearFilters();
            }
            
            /*if (filter.isEmpty() || searchBar.getValue().isEmpty()) {
            	dataContainer.getPlanRecords();
            } else {
            	dataContainer.getPlanRecordsByParams(filter, searchBar.getValue());
            }
            
	        dataProvider = DataProvider.ofCollection(dataContainer.transPlanRecords.values());
	        planGrid.setDataProvider(dataProvider);*/
            if (filter.equals("PlanID")) {
            	if (isImport) {
            		importDataProvider.addFilter(
                            plan -> StringUtils.containsIgnoreCase(String.valueOf(plan.planID),
                                    searchBar.getValue()));
                } else {
                	exportDataProvider.addFilter(
                            plan -> StringUtils.containsIgnoreCase(String.valueOf(plan.planID),
                                    searchBar.getValue()));
                }
            } else if (filter.equals("ContainerID")) {
            	if (isImport) {
            		importDataProvider.addFilter(
                            plan -> StringUtils.containsIgnoreCase(String.valueOf(plan.getContainerID()),
                                    searchBar.getValue()));
                } else {
                	exportDataProvider.addFilter(
                            plan -> StringUtils.containsIgnoreCase(String.valueOf(plan.getContainerID()),
                                    searchBar.getValue()));
                }
            } else if (filter.equals("Manager"))  {
            	if (isImport) {
            		importDataProvider.addFilter(
                            plan -> StringUtils.containsIgnoreCase(plan.manager,
                                    searchBar.getValue()));
            	} else {
            		exportDataProvider.addFilter(
                            plan -> StringUtils.containsIgnoreCase(plan.manager,
                                    searchBar.getValue()));
            	}
            }
            
        }).debounce(300, DebouncePhase.TRAILING);
        
        HorizontalLayout toolBar = new HorizontalLayout(searchFilter, searchBar);
        toolBar.setAlignItems(Alignment.BASELINE);
        toolBar.setSpacing(true);
        toolBar.setPadding(true);
        
        return toolBar;
	}
    
    private void createImportPlanGrid() {
    	importPlanGrid = new Grid<>();
    	
    	importPlanGrid.addSelectionListener(e -> {
    		e.getFirstSelectedItem().ifPresent(this::showImportPlanDetail);
    	});
    	
    	dataContainer.getPlanRecords();	
    	importDataProvider = DataProvider.ofCollection(
    			dataContainer.importPlanRecords.values());
    	importPlanGrid.setDataProvider(importDataProvider);
    	importPlanGrid.setWidthFull();
		
    	importPlanGrid.addComponentColumn(this::createPlanCard)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true);
    	importPlanGrid.addComponentColumn(p-> createRemoveButton(p, true))
				.setFlexGrow(0).setWidth("130px")
				.setResizable(true)
				.setTextAlign(ColumnTextAlign.CENTER);
	}
    
    private void createExportPlanGrid() {
    	exportPlanGrid = new Grid<>();
    	
    	exportPlanGrid.addSelectionListener(e -> {
    		e.getFirstSelectedItem().ifPresent(this::showExportPlanDetail);
    	});
    	
    	dataContainer.getPlanRecords();	
    	exportDataProvider = DataProvider.ofCollection(
    			dataContainer.exportPlanRecords.values());
    	exportPlanGrid.setDataProvider(exportDataProvider);
    	exportPlanGrid.setWidthFull();
		
    	exportPlanGrid.addComponentColumn(this::createPlanCard)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true);
    	exportPlanGrid.addComponentColumn(p->createRemoveButton(p, false))
				.setFlexGrow(0).setWidth("130px")
				.setResizable(true)
				.setTextAlign(ColumnTextAlign.CENTER);
	}

	private Button createRemoveButton(TransPlan plan, boolean isImport) {
		Button button = new Button(new Icon(VaadinIcon.TRASH));
		button.addClickListener(e-> {
            int code = dataContainer.deletePlanRecords(plan.getPlanID());
            if (code == 0) {
            	if (isImport) {
            		importDataProvider.getItems().remove(plan);
            		importPlanGrid.setDataProvider(importDataProvider);
            		board.remove(editImportPlanRow);
            		updateImportPlanRow();
            	    createImportPlanRow();
            	} else {
            		exportDataProvider.getItems().remove(plan);
            		exportPlanGrid.setDataProvider(exportDataProvider);
            		board.remove(editExportPlanRow);
            		updateExportPlanRow();
        	        createExportPlanRow();
            	}
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
	
	private void updateImportPlanRow() {
		if (currentImportPlan == null) {
			importPlanDetailWrapper = initNoPlan();
		} else {
			try {
				HorizontalLayout planDetail = new HorizontalLayout(
						createPlanEditor(true), createGrpah(true));
				importPlanDetailWrapper = new WrapperCard("wrapper",
				        new Component[] {new H3("Import Plan"),  planDetail}, "card");
			} catch (IOException e) {
				e.printStackTrace();
				importPlanDetailWrapper = initNoPlan();
			}
		}
	}
	
	private void updateExportPlanRow() {
		if (currentExportPlan == null) {
			exportPlanDetailWrapper = initNoPlan();
		} else {
			try {
				HorizontalLayout planDetail = new HorizontalLayout(
						createPlanEditor(false), createGrpah(false));
				exportPlanDetailWrapper = new WrapperCard("wrapper",
				        new Component[] {new H3("Export Plan"),  planDetail}, "card");
			} catch (IOException e) {
				e.printStackTrace();
				exportPlanDetailWrapper = initNoPlan();
			}
		}
	}
    
    private VerticalLayout createImportTasks() {
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
        	if (e.getValue()) {
        		Notification.show("Unload Container Completed",
    					4000, Notification.Position.BOTTOM_CENTER);
        	}
        });
        
        Checkbox checkComplet = new Checkbox();
        checkComplet.setLabel("Custom Check");
        if (currentImportPlan.isCustomPassed()) {
        	checkComplet.setValue(true);
        }
        checkComplet.addValueChangeListener(e -> {
        	if (unloadComplet.getValue()) {
	        	currentImportPlan.setCustomPassed(e.getValue());
		       	dataContainer.updateCustomPassed(
		       			e.getValue(), currentImportPlan.planID, 
		       			currentImportPlan.getContainerID()); 
		       	if (e.getValue()) {
	        		Notification.show("Cuatomer Check Completed",
	    					1000, Notification.Position.BOTTOM_CENTER);
	        	}
        	} else {
        		if (e.getValue()) {
	        		Notification.show("Cannot do customer check becuase\n "
	        				+ "preivious checkpoint are incomplete",
	    					1000, Notification.Position.BOTTOM_CENTER);
	        		checkComplet.setValue(false);
        		}
        	}
        });
        
        Checkbox distirbuteComplet = new Checkbox();
        distirbuteComplet.setLabel("Distribute Container");
        if (currentImportPlan.isContainerDistributed()) {
        	distirbuteComplet.setValue(true);
        }
        distirbuteComplet.addValueChangeListener(e -> {
        	if (checkComplet.getValue()) {
	        	currentImportPlan.setContainerDistributed(e.getValue());
	    		dataContainer.updateContainerDistributed(
	    			e.getValue(), currentImportPlan.planID, 
	    			currentImportPlan.getContainerID());
	    		if (e.getValue()) {
	        		Notification.show("Distribute Container Completed",
	    					1000, Notification.Position.BOTTOM_CENTER);
	        	}
        	} else {
        		if (e.getValue()) {
	        		Notification.show("Cannot distribute container because\n "
	        				+ "preivious checkpoints are incomplete",
	    					1000, Notification.Position.BOTTOM_CENTER);
	        		distirbuteComplet.setValue(false);
        		}
        	}
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
					1000, Notification.Position.BOTTOM_CENTER);
    	   if (e.getValue()) {
      			Notification.show("Container Retrived",
      					1000, Notification.Position.BOTTOM_CENTER);
	       }
      });
       
       Checkbox billingComplet = new Checkbox();
       billingComplet.setLabel("Service Billing");
       if (currentExportPlan.isServicePayed()) {
    	   billingComplet.setValue(true);
       }
       billingComplet.addValueChangeListener(e -> {
    	   if (retriveComplet.getValue()) {
	    	   currentExportPlan.setServicePayed(true);
	    	   dataContainer.updateServicePayed(
	    			   e.getValue(), currentExportPlan.planID, 
	    			   currentExportPlan.getContainerID());
	    	   if (e.getValue()) {
	       			Notification.show("Container Service Billed",
	       					1000, Notification.Position.BOTTOM_CENTER);
		       }
    	   } else {
    		   if (e.getValue()) {
		       		Notification.show("Cannot bill container because\n "
		       					+ "preivious checkpoint is incomplete",
		       					1000, Notification.Position.BOTTOM_CENTER);
		       		billingComplet.setValue(false);
    		   }
    	   }
		});
       
       Checkbox loadComplet = new Checkbox();
       loadComplet.setLabel("Load Container");
       if (currentExportPlan.isContainerRetrived()) {
    	   loadComplet.setValue(true);
       }
       loadComplet.addValueChangeListener(e -> {
    	   if (billingComplet.getValue()) {
	    	   currentExportPlan.setLoadComplete(true);
	    	   dataContainer.updateLoadCompleted(
	    			   e.getValue(), currentExportPlan.planID, 
	    			   currentExportPlan.getContainerID());
	    	   if (e.getValue()) {
	       			Notification.show("Container Loading Completed",
	       					1000, Notification.Position.BOTTOM_CENTER);
		       }
    	   } else {
    		   if (e.getValue()) {
		       		Notification.show("Cannot load container because\n "
	       						+ "preivious checkpoint is incomplete",
	       						1000, Notification.Position.BOTTOM_CENTER);
		       		loadComplet.setValue(false);
    		   }
    	   }
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
    	   if (isImport) {
	    	   boolean validCompletion =
	    			   (currentImportPlan.isContainerDistributed()) &&
	    			   (currentImportPlan.isCustomPassed()) &&
	    			   (currentImportPlan.isUnLoadCompleted());
	    	   if (validCompletion) {
	    		   int code = dataContainer.uodatePlanStatus(manager.getValue(), currentImportPlan.planID, "Complete");
	    		   if (code == 0) {
	    			   dataContainer.deletePlanRecords(currentImportPlan.planID);
	    			   importDataProvider.getItems().remove(currentImportPlan);
	    			   Notification.show("This plan is completed",
		   						2000, Notification.Position.BOTTOM_CENTER);
	    			   importPlanGrid.setDataProvider(importDataProvider);
	    			   importPlanDetailWrapper = initNoPlan();
	    		   } else {
	    			   Notification.show("Cannot complete this plan becuase you are not its manager",
	   						2000, Notification.Position.BOTTOM_CENTER);
	    		   }
	    	   } else {
	    		   Notification.show("Cannot complete this plan becuase not all the checkpoints are complete",
	  						2000, Notification.Position.BOTTOM_CENTER);
	    	   }
    	   } else {
    		   boolean validCompletion =
	    			   (currentExportPlan.isContainerRetrived()) &&
	    			   (currentExportPlan.isServicePayed()) &&
	    			   (currentExportPlan.isLoadComplete());
	    	   if (validCompletion) {
	    		   int code = dataContainer.uodatePlanStatus(manager.getValue(), currentExportPlan.planID, "Complete");
	    		   if (code == 0) {
	    			   dataContainer.deletePlanRecords(currentExportPlan.planID);
	    			   exportDataProvider.getItems().remove(currentExportPlan);
	    			   Notification.show("This plan is completed",
		   						2000, Notification.Position.BOTTOM_CENTER);
	    			   exportPlanGrid.setDataProvider(exportDataProvider);
	    			   exportPlanDetailWrapper = initNoPlan();
	    		   } else {
	    			   Notification.show("Cannot complete this plan becuase you are not its manager",
	   						2000, Notification.Position.BOTTOM_CENTER);
	    		   }
	    	   } else {
	    		   Notification.show("Cannot complete this plan becuase not all the checkpoints are complete",
	  						2000, Notification.Position.BOTTOM_CENTER);
	    	   }
    	   }
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
	   		exportDiagram = exportDiagram.replace("$vesselID", String.valueOf(currentExportPlan.getLoadTo()));
	       	exportDiagram = exportDiagram.replace("$containerID", String.valueOf(currentExportPlan.getContainerID()));
	       	exportDiagram = exportDiagram.replace("$planID", String.valueOf(currentExportPlan.planID));
	       	exportDiagram = exportDiagram.replace("$manager", currentExportPlan.manager);
	       	exportDiagram = exportDiagram.replace("$status", currentExportPlan.status);
	       	exportDiagram = exportDiagram.replace("$date", currentExportPlan.date.toString());
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
		
		if (isImport) {
			image.setWidth("280px");
			image.setHeight("400px");
		} else {
			image.setWidth("280px");
			image.setHeight("350px");
		}
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
		dataContainer.getLocationRecords();
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
					if (dataContainer.locationRecords.containsKey(String.valueOf(containerID))) {
						double totalCost = dataContainer.calculateCost(containerID);
						TransPlan exportPlan = new ExportPlan(
								planID, manager, planDate, status, type, 
								containerID, vesselID, totalCost, false, false, false);
						code = dataContainer.insertPlanRecords(exportPlan);
					}
				}
				count += (code==0)? 1 : 0;
			}
			
			if (code == 0) {
				dataContainer.getPlanRecords();
				importDataProvider = DataProvider.ofCollection(
						dataContainer.importPlanRecords.values());
				importPlanGrid.setDataProvider(importDataProvider);
				exportDataProvider = DataProvider.ofCollection(
						dataContainer.exportPlanRecords.values());
				exportPlanGrid.setDataProvider(exportDataProvider);
				Notification.show(String.format("Succesfully Created %s plans",count), 
						2000, Notification.Position.BOTTOM_CENTER);
			} else {
				Notification.show("Some plans are not created." 
						+ String.format("Succesfully Created %s plans",count),
						2000, Notification.Position.BOTTOM_CENTER);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}

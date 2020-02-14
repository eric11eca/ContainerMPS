package com.chen.eric.ui.views.dashboard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.chen.eric.backend.ExportPlan;
import com.chen.eric.backend.ImportPlan;
import com.chen.eric.backend.TransPlan;
import com.chen.eric.backend.service.DataContainer;
import com.chen.eric.ui.MainLayout;
import com.chen.eric.ui.components.FlexBoxLayout;
import com.chen.eric.ui.util.UIUtils;
import com.chen.eric.ui.util.css.FlexDirection;
import com.chen.eric.ui.views.ViewFrame;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
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
public class DashboardView extends ViewFrame implements AfterNavigationObserver {

    private Grid<HealthGridItem> grid = new Grid<>();

    private Chart monthlyVisitors = new Chart();
    private Chart responseTimes = new Chart();
    private final H2 usersH2 = new H2();
    private final H2 eventsH2 = new H2();
    private final H2 conversionH2 = new H2();
    
    private ImportPlan currentImportPlan;
    private ExportPlan currentExportPlan;
    
    private Grid<TransPlan> planGrid;
    private ListDataProvider<TransPlan> dataProvider;
    
    private DataContainer dataContainer = DataContainer.getInstance();
    
    private Board board; 
    private Row editPlanRow;

    public DashboardView() {    	
    	board = new Board();
        board.addRow(
                createBadge("Users", usersH2, "primary-text", "Current users in the app", "badge"),
                createBadge("Events", eventsH2, "success-text", "Events from the views", "badge success"),
                createBadge("Conversion", conversionH2, "error-text","User conversion rate", "badge error")
        );

        monthlyVisitors.getConfiguration()
                .setTitle("Monthly visitors per city");
        monthlyVisitors.getConfiguration().getChart().setType(ChartType.COLUMN);
        WrapperCard monthlyVisitorsWrapper = new WrapperCard("wrapper",
                new Component[] { monthlyVisitors }, "card");
        board.add(monthlyVisitorsWrapper);

        grid.addColumn(HealthGridItem::getCity).setHeader("City");
        grid.addColumn(new ComponentRenderer<>(item -> {
            Span span = new Span(item.getStatus());
            span.getElement().getThemeList().add(item.getTheme());
            return span;
        })).setFlexGrow(0).setWidth("100px").setHeader("Status");
        grid.addColumn(HealthGridItem::getItemDate).setHeader("Date")
                .setWidth("140px");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        
        board.addRow(createPlanGrid());

        /*WrapperCard gridWrapper = new WrapperCard("wrapper",
                new Component[] { new H3("Service health"), grid }, "card");
       
        responseTimes.getConfiguration().setTitle("Response times");
        WrapperCard responseTimesWrapper = new WrapperCard("wrapper",
                new Component[] { responseTimes }, "card");*/
        
        updatePlanRow(true);
        board.add(editPlanRow);
        
        FlexBoxLayout content = new FlexBoxLayout(board);
		content.setAlignItems(FlexComponent.Alignment.CENTER);
		content.setFlexDirection(FlexDirection.COLUMN);
		setViewContent(content);
    }
    
    private String drawExportPlan() {
    	String exportDiagram = "@startuml\n" + 
    			"\n" + 
    			"package Retrive($retrived) {\n" + 
    			"    [Locate Container\\n <<$containerLocation>>] as location\n" + 
    			"    [Retrive Container\\n <<$containerID>>] as container\n" + 
    			"}\n" + 
    			"\n" + 
    			"package Payment($payed) {\n" + 
    			"    [Bill Container\\n <<$containerID>>\\n <<$fee>>] as bill\n" + 
    			"}\n" + 
    			"\n" + 
    			"package Load($loaded) {\n" + 
    			"    [Load To Vessel\\n <<$vesselID>>] as load\n" + 
    			"}\n" + 
    			"\n" + 
    			"\n" + 
    			"package ExportPlan {\n" + 
    			"    [Plan ID\\n <<$planID>>] as id\n" + 
    			"    [Plan Manager\\n <<$manager>>] as manager\n" + 
    			"    [Plan Status\\n <<$status>>] as status\n" + 
    			"    [Plan Date\\n <<$date>>] as date\n" + 
    			"}\n" + 
    			"\n" + 
    			"location -> container\n" + 
    			"container --> bill\n" + 
    			"bill --> load\n" + 
    			"\n" + 
    			"id-down-date\n" + 
    			"date-down-manager\n" + 
    			"manager-down-status\n" + 
    			"\n" + 
    			"@enduml";
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
    	String importDiagram = "@startuml\n" + 
    			"\n" + 
    			"package Unload($unloaded) {\n" + 
    			"    [Select Vessel\\n <<$vesselID>>] as vessel\n" + 
    			"    [Create Container\\n <<$containerID>>] as container\n" + 
    			"}\n" + 
    			"\n" + 
    			"package Custom($checked) {\n" + 
    			"    [Check Container\\n <<$containerID>>] as check\n" + 
    			"}\n" + 
    			"\n" + 
    			"package Distribute($distributed) {\n" + 
    			"    [Assign Loactaion\\n <<$containerID>>] as location\n" + 
    			"}\n" + 
    			"\n" + 
    			"node Storage($stored) {\n" + 
    			"    node Container <<$containerID>> as storage\n" + 
    			"}\n" + 
    			"\n" + 
    			"package Plan {\n" + 
    			"    [Plan ID\\n <<$planID>>] as id\n" + 
    			"    [Plan Manager\\n <<$manager>>] as manager\n" + 
    			"    [Plan Status\\n <<$status>>] as status\n" + 
    			"    [Plan Date\\n <<$date>>] as date\n" + 
    			"}\n" + 
    			"\n" + 
    			"vessel -> container\n" + 
    			"container --> check\n" + 
    			"check --> location\n" + 
    			"location --> storage\n" + 
    			"\n" + 
    			"id-down-date\n" + 
    			"date-down-manager\n" + 
    			"manager-down-status\n" + 
    			"\n" + 
    			"@enduml";
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
    
    private HorizontalLayout createImportTasks() {
    	 Checkbox unloadComplet = new Checkbox();
         unloadComplet.setLabel("Unload Container");
         unloadComplet.addValueChangeListener(e -> 
         	dataContainer.updateUnLoadCompleted(
         			e.getValue(), currentImportPlan.planID, 
         			currentImportPlan.getContainerID()));
         
         Checkbox checkComplet = new Checkbox();
         checkComplet.setLabel("Custom Check");
         checkComplet.addValueChangeListener(e ->
        	dataContainer.updateCustomPassed(
        			e.getValue(), currentImportPlan.planID, 
        			currentImportPlan.getContainerID()));
         
         Checkbox distirbuteComplet = new Checkbox();
         distirbuteComplet.setLabel("Distribute Container");
         distirbuteComplet.addValueChangeListener(e ->
     		dataContainer.updateContainerDistributed(
     			e.getValue(), currentImportPlan.planID, 
     			currentImportPlan.getContainerID()));
         
         HorizontalLayout checkboxes = new HorizontalLayout(
        		 new H6("Impor Tasks"), unloadComplet, checkComplet, distirbuteComplet);
         checkboxes.setSpacing(true);
         checkboxes.setAlignItems(Alignment.BASELINE);
         return checkboxes;
    }
    
    private HorizontalLayout createExportTasks() {
   	 	Checkbox retriveComplet = new Checkbox();
        retriveComplet.setLabel("Retrive Container");
        retriveComplet.addValueChangeListener(e -> 
     		dataContainer.updateContainerRetrived(
     			e.getValue(), currentExportPlan.planID, 
     			currentExportPlan.getContainerID()));
        
        Checkbox billingComplet = new Checkbox();
        billingComplet.setLabel("Service Billing");
        billingComplet.addValueChangeListener(e -> 
 			dataContainer.updateServicePayed(
 					e.getValue(), currentExportPlan.planID, 
 					currentExportPlan.getContainerID()));
        
        Checkbox loadComplet = new Checkbox();
        loadComplet.setLabel("Load Container");
        loadComplet.addValueChangeListener(e -> 
 			dataContainer.updateLoadCompleted(
 					e.getValue(), currentExportPlan.planID, 
 					currentExportPlan.getContainerID()));
        
        HorizontalLayout checkboxes = new HorizontalLayout(
        		new H6("Export Tasks"), retriveComplet, billingComplet, loadComplet);
        checkboxes.setSpacing(true);
        checkboxes.setAlignItems(Alignment.BASELINE);
        return checkboxes;
   }
    
    private VerticalLayout createPlanEditor(boolean isImport) {
        VerticalLayout planEditor = new VerticalLayout();
        TextField manager = new TextField();
        if (isImport) {
        	manager.setPlaceholder(currentImportPlan.manager);
        } else {
        	manager.setPlaceholder(currentExportPlan.manager);
        }
        manager.setLabel("Plan Manager");

        planEditor.add(manager);
        
        if (isImport) {
        	planEditor.add(createImportTasks());
        } else {
        	planEditor.add(createExportTasks());
        }
        
        return planEditor;
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
		image.setWidth("400px");
		image.setHeight("400px");

		os.close();
		return image;
	}
	
	private void initNoPlan() {
		WrapperCard noPlan = new WrapperCard("wrapper",
		        new Component[] {new H4("No Plan Selected")}, "card");
		WrapperCard noPlan1 = new WrapperCard("wrapper",
		        new Component[] {new H4("No Plan Selected")}, "card");
		editPlanRow.add(noPlan, noPlan1);
	}
	
	private void updatePlanRow(boolean isImport) {
		editPlanRow = new Row();
		
		if (isImport) {
			if (currentImportPlan == null) {
				initNoPlan();
			} else {
				WrapperCard importPlanEditor = new WrapperCard("wrapper",
				        new Component[] {new H4("Import Plan"), createPlanEditor(true) }, "card");
				try {
					WrapperCard planTree = new WrapperCard("wrapper",
					        new Component[] {new H4("Import Plan Flow"), createGrpah(true) }, "card");
					editPlanRow.add(importPlanEditor, planTree);
				} catch (IOException e) {
					e.printStackTrace();
					editPlanRow.add(importPlanEditor);
				}
			}
		} else {
			if (currentExportPlan == null) {
				initNoPlan();
			} else {
				WrapperCard exportPlanEditor = new WrapperCard("wrapper",
				        new Component[] {new H4("Export Plan"), createPlanEditor(false) }, "card");
				try {
					WrapperCard planTree = new WrapperCard("wrapper",
					        new Component[] {new H4("Export Plan Flow"), createGrpah(false) }, "card");
					editPlanRow.add(exportPlanEditor, planTree);
				} catch (IOException e) {
					e.printStackTrace();
					editPlanRow.add(exportPlanEditor);
				}
			}
		}
	}

    private WrapperCard createBadge(String title, H2 h2, String h2ClassName,
            String description, String badgeTheme) {
        Span titleSpan = new Span(title);
        titleSpan.getElement().setAttribute("theme", badgeTheme);

        h2.addClassName(h2ClassName);

        Span descriptionSpan = new Span(description);
        descriptionSpan.addClassName("secondary-text");

        return new WrapperCard("wrapper",
                new Component[] { titleSpan, h2, descriptionSpan }, "card", "space-m");
    }
    
    private Grid<TransPlan> createPlanGrid() {
    	planGrid = new Grid<>();
    	
    	planGrid.addSelectionListener(e -> {
    		TransPlan plan = e.getFirstSelectedItem().get();
    		if (plan.type.equals("Import")) {
    			currentImportPlan = getImportPlan(plan);
    			updatePlanRow(true);
    		} else if (plan.type.equals("Export")) {
    			currentExportPlan = getExportPlan(plan);
    			updatePlanRow(false);
    		}
    		board.removeRow(editPlanRow);
    		board.addRow(editPlanRow);
    	});
    	
    	dataProvider = DataProvider.ofCollection(
    			dataContainer.transPlanRecords.values());
    	planGrid.setDataProvider(dataProvider);
    	planGrid.setHeightByRows(true);
    	planGrid.setWidthFull();
		
    	planGrid.addColumn(TransPlan::getPlanID)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Plan ID");
    	planGrid.addColumn(TransPlan::getManager)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Plan Manager");
    	planGrid.addColumn(plan -> UIUtils.formatSqlDate(plan.getDate()))
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Plan Date");
    	planGrid.addColumn(TransPlan::getStatus)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Plan Status");
    	planGrid.addColumn(TransPlan::getType)
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortable(true)
				.setHeader("Plan Type");
    	planGrid.addComponentColumn(this::createRemoveButton)
				.setFlexGrow(0).setWidth("130px")
				.setResizable(true)
				.setTextAlign(ColumnTextAlign.CENTER);
		return planGrid;
	}

	private Button createRemoveButton(TransPlan plan) {
		Button button = new Button(new Icon(VaadinIcon.TRASH), clickEvent -> {
            int code = dataContainer.deleteVesselRecords(plan.getPlanID());
            if (code == 0) {
            	dataContainer.getVesselRecords();
        		dataProvider = DataProvider.ofCollection(dataContainer.transPlanRecords.values());
        		planGrid.setDataProvider(dataProvider);
        		Notification.show("Succefully Deleted the vessel!", 4000, Notification.Position.BOTTOM_CENTER);
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

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        usersH2.setText("745");
        eventsH2.setText("54.6k");
        conversionH2.setText("18%");

        Configuration configuration = monthlyVisitors.getConfiguration();
        configuration.addSeries(new ListSeries("Tokyo", 49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4,
                194.1, 95.6, 54.4));
        configuration.addSeries(
                new ListSeries("New York", 83.6, 78.8, 98.5, 93.4, 106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3));
        configuration.addSeries(
                new ListSeries("London", 48.9, 38.8, 39.3, 41.4, 47.0, 48.3, 59.0, 59.6, 52.4, 65.2, 59.3, 51.2));
        configuration.addSeries(
                new ListSeries("Berlin", 42.4, 33.2, 34.5, 39.7, 52.6, 75.5, 57.4, 60.4, 47.6, 39.1, 46.8, 51.1));

        XAxis x = new XAxis();
        x.setCrosshair(new Crosshair());
        x.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        configuration.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        configuration.addyAxis(y);

        // Grid
        List<HealthGridItem> gridItems = new ArrayList<>();
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "M\u00FCnster", "Germany", "Good", "badge"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Cluj-Napoca", "Romania", "Good", "badge"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Ciudad Victoria", "Mexico", "Good", "badge"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Ebetsu", "Japan", "Excellent", "badge success"));
        gridItems
                .add(new HealthGridItem(LocalDate.of(2019, 1, 14), "S\u00E3o Bernardo do Campo", "Brazil", "Good", "badge"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Maputo", "Mozambique", "Good", "badge"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Warsaw", "Poland", "Good", "badge"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Kasugai", "Japan", "Failing", "badge error"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Lancaster", "United States", "Excellent",
                "badge success"));

        grid.setItems(gridItems);
        
        ListSeries Tokyo = new ListSeries("Tokyo", 7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6);
        // Second chart
        configuration = responseTimes.getConfiguration();
        configuration.addSeries(Tokyo);
        configuration
                .addSeries(new ListSeries("London", 3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8));

        x = new XAxis();
        x.setCrosshair(new Crosshair());
        x.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        configuration.addxAxis(x);

        y = new YAxis();
        y.setMin(0);
        configuration.addyAxis(y);
    }
}
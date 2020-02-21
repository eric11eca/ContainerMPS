package com.chen.eric.ui.views;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.chen.eric.backend.Container;
import com.chen.eric.backend.Customer;
import com.chen.eric.backend.Employee;
import com.chen.eric.backend.ExportPlan;
import com.chen.eric.backend.ImportPlan;
import com.chen.eric.backend.Location;
import com.chen.eric.backend.StorageArea;
import com.chen.eric.backend.TransPlan;
import com.chen.eric.backend.Vessel;
import com.chen.eric.ui.components.ValidTextField;
import com.chen.eric.ui.util.UIUtils;
import com.helger.commons.csv.CSVReader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = LoginView.ROUTE)
@PageTitle("Login")
public class LoginView extends HorizontalLayout {
        public static final String ROUTE = "login";

        @Autowired
        private JdbcTemplate jdbcTemplate;
        
        private LoginOverlay login = new LoginOverlay(); 
        private String userName = "";
        private String password = "";
        private String SSN = "";
		private String name = "";

		private int code;
		private File tempFile;

        public LoginView(){
        	
        	//String encoded = new BCryptPasswordEncoder().encode("Hzfy83677@");
        	//System.out.println(encoded);
        	
            login.setAction("login");
            login.setOpened(true); 
            login.setTitle("ContainerMPS");
            login.setDescription("Login with valid username and password");
            login.setOpened(false);
            getElement().appendChild(login.getElement());
            
            Button log = UIUtils.createPrimaryButton("Log In");	
    		log.setWidthFull();
    		log.addClickListener(e->{
    			login.setOpened(true);
    		});
    		
    		Button register = UIUtils.createPrimaryButton("Sign Up");	
    		register.setWidthFull();
    		register.addClickListener(e->{
    			Dialog signupDialog = signUPDialog();
    			signupDialog.open();
    		});
    			
    		Component uploader = uploadContainers();
    		
    		H1 mainTitle = new H1();
    		mainTitle.add(new Text("Container Terminal\n Management and Planning System"));
    		
    		Image cover = new Image();
    		cover.setSrc("images/container.jpg");
    		cover.setMaxWidth("640px");
    		cover.setMaxHeight("480px");
    		VerticalLayout imageContainer = new VerticalLayout(cover);
    		imageContainer.setAlignItems(Alignment.CENTER);
    		
    		HorizontalLayout buttonBar = new HorizontalLayout(log, register, uploader);
    		buttonBar.setAlignItems(Alignment.CENTER);
    		buttonBar.setMargin(true);
    		buttonBar.setSpacing(true);
    		buttonBar.setMaxWidth("512px");
    		
    		VerticalLayout rightContent = new VerticalLayout();
    		rightContent.add(mainTitle, buttonBar);
    		rightContent.setAlignItems(Alignment.START);
    		
    		rightContent.setPadding(false);
    		rightContent.setMargin(true);
    		rightContent.setSpacing(true);
    	
    		setSpacing(true);
    		add(imageContainer,rightContent);
    		setAlignItems(Alignment.CENTER);
        }
        
        private Dialog signUPDialog() {
        	Dialog signupDialog = new Dialog();
        	signupDialog.setWidth("640px");
        	signupDialog.setHeight("480px");
        	
        	ValidTextField user = new ValidTextField();
        	user.addValidator(new StringLengthValidator("Cannot exceed 20 characters", 0, 20));
    		user.setLabel("User Name");
    		user.addValueChangeListener(e->{
    			userName = e.getValue();
    		});
    		
    		PasswordField passwordInput = new PasswordField();
    		passwordInput.setLabel("Password");
    		passwordInput.setMaxLength(80);
    		passwordInput.setErrorMessage("Cannot exceed 80 characters");
    		passwordInput.addValueChangeListener(e->{
    			password = e.getValue();
    		});
    		
    		ValidTextField ssnInput = new ValidTextField();
    		ssnInput.setLabel("SSN");
    		ssnInput.addValidator(new StringLengthValidator("Invalid SSN format", 9, 9));
    		ssnInput.addValueChangeListener(e-> {
    			SSN = e.getValue();
    		});
    		
    		ValidTextField nameInput = new ValidTextField();
    		nameInput.setLabel("Name");
    		nameInput.addValidator(new StringLengthValidator("Cannot exceed 50 characters", 0, 50));
    		nameInput.addValueChangeListener(e->{
    			name = e.getValue();
    		});
    		
    		Button save = UIUtils.createPrimaryButton("Save");
    		save.addClickListener(e->{
    			final String INSERT_SQL = "{? = call dbo.Insert_User(?,?,?,?)}";
    			final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    			Boolean paramsValid = !userName.isEmpty() && !password.isEmpty() && 
    					!SSN.isEmpty() && !name.isEmpty();
    			if (!paramsValid) {
    				Notification.show(
						"Please Fill Out All the Empty Feild!", 2000, Notification.Position.BOTTOM_CENTER);
    				return;
    			}

				try {
					Connection connection = jdbcTemplate.getDataSource().getConnection();
					CallableStatement cs = connection.prepareCall(INSERT_SQL);
					cs.registerOutParameter(1, Types.INTEGER);
					cs.setString(2, userName);
					cs.setString(3, passwordEncoder.encode(password));
					cs.setInt(4, Integer.valueOf(SSN));
					cs.setString(5, name);
					cs.execute();
					
					int returnCode = cs.getInt(1);
					if (returnCode != 0) {
						Notification.show(
							"Registration Failed", 2000, Notification.Position.BOTTOM_CENTER);
					} else {
						Notification.show(
							"Registration Sucessful", 2000, Notification.Position.BOTTOM_CENTER);
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
					Notification.show(
						"Registration Failed", 2000, Notification.Position.BOTTOM_CENTER);
				}
    			
    			signupDialog.close();
    		});
    		
    		Button cancle = UIUtils.createTertiaryButton("Cancel");
    		cancle.addClickListener(e->{
    			signupDialog.close();
    		});
    		
    		HorizontalLayout footer = new HorizontalLayout(save, cancle);
    		footer.setMargin(true);
    		footer.setAlignItems(Alignment.BASELINE);
    		
    		H2 signupTitle = new H2();
    		signupTitle.add(new Text("Employee Registration"));
    		Text signupSubTitle = new Text("Please Enter username, password, name, and SSN");
    		
    		HorizontalLayout layer1 = new HorizontalLayout(user, passwordInput);
    		HorizontalLayout layer2 = new HorizontalLayout(ssnInput, nameInput);
    		layer1.setPadding(false);
    		layer1.setMargin(true);
    		layer1.setSpacing(true);
    		layer1.setAlignItems(Alignment.CENTER);
    		layer2.setPadding(false);
    		layer2.setMargin(true);
    		layer2.setSpacing(true);
    		layer2.setAlignItems(Alignment.CENTER);
    		
    		VerticalLayout signup = new VerticalLayout();
    		signup.setAlignItems(Alignment.CENTER);
    		signup.setPadding(true);
    		signup.setMargin(true);
    		signup.setSpacing(true);
    		signup.add(signupTitle);
    		signup.add(signupSubTitle);
    		signup.add(layer1);
    		signup.add(layer2);
    		signup.add(footer);
    		signup.getStyle().set("border", "2px solid #9E9E9E");
    		signup.setMaxWidth("600px");
    		
    		signupDialog.add(signup);
    		return signupDialog;
    	}
        
        public Component uploadContainers() {
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
    		          fetchDataFromCSV(in);
    		          tempFile.delete();
    		          in.close();
    		        } catch (IOException e1) {
    		          e1.printStackTrace();
    		        }
    		    });
    		    upload.setWidthFull();
    		upload.setHeight("50%");
    		return upload;
    	}

    	private void fetchDataFromCSV(DataInputStream in) {
    		CSVReader csvReader;
    		try {
    			csvReader = new CSVReader(new InputStreamReader(in,"utf-8"));
    	    	List<String> record;
    	    	int i = 1;
    			while ((record = csvReader.readNext()) != null) {
    				if (i < 8) {
    					Customer customer = new Customer();
    					customer.setCustomerID(Integer.parseInt(record.get(0)));
    					customer.setCompanyName(record.get(1));
    					customer.setContactEmail(record.get(2));
    					customer.setCountry(record.get(3));
    					customer.setState(record.get(4));
    					code = insertCustomer(customer);
    					System.out.println("Customer: " + code);
    				} else if (i > 7 && i < 13) {
    					Employee emp = new Employee();
    					emp.setUserName(record.get(0));
    					emp.setPassword(record.get(1));
    					emp.setSSN(Integer.parseInt(record.get(2)));
    					emp.setRole(record.get(3));
    					emp.setName(record.get(4));
    					code = insertUser(emp);
    					System.out.println("User: " + code);
    				} else if (i > 12 && i < 112) {
    					Container con = new Container();
    					con.setContainerID(Integer.valueOf(record.get(0)));
    					con.setType(record.get(1));
    					con.setLength(Integer.valueOf(record.get(2)));
    					con.setWidth(Integer.valueOf(record.get(3)));
    					con.setHeight(Integer.valueOf(record.get(4)));
    					con.setWeight(Integer.valueOf(record.get(5)));
    					con.setOwner(record.get(6));
    					con.setPayed(Boolean.parseBoolean(record.get(7)));
    					con.setFee(Integer.valueOf(record.get(8)));
    					code = insertContainer(con);
    					System.out.println("Container: " + code);
    				} else if (i > 111 && i < 118) {
    					StorageArea sa = new StorageArea();
    					sa.setStorageID(Integer.valueOf(record.get(0)));
    					sa.setType(record.get(1));
    					sa.setCapacity(Integer.valueOf(record.get(2)));
    					sa.setStoragePrice(Double.valueOf(record.get(3)));
    					code = insertStorageArea(sa);
    					System.out.println("StorageArea: " + code);
    				} else if (i > 117 && i < 128) {
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
    					code = insertVessel(v);
    					System.out.println("Vessel: " + code);
    				} else if (i > 127 && i < 219) {
    					Location loc = new Location();
    					loc.setContainerID(Integer.valueOf(record.get(0)));
    					loc.setStorageID(Integer.valueOf(record.get(1)));
    					loc.setBlockIndex(Integer.valueOf(record.get(2)));
    					loc.setRowIndex(Integer.valueOf(record.get(3)));
    					loc.setTierIndex(Integer.valueOf(record.get(4)));
    					loc.setBayIndex(Integer.valueOf(record.get(5)));
    					loc.setStartDate(Date.valueOf(record.get(6)));
    					loc.setEndDate(Date.valueOf(record.get(7)));
    					code = insertLocation(loc);
    					System.out.println("Location: " + code);
    				} else if (i > 218 && i < 234) {
    					if (record.get(3).equals("Import")) {
    						ImportPlan imp = new ImportPlan();
    						imp.planID = Integer.valueOf(record.get(0));
    						imp.date = Date.valueOf(record.get(1));
    						imp.manager = record.get(2);
    						imp.type = record.get(3);
    						imp.status = record.get(4);
    						imp.setContainerID(Integer.valueOf(record.get(5)));
    						imp.setUnloadFrom(Integer.valueOf(record.get(6)));
    						code = insertPlans(imp);
    					} else {
    						ExportPlan exp = new ExportPlan();
    						exp.planID = Integer.valueOf(record.get(0));
    						exp.date = Date.valueOf(record.get(1));
    						exp.manager = record.get(2);
    						exp.type = record.get(3);
    						exp.status = record.get(4);
    						exp.setContainerID(Integer.valueOf(record.get(5)));
    						exp.setTotalCost(Double.valueOf(record.get(6)));
    						exp.setLoadTo(Integer.valueOf(record.get(7)));
    						code = insertPlans(exp);
    					}
    					System.out.println("Plan: " + code);
    				}
    				i += 1;	
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}	
    	}
    	
    	private int insertPlans(TransPlan plan) {
    		try {
    			Connection connection = jdbcTemplate.getDataSource().getConnection();
    			
    			if (plan.type.equals("Import")) {
    				String queryImport = "{? = CALL dbo.Insert_ImportPlan(?,?,?,?,?,?)}";
    				ImportPlan importPlan = (ImportPlan) plan;
    				CallableStatement stmtImport = connection.prepareCall(queryImport);
    				stmtImport.registerOutParameter(1, Types.INTEGER);
    				stmtImport.setInt(2, importPlan.getPlanID());
    				stmtImport.setString(3, importPlan.getManager());
    				stmtImport.setString(4, importPlan.getType());
    				stmtImport.setString(5, importPlan.getStatus());
    				stmtImport.setInt(6, importPlan.getContainerID());
    				stmtImport.setInt(7, importPlan.getUnloadFrom());
    				stmtImport.execute();				
    				System.out.println(stmtImport.getInt(1));
    				return stmtImport.getInt(1);
    			} else if (plan.type.equals("Export")) {
    				String queryExport = "{? = CALL dbo.Insert_ExportPlan(?,?,?,?,?,?,?)}";
    				ExportPlan exportPlan = (ExportPlan) plan;
    				CallableStatement stmtExport = connection.prepareCall(queryExport);
    				stmtExport.registerOutParameter(1, Types.INTEGER);
    				stmtExport.setInt(2, exportPlan.getPlanID());
    				stmtExport.setString(3, exportPlan.getManager());
    				stmtExport.setString(4, exportPlan.getType());
    				stmtExport.setString(5, exportPlan.getStatus());
    				stmtExport.setInt(6, exportPlan.getContainerID());
    				stmtExport.setInt(7, exportPlan.getLoadTo());
    				stmtExport.setDouble(8, exportPlan.getTotalCost());
    				stmtExport.execute();
    				int code = stmtExport.getInt(1);
    				connection.close();
    				return code;
    			}
    		}
    		catch (SQLException ex) {
    			ex.printStackTrace();
    			return -1;
    		}
    		return 0;
    	}
    	
    	private int insertCustomer(Customer t) {
    		try {
    			Connection connection = jdbcTemplate.getDataSource().getConnection();
    			String query = "{? = CALL dbo.Insert_Customer(?,?,?,?,?)}";
    			
    			CallableStatement stmt =  connection.prepareCall(query);
    			stmt.registerOutParameter(1, Types.INTEGER);
    			stmt.setInt(2, t.getCustomerID());
    			stmt.setString(3, t.getCompanyName());
    			stmt.setString(4, t.getContactEmail());
    			stmt.setString(5, t.getCountry());
    			stmt.setString(6, t.getState());
    			stmt.execute();
    			int code = stmt.getInt(1);
    			connection.close();
    			return code;
    		}
    		catch (SQLException ex) {
    			ex.printStackTrace();
    			return -1;
    		}
    	}
    	
    	private int insertUser(Employee emp) {
    		final String INSERT_SQL = "{? = call dbo.Insert_NewUser(?,?,?,?,?)}";
    		final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    		try {
    			Connection connection = jdbcTemplate.getDataSource().getConnection();
    			CallableStatement cs = connection.prepareCall(INSERT_SQL);
    			cs.registerOutParameter(1, Types.INTEGER);
    			cs.setString(2, emp.getUserName());
    			cs.setString(4, emp.getRole());
    			cs.setString(3, passwordEncoder.encode(emp.getPassword()));
    			cs.setInt(5, Integer.valueOf(emp.getSSN()));
    			cs.setString(6, emp.getName());
    			cs.execute();
    			
    			int returnCode = cs.getInt(1);
    			connection.close();
    			return returnCode;
    		} catch (SQLException e1) {
    			e1.printStackTrace();
    			return -1;
    		}
    	}
    	
    	private int insertContainer(Container t) {
    		try {
    			Connection connection = jdbcTemplate.getDataSource().getConnection();
    			String query = "{? = CALL dbo.Insert_Container(?,?,?,?,?,?,?,?,?)}";
    			
    			CallableStatement stmt =  connection.prepareCall(query);
    			stmt.registerOutParameter(1, Types.INTEGER);
    			stmt.setInt(2, t.getContainerID());
    			stmt.setString(3, t.getType());
    			stmt.setDouble(4, t.getLength());
    			stmt.setDouble(5, t.getWidth());
    			stmt.setDouble(6, t.getHeight());
    			stmt.setDouble(7, t.getWeight());
    			stmt.setString(8, t.getOwner());
    			stmt.setBoolean(9, t.isPayed());
    			stmt.setDouble(10, t.getFee());
    			stmt.execute();
    			int code = stmt.getInt(1);
    			connection.close();
    			return code;
    		}
    		catch (SQLException ex) {
    			ex.printStackTrace();
    			return -1;
    		}
    	}
    	
    	private int insertStorageArea(StorageArea t) {
    		try {
    			Connection connection = jdbcTemplate.getDataSource().getConnection();
    			String query = "{? = CALL dbo.Insert_StorageArea(?,?,?,?)}";
    			
    			CallableStatement stmt =  connection.prepareCall(query);
    			stmt.registerOutParameter(1, Types.INTEGER);
    			stmt.setInt(2, t.getStorageID());
    			stmt.setString(3, t.getType());
    			stmt.setInt(4, t.getCapacity());
    			stmt.setDouble(5, t.getStoragePrice());
    			stmt.execute();
    			int code = stmt.getInt(1);
    			connection.close();
    			return code;
    		}
    		catch (SQLException ex) {
    			ex.printStackTrace();
    			return -1;
    		}
    	}
    	
    	private int insertVessel(Vessel vessel) {
    		try {
    			Connection connection = jdbcTemplate.getDataSource().getConnection();
    			String query = "{? = call Insert_Vessel(?,?,?,?,?,?,?,?,?,?)}";
    			
    			CallableStatement stmt =  connection.prepareCall(query);	
    			stmt.registerOutParameter(1, Types.INTEGER);
    			stmt.setInt(2, vessel.getVesselID());
    			stmt.setInt(3, vessel.getCapacity());
    			stmt.setDate(4, vessel.getDepartDate());
    			stmt.setDate(5, vessel.getArivalDate());
    			stmt.setString(6, vessel.getDestinationCountry());
    			stmt.setString(7, vessel.getDestinationState());
    			stmt.setString(8, vessel.getDestinationCity());
    			stmt.setString(9, vessel.getDepartedFromCountry());
    			stmt.setString(10, vessel.getDepartedFromState());
    			stmt.setString(11, vessel.getDepartedFromCity());
    			stmt.execute();
    			int code = stmt.getInt(1);
    			connection.close();
    			return code;
    		}
    		catch (SQLException ex) {
    			ex.printStackTrace();
    			return -1;
    		}
    	}
    	
    	public int insertLocation(Location t) {
    		try {
    			Connection connection = jdbcTemplate.getDataSource().getConnection();
    			String query = "{? = CALL dbo.Insert_StoredAt(?,?,?,?,?,?,?,?)}";
    			
    			CallableStatement stmt =  connection.prepareCall(query);
    			stmt.registerOutParameter(1, Types.INTEGER);
    			stmt.setInt(2, t.getContainerID());
    			stmt.setInt(3, t.getStorageID());
    			stmt.setInt(4, t.getBlockIndex());
    			stmt.setInt(5, t.getRowIndex());
    			stmt.setInt(6, t.getTierIndex());
    			stmt.setInt(7, t.getBayIndex());
    			stmt.setDate(8, t.getStartDate());
    			stmt.setDate(9, t.getEndDate());
    			stmt.execute();
    			int code = stmt.getInt(1);
    			connection.close();
    			return code;
    		}
    		catch (SQLException ex) {
    			ex.printStackTrace();
    			return -1;
    		}
    	}  
}

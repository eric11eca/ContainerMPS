package com.chen.eric.backend.service;

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
import com.helger.commons.csv.CSVReader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;

public class DumpData {
	private File tempFile;
	private int code = 0;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public  DumpData ( ) {}
	
	public Component uploadContainers() {
		@SuppressWarnings("serial")
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
					emp.setSSN(Integer.parseInt(record.get(0)));
					emp.setRole(record.get(1));
					emp.setName(record.get(2));
					emp.setUserName(record.get(3));
					code = insertUser(emp);
					System.out.println("User: " + code);
				} else if (i > 12 && i < 113) {
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
				} else if (i > 112 && i < 119) {
					StorageArea sa = new StorageArea();
					sa.setStorageID(Integer.valueOf(record.get(0)));
					sa.setType(record.get(1));
					sa.setCapacity(Integer.valueOf(record.get(2)));
					sa.setStoragePrice(Double.valueOf(record.get(3)));
					code = insertStorageArea(sa);
					System.out.println("StorageArea: " + code);
				} else if (i > 118 && i < 129) {
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
				} else if (i > 128 && i < 220) {
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
				} else if (i > 219 && i < 235) {
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
			return stmt.getInt(1);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	private int insertUser(Employee emp) {
		final String INSERT_SQL = "{? = call dbo.Insert_NewUser(?,?,?,?)}";
		final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		try {
			Connection connection = jdbcTemplate.getDataSource().getConnection();
			CallableStatement cs = connection.prepareCall(INSERT_SQL);
			cs.registerOutParameter(1, Types.INTEGER);
			cs.setString(2, emp.getUserName());
			cs.setString(3, passwordEncoder.encode(emp.getPassword()));
			cs.setInt(4, Integer.valueOf(emp.getSSN()));
			cs.setString(5, emp.getName());
			cs.execute();
			
			int returnCode = cs.getInt(1);
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
			
			return stmt.getInt(1);
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
			
			return stmt.getInt(1);
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
			return stmt.getInt(1);
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
			
			return stmt.getInt(1);
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

}

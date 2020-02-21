package com.chen.eric.backend.service;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.chen.eric.backend.ExportPlan;
import com.chen.eric.backend.ImportPlan;
import com.chen.eric.backend.TransPlan;

public class PlanService implements EntityService<TransPlan> {
	private DBConnectionService dbService;
	private Map<String, TransPlan> transPlans;
	private Map<String, ImportPlan> importPlans;
	private Map<String, ExportPlan> exportPlans;
	
	public  PlanService (DBConnectionService dbService) {
		this.dbService = dbService;
	}

	@Override
	public Map<String, TransPlan> retriveRecords() {
		transPlans = new HashMap<>();
		importPlans = new HashMap<>();
		exportPlans = new HashMap<>();
		
		try {
			dbService.connect();
			String importQuery = "{call dbo.View_ImportPlan()}";
			String exportQuery = "{call dbo.View_ExportPlan()}";
			
			CallableStatement stmtImport =  dbService.getConnection().prepareCall(importQuery);		
			CallableStatement stmtExport =  dbService.getConnection().prepareCall(exportQuery);						
			
			boolean hasRsImport = stmtImport.execute();
			boolean hasRsExport = stmtExport.execute();
			
			
	        if (hasRsImport) {
	           try (ResultSet rs = stmtImport.getResultSet()) {     	
	        	    parseResults(rs);
	           }
	        }	
	        
	        if (hasRsExport) {
	        	try (ResultSet rs = stmtExport.getResultSet()) {     	
	        		parseResults(rs);
	        	}
	        }
	        return transPlans;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return transPlans;
		}		
	}

	public Map<String, ImportPlan> getImportPlans() {
		return importPlans;
	}

	public Map<String, ExportPlan> getExportPlans() {
		return exportPlans;
	}

	@Override
	public Map<String, TransPlan> parseResults(ResultSet rs) {
		try {
			int PlanID = rs.findColumn("PlanID");
			int type = rs.findColumn("Type");
			int Date = rs.findColumn("Date");
			int Status = rs.findColumn("Status");
			int Manager = rs.findColumn("Manager");
			while (rs.next()) {
				String planType = rs.getString(type);
				if (planType.contains("Import")) {
					ImportPlan importPlan = new ImportPlan(
							rs.getInt(PlanID), rs.getString(Manager),
							rs.getDate(Date), rs.getString(Status),
							planType, rs.getInt("ContainerID"), 
							rs.getInt("UnLoadFrom"),
							rs.getBoolean("UnLoadCompleted"),
							rs.getBoolean("CustomPassed"),
							rs.getBoolean("ContainerDistributed"));
					transPlans.put(rs.getString(PlanID), importPlan);
					importPlans.put(rs.getString(PlanID), importPlan);
				} else if (planType.contains("Export")) {
					ExportPlan exportPlan = new ExportPlan(
							rs.getInt(PlanID), rs.getString(Manager),
							rs.getDate(Date), rs.getString(Status), planType,
							rs.getInt("ContainerID"), rs.getInt("LoadTo"),
							rs.getDouble("TotalCost"),
							rs.getBoolean("ContainerRetrived"),
							rs.getBoolean("ServicePayed"),
							rs.getBoolean("LoadComplete"));
					
					transPlans.put(rs.getString(PlanID), exportPlan);
					exportPlans.put(rs.getString(PlanID), exportPlan);
				}
				
			}
			return new HashMap<>();
		} catch (SQLException ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

	@Override
	public int insertRecords(TransPlan plan) {
		try {
			dbService.connect();
			
			if (plan.type.equals("Import")) {
				String queryImport = "{? = CALL dbo.Insert_ImportPlan(?,?,?,?,?,?)}";
				ImportPlan importPlan = (ImportPlan) plan;
				CallableStatement stmtImport = dbService.getConnection().prepareCall(queryImport);
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
				CallableStatement stmtExport = dbService.getConnection().prepareCall(queryExport);
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

	@Override
	public Map<String, TransPlan> retriveRecordsByParameters(String filter, String value) {
		transPlans = new HashMap<>();
		exportPlans = new HashMap<>();
		importPlans = new HashMap<>();
		return new HashMap<>();
	}
	
	
	public void retriveImportPlanRecords(String filter, String value){	
		try {
			this.dbService.connect();
			String query = "{call Search_ImportPlan(?,?,?,?,?)}";
			CallableStatement stmt = this.dbService.getConnection().prepareCall(query);
			if (filter.equals("PlanID")) {
				stmt.setString(1, value);
			} else {
				stmt.setString(1, null);
			}
			
			if (filter.equals("ContainerID")) {
				stmt.setString(2, value);
			} else {
				stmt.setString(2, null);
			}
			
			if (filter.equals("UnloadFrom")) {
				stmt.setString(3, value);
			} else {
				stmt.setString(3, null);
			}
			
			if (filter.equals("Type")) {
				stmt.setString(4, value);
			} else {
				stmt.setString(4, null);
			}
			
			if (filter.equals("Manager")) {
				stmt.setString(5, value);
			} else {
				stmt.setString(5, null);
			}
			
			boolean hasRs = stmt.execute();
			ResultSet res = stmt.getResultSet();
			if (hasRs) {
				parseResults(res);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public Map<String, TransPlan> retriveExportPlanRecords(String filter, String value){	
		if (filter.isEmpty()) {
			return new HashMap<>();
		}
			try {
				this.dbService.connect();
				String query = "{call Search_ExportPlan(?,?,?,?,?)}";
				CallableStatement stmt = this.dbService.getConnection().prepareCall(query);
				if (filter.equals("PlanID")) {
					stmt.setString(1, value);
				}else {
					stmt.setString(1, null);
				}
				
				if (filter.equals("ContainerID")) {
					stmt.setString(2, value);
				}else {
					stmt.setString(2, null);
				}
				
				
				if (filter.equals("LoadTo")) {
					stmt.setString(3, value);
				}else {
					stmt.setString(3, null);
				}
				
				if (filter.equals("Type")) {
					stmt.setString(4, value);
				}else {
					stmt.setString(4, null);
				}
				
				if (filter.equals("Manager")) {
					stmt.setString(5, value);
				}else {
					stmt.setString(5, null);
				}
				
				boolean hasRs = stmt.execute();
				ResultSet res = stmt.getResultSet();
				if (hasRs) {
					parseResults(res);
					return transPlans;
				}
				return new HashMap<>();
			}catch(SQLException e) {
				e.printStackTrace();
				return new HashMap<>();
			}
	}
	
	@Override
	public int updateRecords(TransPlan plan, int key) {
		return 0;
	}
	
	public int updateStatus(String status, int key) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_TransPlan_Status(?,?)}";
			CallableStatement stmt = dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, key);
			stmt.setString(3, status);
			stmt.execute();
			int returnCode = stmt.getInt(1);
			return returnCode;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	public int updateImportVessel(Integer unloadFrom, int primary, int secondary) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_ImportPlan_Vessel(?,?,?)}";
			CallableStatement stmt = dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, primary);
			stmt.setInt(3, secondary);
			stmt.setInt(4, unloadFrom);
			stmt.execute();
			int returnCode = stmt.getInt(1);
			return returnCode;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	public int updateImportCustom(boolean customPassed, int primary, int secondary) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_ImportPlan_CustomPassed(?,?,?)}";
			CallableStatement stmt = dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, primary);
			stmt.setInt(3, secondary);
			stmt.setBoolean(4, customPassed);
			stmt.execute();
			int returnCode = stmt.getInt(1);
			return returnCode;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	public int updateImportUnload(boolean unloadComplet, int primary, int secondary) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_ImportPlan_UnloadComplete(?,?,?)}";
			CallableStatement stmt = dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, primary);
			stmt.setInt(3, secondary);
			stmt.setBoolean(4, unloadComplet);
			stmt.execute();
			int returnCode = stmt.getInt(1);
			return returnCode;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	public int updateImportDistribute(boolean containerDistributed, int primary, int secondary) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_ImportPlan_Distributed(?,?,?)}";
			CallableStatement stmt = dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, primary);
			stmt.setInt(3, secondary);
			stmt.setBoolean(4, containerDistributed);
			stmt.execute();
			int returnCode = stmt.getInt(1);
			return returnCode;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	public int updateExportVessel(Integer loadTo, int primary, int secondary) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_ExportPlan_Vessel(?,?,?)}";
			CallableStatement stmt = dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, primary);
			stmt.setInt(3, secondary);
			stmt.setInt(4, loadTo);
			stmt.execute();
			int returnCode = stmt.getInt(1);
			return returnCode;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	public int updateExportTotalCost(Double totalCost, int primary, int secondary) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_ExportPlan_TotalCost(?,?,?)}";
			CallableStatement stmt = dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, primary);
			stmt.setInt(3, secondary);
			stmt.setDouble(4, totalCost);
			stmt.execute();
			int returnCode = stmt.getInt(1);
			return returnCode;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	public int updateExportRetrived(boolean containerRetrived, int primary, int secondary) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_ExportPlan_ContainerRetrived(?,?,?)}";
			CallableStatement stmt = dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, primary);
			stmt.setInt(3, secondary);
			stmt.setBoolean(4, containerRetrived);
			stmt.execute();
			int returnCode = stmt.getInt(1);
			return returnCode;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	public int updateExportLoad(boolean loadCompleted, int primary, int secondary) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_ExportPlan_LoadComplete(?,?,?)}";
			CallableStatement stmt = dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, primary);
			stmt.setInt(3, secondary);
			stmt.setBoolean(4, loadCompleted);
			stmt.execute();
			int returnCode = stmt.getInt(1);
			return returnCode;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	public int updateExportBill(boolean servicePayed, int primary, int secondary) {
		try {
			dbService.connect();
			String query = "{? = CALL dbo.Update_ExportPlan_ServicePayed(?,?,?)}";
			CallableStatement stmt = dbService.getConnection().prepareCall(query);
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, primary);
			stmt.setInt(3, secondary);
			stmt.setBoolean(4, servicePayed);
			stmt.execute();
			int returnCode = stmt.getInt(1);
			return returnCode;
		}
		catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	public int updateStatus(String input, int PlanID,String Status) {
		try {
			dbService.connect();
			String query = "{? = call dbo.Check_Signature(?,?,?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);	
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setString(2, input);
			stmt.setInt(3, PlanID);
			stmt.setString(4, Status);
			stmt.execute();
			return stmt.getInt(1);
		} catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	@Override
	public int updateTwoKeyRecords(TransPlan plan, int primary, int secondary) {
		
		return 0;
	}

	@Override
	public int deleteRecords(int ID) {
		try {
			dbService.connect();
			String query = "{? = call dbo.Delete_Plan(?)}";
			
			CallableStatement stmt =  dbService.getConnection().prepareCall(query);	
			stmt.registerOutParameter(1, Types.INTEGER);
			stmt.setInt(2, ID);
			stmt.execute();
			return stmt.getInt(1);
		} catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

}

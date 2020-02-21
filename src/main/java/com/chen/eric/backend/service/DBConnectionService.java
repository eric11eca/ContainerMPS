package com.chen.eric.backend.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

public class DBConnectionService {
	  //private static final String DBURL = "jdbc:sqlserver://golem.csse.rose-hulman.edu;databaseName=ContainerTestDB;";
	  private SQLServerDataSource ds = new SQLServerDataSource();
	  
	  private Connection connection = null;
	  
	  private static DBConnectionService dbService; 
	  
	  public static DBConnectionService getInstance() { 
		  if (dbService == null) 
			  dbService = new DBConnectionService(); 
	      return dbService; 
	  } 
	  
	  private DBConnectionService() {
		  ds.setUser("gaoq");
	      ds.setPassword("668899aA");
	      ds.setServerName("golem.csse.rose-hulman.edu");
	      ds.setDatabaseName("ContainerTestDB");
	  }
	  
	  public boolean connect() {
		  try {
			  this.connection = ds.getConnection();
	      } catch (SQLException e) {
	          e.printStackTrace();
	          return false;
	      } 
		  return true;
	  }
	  
	  public Connection getConnection() {
		  return this.connection;
	  }
	  
      public void closeConnection() {
    	  try {
    		  this.connection.close();
    	  } catch (SQLException e) {
  			e.printStackTrace();
    	  }
      }

}

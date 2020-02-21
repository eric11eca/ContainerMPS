package com.chen.eric.backend.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

@Configuration
@PropertySource("classpath:application.properties")
public class DBConnectionService {
	  //private static final String DBURL = "jdbc:sqlserver://golem.csse.rose-hulman.edu;databaseName=ContainerTestDB;";
	  private SQLServerDataSource ds = new SQLServerDataSource();
	  private Connection connection = null;

	  @Value( "${spring.datasource.url}" )
	  private String jdbcUrl;
	  
	  public DBConnectionService() {
		       
	  }
	  
	  public void configurate(String url) {
		  ds.setUser("gaoq");
	      ds.setPassword("668899aA");
	      //ds.setServerName("golem.csse.rose-hulman.edu");
	      //ds.setDatabaseName("ContainerTestDB");	
	      ds.setURL(url);
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

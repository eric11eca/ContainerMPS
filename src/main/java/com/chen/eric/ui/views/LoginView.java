package com.chen.eric.ui.views;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.chen.eric.ui.components.ValidTextField;
import com.chen.eric.ui.util.UIUtils;
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
    		
    		H1 mainTitle = new H1();
    		mainTitle.add(new Text("Container Terminal\n Management and Planning System"));
    		
    		Image cover = new Image();
    		cover.setSrc("images/container.jpg");
    		cover.setMaxWidth("640px");
    		cover.setMaxHeight("480px");
    		VerticalLayout imageContainer = new VerticalLayout(cover);
    		imageContainer.setAlignItems(Alignment.CENTER);
    		
    		HorizontalLayout buttonBar = new HorizontalLayout(log, register);
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
    		user.addValidator(new StringLengthValidator("Invalid SSN format", 9, 9));
    		ssnInput.addValueChangeListener(e-> {
    			SSN = e.getValue();
    		});
    		
    		ValidTextField nameInput = new ValidTextField();
    		nameInput.setLabel("Name");
    		user.addValidator(new StringLengthValidator("Cannot exceed 50 characters", 0, 50));
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
}

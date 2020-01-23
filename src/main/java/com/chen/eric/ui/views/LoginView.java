package com.chen.eric.ui.views;

import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@SuppressWarnings("serial")
@Route(value = LoginView.ROUTE)
@PageTitle("Login")
public class LoginView extends VerticalLayout {
        public static final String ROUTE = "login";

        private LoginOverlay login = new LoginOverlay(); 

        public LoginView(){
        	
        	//String encoded = new BCryptPasswordEncoder().encode("Hzfy83677@");
        	//System.out.println(encoded);
        	
            login.setAction("login");
            login.setOpened(true); 
            login.setTitle("ContainerMPS");
            getElement().appendChild(login.getElement());
        }
}

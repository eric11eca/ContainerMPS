package com.chen.eric.Security;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.chen.eric.ui.views.LoginView;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final String LOGIN_PROCESSING_URL = "/" + LoginView.ROUTE;
	private static final String LOGIN_FAILURE_URL = "/" + LoginView.ROUTE;
	private static final String LOGIN_URL = "/" + LoginView.ROUTE;
	private static final String LOGOUT_SUCCESS_URL = "/" + LoginView.ROUTE;

	/**
	 * Require login to access internal pages and configure login form.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Not using Spring CSRF here to be able to use plain HTML for the login page
		http.csrf().disable()

		// Register our CustomRequestCache, that saves unauthorized access attempts, so
		// the user is redirected after login.
		.requestCache().requestCache(new CustomRequestCache())

		// Restrict access to our application.
		.and().authorizeRequests()

		// Allow all flow internal requests.
		.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

		// Allow all requests by logged in users.
		.anyRequest().authenticated()

		// Configure the login page.
		.and().formLogin().loginPage(LOGIN_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL)
		.failureUrl(LOGIN_FAILURE_URL)

		// Configure logout
		.and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
	}
	
	@Autowired
	DataSource dataSource;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    auth.jdbcAuthentication().dataSource(dataSource)
	        .usersByUsernameQuery("SELECT Username, Password, Enable"
	            + " FROM Login where Username=?")
	        .authoritiesByUsernameQuery(
	        		"SELECT Username, LTRIM(RTRIM(Role)) AS Role "
	        		+ "From Employee Join Login On Login.ID = Employee.LoginID "
	        		+ "WHERE Username=?")
	        .passwordEncoder(new BCryptPasswordEncoder());
	}
	

	/**
	 * Allows access to static resources, bypassing Spring security.
	 */
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers(
				// Vaadin Flow static resources
				"/VAADIN/**",

				// the standard favicon URI
				"/favicon.ico",

				// the robots exclusion standard
				"/robots.txt",

				// web application manifest
				"/manifest.webmanifest",
				"/sw.js",
				"/offline-page.html",

				// icons and images
				"/icons/**",
				"/images/**",

				// (development mode) static resources
				"/frontend/**",

				// (development mode) webjars
				"/webjars/**",

				// (development mode) H2 debugging console
				"/h2-console/**",

				// (production mode) static resources
				"/frontend-es5/**", "/frontend-es6/**");
	}
}
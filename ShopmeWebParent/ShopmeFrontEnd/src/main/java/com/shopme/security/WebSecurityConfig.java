package com.shopme.security;

import com.shopme.security.oauth.CustomerOAuth2UserService;
import com.shopme.security.oauth.DbLoginSuccessHandler;
import com.shopme.security.oauth.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{

	@Autowired
	private CustomerOAuth2UserService oAuth2UserService;

	@Autowired
	private OAuth2LoginSuccessHandler oAuth2LoginHandler;

	@Autowired
	private PasswordEncoderConfig passwordEncoderConfig;

	@Autowired
	private DbLoginSuccessHandler dbLoginHandler;

	public WebSecurityConfig(CustomerOAuth2UserService oAuth2UserService) {
		this.oAuth2UserService = oAuth2UserService;
	}


	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomerUserDetailsService();
	}


	@Bean
	SecurityFilterChain configHttp(HttpSecurity http) throws Exception {
		http.authenticationProvider(authenticationProvider());

		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/account_details", "/update_account_details", "/cart", "/orders/**",
						"/address_book/**", "/checkout", "/place_order", "/process_paypal_order").authenticated()
				.anyRequest().permitAll()
		)
				.formLogin(form -> form
						.loginPage("/login")
						.usernameParameter("email")
						.successHandler(dbLoginHandler)
						.permitAll()
				)
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/login")
						.userInfoEndpoint(u -> u.userService(oAuth2UserService))
						.successHandler(oAuth2LoginHandler)
				)
				.logout(logout -> logout.permitAll())
				.rememberMe(httpSecurityRememberMeConfigurer -> httpSecurityRememberMeConfigurer
						.key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ")
						.tokenValiditySeconds(14 * 24 * 60 * 60))
						.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
				;


		return http.build();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoderConfig.passwordEncoder());

		return authProvider;
	}

	@Bean
	WebSecurityCustomizer configureWebSecurity() throws Exception {
		return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
	}

}

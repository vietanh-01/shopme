package com.shopme.security.oauth;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
        HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        CustomerOAuth2User customerOAuth2User = (CustomerOAuth2User) authentication.getPrincipal();

        String email = customerOAuth2User.getEmail();
        String name = customerOAuth2User.getName();
        String countryCode = request.getLocale().getCountry();
        String clientName = customerOAuth2User.getClientName();

        System.out.println("oauth2User infro: " + customerOAuth2User.getName() + " | " + customerOAuth2User.getEmail());
        System.out.println("Client name: " + customerOAuth2User.getClientName());

        AuthenticationType type = getAuthenticationType(clientName);
        Customer customerByEmail = customerService.getCustomerByEmail(email);

        if(customerByEmail == null) {
            customerService.addNewCustomerUponOAuthLogin(name, email,countryCode, type);
        } else{
            customerOAuth2User.setFullName(customerByEmail.getFullName());
            customerService.updateAuthenticationType(customerByEmail, type);
        }


        super.onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationType getAuthenticationType(String clientName) {
        if(clientName.equalsIgnoreCase("Google"))
            return AuthenticationType.GOOGLE;
        else if (clientName.equalsIgnoreCase("Facebook"))
            return AuthenticationType.FACEBOOK;
        return AuthenticationType.DATABASE;
    }

}

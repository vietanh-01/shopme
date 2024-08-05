package com.shopme.checkout;

import com.shopme.checkout.paypal.PayPalOrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class PayPalApiTests {
    private static final String BASE_URL = "https://api.sandbox.paypal.com";
    private static final String GET_ORDER_API = "/v2/checkout/orders/";
    private static final String CLIENT_ID = "AV9lnwy1Ft89Gk1i9CUC37JE9wtGJrnNOW_q2M9sNXqKuZqqlPKq48uIz2r2N29Y3c9JZG9-3Y2PB5BY";
    private static final String CLIENT_SECRET = "EFzYkmOCA7ZmKGiL5MOBpZz3P7qqNCMlpmovEVcGPxipYRhpsImtaEP4WcqazCszEBnsHCTI0KP_bu2u";

    @Test
    public void testGetOrderDetails() {
        String orderId = "76M591379B914002V";
        String requestURL = BASE_URL + GET_ORDER_API + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PayPalOrderResponse> response = restTemplate.exchange(
                requestURL, HttpMethod.GET, request, PayPalOrderResponse.class);
        PayPalOrderResponse orderResponse = response.getBody();

//        System.out.println("Order ID: " + orderResponse.getId());
//        System.out.println("Validated: " + orderResponse.validate(orderId));
        System.out.println(response);

    }
}

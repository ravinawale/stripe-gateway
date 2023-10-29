package com.priwar.stripe.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.priwar.stripe.dto.CreateCustomerRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;

@RestController
public class CutomerController {

	@PostMapping("/create-customer")
	public ResponseEntity<Map<String, Object>> createCustomer(@RequestBody CreateCustomerRequest request) throws StripeException {

		Optional<Customer> customer;
		CustomerListParams params = CustomerListParams.builder().setEmail(request.getEmail()).build();
		CustomerCollection cust =  Customer.list(params);
		
		boolean isPresent = cust.getData().stream()
					  .anyMatch(custEle -> custEle.getEmail().equals(request.getEmail()));
					  
		if(isPresent) {
			customer=cust.getData().stream().findFirst();
		}else {
			CustomerCreateParams customerParams = CustomerCreateParams.builder()
							.setEmail(request.getEmail())
							.setName(request.getCname())
							.build();
			// Create a new customer object.
			customer =  Optional.of(Customer.create(customerParams));
		}
		
		Map<String, Object> responseData = new HashMap<>();
		responseData.put("customer", customer.get());
		responseData.put("customerId", customer.get().getId());
		
		/*Cookie cookie = new Cookie("customer", customer.getId());
		cookie.setMaxAge(Duration.ofMillis(3600));
		cookie.setPath("/");
		cookie.setHttpOnly(false);
		cookie.setSecure(true);
		reponse.addCookie(cookie);*/

		return ResponseEntity.ok(responseData);
	}
}

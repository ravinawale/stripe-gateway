package com.priwar.stripe.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.priwar.stripe.ApplicationConstant;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.param.PriceListParams;

@RestController
public class ConfigController {

	@GetMapping("/config")
	public ResponseEntity<Map<String, Object>> config() throws StripeException {
		
		Map<String, Object> responseData = new HashMap<>();

        responseData.put(
          "publishableKey",ApplicationConstant.STRIPE_PUBLISHABLE_KEY
        );

        PriceListParams params = PriceListParams
          .builder()
          .addLookupKey("sample_basic")
          .addLookupKey("sample_premium")
          .build();
        
        PriceCollection prices = Price.list(params);
        responseData.put("prices", prices.getData());
		
        return ResponseEntity.ok(responseData);
	}
}

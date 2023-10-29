package com.priwar.stripe;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@PropertySource({"classpath:stripe.properties"})
public class StripeApplication {
	
	@Value("${stripe.apikey}") String apiKey;
	@Value("${STRIPE_SECRET_KEY}")  String STRIPE_SECRET_KEY;
	@Value("${STRIPE_PUBLISHABLE_KEY}")  String STRIPE_PUBLISHABLE_KEY;
	@Value("${STRIPE_WEBHOOK_SECRET}")  String STRIPE_WEBHOOK_SECRET;
	@Value("${REACT_APP_STRIPE_PUBLISHABLE_KEY}")  String REACT_APP_STRIPE_PUBLISHABLE_KEY;
	@Value("${BASIC}")  String BASIC;
	@Value("${PREMIUM}")  String PREMIUM;
	
    @PostConstruct
    public void setApiKey() {
    	Stripe.apiKey=apiKey;
    	ApplicationConstant.STRIPE_SECRET_KEY=this.STRIPE_SECRET_KEY;
    	ApplicationConstant.STRIPE_PUBLISHABLE_KEY=this.STRIPE_PUBLISHABLE_KEY;
    	ApplicationConstant.STRIPE_WEBHOOK_SECRET=STRIPE_WEBHOOK_SECRET;
    	ApplicationConstant.REACT_APP_STRIPE_PUBLISHABLE_KEY=REACT_APP_STRIPE_PUBLISHABLE_KEY;
    	ApplicationConstant.BASIC=BASIC;
    	ApplicationConstant.PREMIUM=PREMIUM;
    }
	
	public static void main(String[] args) {
		SpringApplication.run(StripeApplication.class, args);
	}
}

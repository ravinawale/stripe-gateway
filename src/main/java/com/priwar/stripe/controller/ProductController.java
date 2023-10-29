package com.priwar.stripe.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.param.ProductCreateParams;

@RestController
public class ProductController {

	@PostMapping("/create-product")
	public String createProduct() throws StripeException {
		/*ProductCreateParams basic =
				  ProductCreateParams.builder()
				    .setName("Basic Product")
				    .setDefaultPriceData(
				      ProductCreateParams.DefaultPriceData.builder()
				        .setUnitAmount(100L)
				        .setCurrency("inr")
				        .setRecurring(
				          ProductCreateParams.DefaultPriceData.Recurring.builder()
				            .setInterval(ProductCreateParams.DefaultPriceData.Recurring.Interval.YEAR)
				            .build()
				        )
				        .build()
				    )
				    .addExpand("default_price")
				    .build();

		Product product = Product.create(basic);*/
		
		ProductCreateParams params =
				  ProductCreateParams.builder()
				    .setName("Premium Product")
				    .setDefaultPriceData(
				      ProductCreateParams.DefaultPriceData.builder()
				        .setUnitAmount(200L)
				        .setCurrency("inr")
				        .setRecurring(
				          ProductCreateParams.DefaultPriceData.Recurring.builder()
				            .setInterval(ProductCreateParams.DefaultPriceData.Recurring.Interval.YEAR)
				            .build()
				        )
				        .build()
				    )
				    .addExpand("default_price")
				    .build();
		
		Product product2 = Product.create(params);
		
		return "created";
	}
}

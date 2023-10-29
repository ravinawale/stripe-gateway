package com.priwar.stripe.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.priwar.stripe.dto.CancelSubscriptionRequest;
import com.priwar.stripe.dto.CreateSubscriptionRequest;
import com.priwar.stripe.dto.UpdateSubscriptionRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.param.InvoiceUpcomingParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.SubscriptionUpdateParams;

@RestController
public class SubscriptionController {

	@PostMapping("/create-subscription")
	public ResponseEntity<Map<String, Object>> cteateSubscription(@RequestHeader("customerId") String customerId,@RequestBody CreateSubscriptionRequest request)
			throws StripeException {

		System.out.println("Customer Id in SubscriptionController cteateSubscription >>>>>> "+customerId);
		
		// Create the subscription
		SubscriptionCreateParams subCreateParams = SubscriptionCreateParams.builder()
				.setCustomer(customerId) //set customer id
				.addItem(SubscriptionCreateParams.Item.builder().setPrice(request.getPriceId()).build())
				.setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
				.addAllExpand(Arrays.asList("latest_invoice.payment_intent")).build();

		Subscription subscription = Subscription.create(subCreateParams);

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("subscriptionId", subscription.getId());
		responseData.put("clientSecret",
				subscription.getLatestInvoiceObject().getPaymentIntentObject().getClientSecret());

		return ResponseEntity.ok(responseData);
	}
	
	@PostMapping("/cancel-subscription")
	public ResponseEntity<Map<String, Object>> cancelSubscription(@RequestBody CancelSubscriptionRequest  request)
			throws StripeException {
		
	        Subscription subscription = Subscription.retrieve(
	        		request.getSubscriptionId()
	        );

	        Subscription deletedSubscription = subscription.cancel();

	        Map<String, Object> responseData = new HashMap<>();
	        responseData.put("subscription", deletedSubscription);
		
		return ResponseEntity.ok(responseData);
	}
	
	
	@PostMapping("/update-subscription")
	public ResponseEntity<Map<String, Object>> updateSubscription(@RequestBody UpdateSubscriptionRequest request)
			throws StripeException {
		
        // Retrieve the subscription to access related subscription item ID
        // to update.
        Subscription subscription = Subscription.retrieve(
        		request.getSubscriptionId()
        );

        // Build params to update the Subscription.
        SubscriptionUpdateParams params = SubscriptionUpdateParams
          .builder()
          .addItem(
            SubscriptionUpdateParams
              .Item.builder()
              .setId(subscription.getItems().getData().get(0).getId())
              .setPrice(request.getNewPriceLookupKey().toLowerCase())
              .build()
          )
          .setCancelAtPeriodEnd(false)
          .build();

        // update the subscription.
        subscription.update(params);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("subscription", subscription);
        
        return ResponseEntity.ok(responseData);
	}
	
	@GetMapping("/subscriptions")
	public ResponseEntity<Map<String, Object>> getSubscriptions(@RequestHeader("customerId") String customerId) throws StripeException {
		
		System.out.println(">>>> customerId  in getSubscriptions >>> "+customerId);

        SubscriptionListParams params = SubscriptionListParams
            .builder()
            .setStatus(SubscriptionListParams.Status.ALL)
            .setCustomer(customerId)
            .addAllExpand(Arrays.asList("data.default_payment_method"))
            .build();

        SubscriptionCollection subscriptions = Subscription.list(params);
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("subscriptions", subscriptions);
        
        return ResponseEntity.ok(responseData); 
	}
	
	
	@GetMapping("/invoice-preview")
	public ResponseEntity<Map<String, Object>> invoicePreview(@RequestHeader("customerId") String customerId,
															  @RequestParam("newPriceLookupKey") String newPriceLookupKey,
															  @RequestParam("subscriptionId") String subscriptionId) throws StripeException {

		 String newPriceId = newPriceLookupKey;
	     Subscription subscription = Subscription.retrieve(subscriptionId);
		
	     System.out.println(">>>> customerId  in invoicePreview >>> "+customerId);
	     
	     // Build the params for retrieving the invoice preview.
	     InvoiceUpcomingParams invoiceParams = InvoiceUpcomingParams
	          .builder()
	          .setCustomer(customerId)
	          .setSubscription(subscriptionId)
	          .addSubscriptionItem(
	            InvoiceUpcomingParams
	              .SubscriptionItem.builder()
	              .setId(subscription.getItems().getData().get(0).getId())
	              .setPrice(newPriceId)
	              .build()
	          )
	          .build();

        // Fetch the invoice preview.
        Invoice invoice = Invoice.upcoming(invoiceParams);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("invoice", invoice);
	     
		return ResponseEntity.ok(responseData);
	}
	
}

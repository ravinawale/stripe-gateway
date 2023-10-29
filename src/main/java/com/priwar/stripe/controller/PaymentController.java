package com.priwar.stripe.controller;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonSyntaxException;
import com.priwar.stripe.dto.CreatePayment;
import com.priwar.stripe.dto.CreatePaymentResponse;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PriceListParams;
import com.stripe.param.checkout.SessionCreateParams;

@RestController
public class PaymentController {
	
    final String YOUR_DOMAIN = "http://localhost:9090";
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);
	String endpointSecret = "whsec_12345";
    
	@PostMapping("/create-payment-intent")
	public ResponseEntity<CreatePaymentResponse> createPayment(@RequestBody CreatePayment createPayment) throws StripeException{
	      PaymentIntentCreateParams params =
	        PaymentIntentCreateParams.builder()
	        	.setDescription("Testing Payment")
		        /*.setShipping(
		          PaymentIntentCreateParams.Shipping.builder()
		            .setName("Ravi")
		            .setPhone("918806320582")
		            .setAddress(
		              PaymentIntentCreateParams.Shipping.Address.builder()
		                .setLine1("Flat 404")
		                .setPostalCode("98140")
		                .setCity("Pune")
		                .setState("MHA")
		                .setCountry("IND")
		                .build()
		            )
		            .build()
		         )*/
	            .setAmount(100L)
	            .setCurrency("INR")
	            // In the latest version of the API, specifying the `automatic_payment_methods` 
	            // parameter is optional because Stripe enables its functionality by default.
	           .setAutomaticPaymentMethods(
	            PaymentIntentCreateParams.AutomaticPaymentMethods
	              .builder()
	              .setEnabled(true)
	              .build()
	          )
	          .build();

	       //Create a PaymentIntent with the order amount and currency
	       PaymentIntent paymentIntent = PaymentIntent.create(params);
	       //System.out.println("paymentIntent ==> "+paymentIntent);
		return ResponseEntity.ok(new CreatePaymentResponse(paymentIntent.getClientSecret()));  
	}
	
	@PostMapping("/create-checkout-session")
	public String createCheckoutSession(@RequestParam(name = "lookup_key") String lookupKey) throws StripeException {
		  
		  PriceListParams priceParams = PriceListParams.builder()
				  				.addLookupKey(lookupKey)
				  				.build();
		  
	      PriceCollection prices = Price.list(priceParams);

	      SessionCreateParams params = SessionCreateParams.builder()
	          .addLineItem(
	              SessionCreateParams.LineItem.builder()
	              				.setPrice(prices.getData().get(0).getId())
	              				.setQuantity(1L).build())
	          
	          
	          .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
	          .setSuccessUrl(YOUR_DOMAIN + "/success.html?session_id={CHECKOUT_SESSION_ID}")
	          .setCancelUrl(YOUR_DOMAIN + "/cancel.html")
	          .build();
	      
	      Session session = Session.create(params);
	      
	      return "redirect:/"+session.getUrl();
    }
	
	@PostMapping("/create-portal-session")
	public String createPortalSession(@RequestAttribute(name = "session_id") String session_id) throws StripeException {
		  // For demonstration purposes, we're using the Checkout session to retrieve the
	      // customer ID.
	      // Typically this is stored alongside the authenticated user in your database.
	      // Deserialize request from our front end.
	      Session checkoutSession = Session.retrieve(session_id);

	      String customer = checkoutSession.getCustomer();
	      
	      // Authenticate your user.
	      com.stripe.param.billingportal.SessionCreateParams params 
	      			= new com.stripe.param.billingportal.SessionCreateParams.Builder()
	    		  	.setReturnUrl(YOUR_DOMAIN)
	    		  	.setCustomer(customer).build();

	      com.stripe.model.billingportal.Session portalSession = com.stripe.model.billingportal.Session.create(params);


	      //.redirect(portalSession.getUrl(), 303);
	      return "redirect:/"+portalSession.getUrl();
	}
	
	/*@PostMapping("/webhook")
	public String webhook(@RequestHeader("Stripe-Signature") String sigHeader, @RequestBody String payload) throws StripeException {
		
	      Event event = null;
	      try {
	        event = ApiResource.GSON.fromJson(payload, Event.class);
	      } catch (JsonSyntaxException e) {
	        // Invalid payload
	    	LOGGER.info("Webhook error while parsing basic request.");
	        //response.status(400);
	        return "";
	      }
	      
	      if (endpointSecret != null && sigHeader != null) {
	        // Only verify the event if you have an endpoint secret defined.
	        // Otherwise use the basic event deserialized with GSON.
	        try {
	          event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
	        } catch (SignatureVerificationException e) {
	          // Invalid signature
	          LOGGER.info("Webhook error while parsing basic request.");
	          //response.status(400);
	          return "";
	        }
	      }
	      
	      // Deserialize the nested object inside the event
	      EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
	      StripeObject stripeObject = null;
	      if (dataObjectDeserializer.getObject().isPresent()) {
	        stripeObject = dataObjectDeserializer.getObject().get();
	      } else {
	        // Deserialization failed, probably due to an API version mismatch.
	        // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
	        // instructions on how to handle this case, or return an error here.
	      }
	      
	      // Handle the event
	      Subscription subscription = null;
	      switch (event.getType()) {
	        case "customer.subscription.deleted":
	          subscription = (Subscription) stripeObject;
	          // Then define and call a function to handle the event
	          // customer.subscription.deleted
	          // handleSubscriptionTrialEnding(subscription);
	          LOGGER.info("handleSubscriptionTrialEnding >>>>>>>>>>> ");
	        case "customer.subscription.trial_will_end":
	          subscription = (Subscription) stripeObject;
	          // Then define and call a function to handle the event
	          // customer.subscription.trial_will_end
	          // handleSubscriptionDeleted(subscriptionDeleted);
	          LOGGER.info("handleSubscriptionDeleted >>>>>>>>>>> ");
	        case "customer.subscription.created":
	          subscription = (Subscription) stripeObject;
	          // Then define and call a function to handle the event
	          // customer.subscription.created
	          // handleSubscriptionCreated(subscription);
	          LOGGER.info("handleSubscriptionCreated >>>>>>>>>>> ");
	        case "customer.subscription.updated":
	          subscription = (Subscription) stripeObject;
	          // Then define and call a function to handle the event
	          // customer.subscription.updated
	          // handleSubscriptionUpdated(subscription);
	          // ... handle other event types
	          LOGGER.info("handleSubscriptionUpdated >>>>>>>>>>> ");
	        default:
	          LOGGER.info("Unhandled event type: " + event.getType());
	      }
	      //response.status(200);
		return "";
	}*/
}

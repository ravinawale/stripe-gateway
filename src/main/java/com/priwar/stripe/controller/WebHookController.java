package com.priwar.stripe.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Invoice;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.net.Webhook;
import com.stripe.param.SubscriptionUpdateParams;

@RestController
public class WebHookController {

	@PostMapping("/webhook")
	public String webHook(@RequestHeader("Stripe-Signature") String sigHeader, @RequestBody String payload)
			throws StripeException {
		
		
        String endpointSecret = "";
        		//dotenv.get("STRIPE_WEBHOOK_SECRET");
        
        Event event = null;

        try {
          event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
          // Invalid signature
          //response.status(400);
          return "";
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
        
        switch (event.getType()) {
        case "invoice.payment_succeeded":
          Invoice invoice = (Invoice) stripeObject;
          if(invoice.getBillingReason().equals("subscription_create")) {
            // The subscription automatically activates after successful payment
            // Set the payment method used to pay the first invoice
            // as the default payment method for that subscription
            String subscriptionId = invoice.getSubscription();
            String paymentIntentId = invoice.getPaymentIntent();

            // Retrieve the payment intent used to pay the subscription
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            // Set the default payment method
            Subscription subscription = Subscription.retrieve(subscriptionId);
            SubscriptionUpdateParams params = SubscriptionUpdateParams
              .builder()
              .setDefaultPaymentMethod(paymentIntent.getPaymentMethod())
              .build();
            Subscription updatedSubscription = subscription.update(params);
            System.out.println("Default payment method set for subscription: " + paymentIntent.getPaymentMethod());
          }

          System.out.println("Payment succeeded for invoice: " + event.getId());

          break;
        case "invoice.paid":
          // Used to provision services after the trial has ended.
          // The status of the invoice will show up as paid. Store the status in your
          // database to reference when a user accesses your service to avoid hitting rate
          // limits.
          break;
        case "invoice.payment_failed":
          // If the payment fails or the customer does not have a valid payment method,
          // an invoice.payment_failed event is sent, the subscription becomes past_due.
          // Use this webhook to notify your user that their payment has
          // failed and to retrieve new card details.
          break;
        case "invoice.finalized":
          // If you want to manually send out invoices to your customers
          // or store them locally to reference to avoid hitting Stripe rate limits.
          break;
        case "customer.subscription.deleted":
          // handle subscription cancelled automatically based
          // upon your subscription settings. Or if the user
          // cancels it.
          break;
        default:
        // Unhandled event type
      }
      //response.status(200);
      return "";
		
	}
}

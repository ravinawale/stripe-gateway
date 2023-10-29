package com.priwar.stripe.dto;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class CancelSubscriptionRequest implements Serializable{

	 @SerializedName("subscriptionId")
	 String subscriptionId;

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	 
}

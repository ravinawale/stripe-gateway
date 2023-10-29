package com.priwar.stripe.dto;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class UpdateSubscriptionRequest implements Serializable{

	@SerializedName("subscriptionId")
    String subscriptionId;

    @SerializedName("newPriceLookupKey")
    String newPriceLookupKey;

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getNewPriceLookupKey() {
		return newPriceLookupKey;
	}

	public void setNewPriceLookupKey(String newPriceLookupKey) {
		this.newPriceLookupKey = newPriceLookupKey;
	}

    
}

package com.priwar.stripe.dto;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class CreateSubscriptionRequest implements Serializable{

	@SerializedName("priceId")
    String priceId;

	public String getPriceId() {
		return priceId;
	}

	public void setPriceId(String priceId) {
		this.priceId = priceId;
	}

    
}

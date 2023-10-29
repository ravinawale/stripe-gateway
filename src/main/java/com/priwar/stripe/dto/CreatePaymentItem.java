package com.priwar.stripe.dto;

import java.io.Serializable;

public class CreatePaymentItem implements Serializable{
	
	private static final long serialVersionUID = -2799730137003114051L;
	
	String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}

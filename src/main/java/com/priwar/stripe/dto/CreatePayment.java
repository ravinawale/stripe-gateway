package com.priwar.stripe.dto;

import java.io.Serializable;

public class CreatePayment implements Serializable{

	private static final long serialVersionUID = 8107201883478413163L;
	
	CreatePaymentItem[] items;

	public CreatePaymentItem[] getItems() {
		return items;
	}

	public void setItems(CreatePaymentItem[] items) {
		this.items = items;
	}
}

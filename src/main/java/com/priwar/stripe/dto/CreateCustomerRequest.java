package com.priwar.stripe.dto;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class CreateCustomerRequest implements Serializable{
	
	String email;
	String cname;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}
}

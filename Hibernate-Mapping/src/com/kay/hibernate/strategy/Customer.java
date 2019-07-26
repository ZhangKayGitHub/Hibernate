package com.kay.hibernate.strategy;

import java.util.HashSet;
import java.util.Set;

public class Customer {

	private Integer customerId;
	private String customerName;
	
	private Set<Order1> orders = new HashSet<>();
	
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public Set<Order1> getOrders() {
		return orders;
	}
	public void setOrders(Set<Order1> orders) {
		this.orders = orders;
	}
	
	
}

package com.kay.hibernate.entities.n21.both;

public class Order1 {

	private Integer order1Id;
	private String order1Name;
	
	private Customer customer;

	public Integer getOrder1Id() {
		return order1Id;
	}

	public void setOrder1Id(Integer order1Id) {
		this.order1Id = order1Id;
	}

	public String getOrder1Name() {
		return order1Name;
	}

	public void setOrder1Name(String order1Name) {
		this.order1Name = order1Name;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}

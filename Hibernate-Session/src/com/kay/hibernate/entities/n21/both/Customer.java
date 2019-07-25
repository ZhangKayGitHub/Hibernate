package com.kay.hibernate.entities.n21.both;

import java.util.HashSet;
import java.util.Set;

public class Customer {

	private Integer customerId;
	private String customerName;
	
	/*
	 * 两点需要注意的:
	 * 1.声明集合类型时，需使用接口类型，因为hibernate 在获取集合类型时，返回的是Hibernate内置的集合类型，
	 * 而不是JavaSE 一个标准的集合类型实现。
	 * 
	 * 2.需要将集合初始化，初始化可以的防止发生空指针异常
	 * */
	
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

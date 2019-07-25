package com.kay.hibernate.entities.n21.both;

import java.util.HashSet;
import java.util.Set;

public class Customer {

	private Integer customerId;
	private String customerName;
	
	/*
	 * ������Ҫע���:
	 * 1.������������ʱ����ʹ�ýӿ����ͣ���Ϊhibernate �ڻ�ȡ��������ʱ�����ص���Hibernate���õļ������ͣ�
	 * ������JavaSE һ����׼�ļ�������ʵ�֡�
	 * 
	 * 2.��Ҫ�����ϳ�ʼ������ʼ�����Եķ�ֹ������ָ���쳣
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

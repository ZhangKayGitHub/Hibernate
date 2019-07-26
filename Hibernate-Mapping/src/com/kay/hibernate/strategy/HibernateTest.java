package com.kay.hibernate.strategy;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateTest {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;
	
	@Before
	public void init(){
		System.out.println("init");
		Configuration configuration = new Configuration().configure();
		ServiceRegistry serviceRegistry = 
				new ServiceRegistryBuilder().applySettings(configuration.getProperties())
				.buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
	}
	
	@After
	public void destroy(){
		System.out.println("destory");
		transaction.commit();
		session.close();
		sessionFactory.close();
	}
	@Test 
	public void testMany2OneStrategy(){
//		Order1 order = (Order1) session.get(Order1.class, 1);
//		System.out.println(order.getCustomer().getCustomerName());
		
		List<Order1> orders = session.createQuery("FORM Order o").list();
		for(Order1 order : orders){
			if(order.getCustomer() != null){
				System.out.println(order.getCustomer().getCustomerName());
			}
		}
		
		/*
		 * 1.lazy ȡֵΪ proxy �� false �ֱ�����Ӧ��Ӧ���Բ����ӳټ�������������
		 * 2.fetch ȡֵΪjoin����ʾʹ�������������ӵķ�ʽ��ʼ�� n ��ϵ�� 1 ��һ�˵����ԣ����� lazy ����
		 * 3.batch-size�� ��������Ҫ������ 1 ��һ�˵� class Ԫ����:
		 * <class name="Customer" table="CUSTOMERS" lazy="true" batch-size="5">
		 * ����: һ�γ�ʼ���������ĸ���
		 * */
		
	}
	
	
	
	
	/*
	 * set ���ϵ�fetch ����: ȷʵ��ʼ�� orders ���ϵķ�ʽ��
	 * 1. Ĭ��ֵΪselect��ͨ�������ķ�ʽ����ʼ�� set Ԫ�ء�
	 * 2. ����ȡֵΪsubselect�� ͨ���Ӳ�ѯ�ķ�ʽ����ʼ�����е�set���ϣ��Ӳ�ѯ��Ϊwhere �Ӿ��in���������֣�
	 * �Ӳ�ѯ��ѯ���� 1 ��һ�˵�ID, ��ʱlazy ��Ч����batch-size()ʧЧ��
	 * 3. ��ȡֵΪjion�� ��
	 * 3.1 �ڼ��� 1 ��һ�˵Ķ���ʱ��ʹ��������������(ʹ���������ӽ��в�ѯ�� �ҰѼ������Խ��г�ʼ��)�ķ�ʽ���� n ��һ�˵� ��������
	 * 3.2 ���� lazy ���ԡ�
	 * 3.3 HQL ��ѯ���� fetch=join ��ȡֵ
	 * 
	 * */
	@Test
	public void testFetch2(){
		Customer customer = (Customer) session.get(Customer.class, 1);
		System.out.println(customer.getOrders().size());
	}
	@Test
	public void testFetch(){
List<Customer> customers = session.createQuery("FROM Customer").list();
		
		System.out.println(customers.size());
		for(Customer customer: customers){
			if(customer.getOrders() != null){
				System.out.println(customer.getOrders().size());
			}
		}
	}
	
	
	
	@Test
	public void testSetBatchSize(){
		List<Customer> customers = session.createQuery("FROM Customer").list();
		
		System.out.println(customers.size());
		for(Customer customer: customers){
			if(customer.getOrders() != null){
				System.out.println(customer.getOrders().size());
			}
		}
		//set Ԫ�ص� batch-size ���ԣ��趨һ�γ�ʼ�� set ���ϵ�����
		
	}
	
	
	
	
	@Test
	public void testOne2ManyLevelStrategy(){
		Customer customer = (Customer) session.get(Customer.class, 1);
		System.out.println(customer.getCustomerName());
		
		System.out.println(customer.getOrders().size());
		
		Order1 order = new Order1();
		
		order.setOrder1Id(1);
		
		System.out.println(customer.getOrders().contains(order));
		
		//�Լ��ϵĴ��������г�ʼ������
		Hibernate.initialize(customer.getOrders());
		
		
		//-------------set �� lazy ����----------------
		//1.1-n �� n-n �ļ�������Ĭ��ʹ�������ؼ�������
		//2.����ͨ�� set �� lazy�������޸�Ĭ�ϵļ�������Ĭ��Ϊtrue
		//������������Ϊfalse
		//3.ʵ�ʻ���������Ϊextra��ǿ���ӳټ�������ȡֵ�ᾡ���ܵ��ӳټ��ϳ�ʼ����ʱ����
		
	}
	
	
	
	
	@Test
	public void testClassLevelstrategy(){
		Customer customer = (Customer) session.load(Customer.class, 1);
		System.out.println(customer.getClass());
		
		System.out.println(customer.getCustomerId());
		
		System.out.println(customer.getCustomerName());
		
	}
	
}
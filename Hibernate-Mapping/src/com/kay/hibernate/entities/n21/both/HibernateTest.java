package com.kay.hibernate.entities.n21.both;

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
	
	/*********************单向关联关系***************************/
	@Test
	public void testDelete(){
		//在不设定级联关系的情况下，且1 这一端的对象在引用，不能直接删除 1 这一端的对象
		Customer customer = (Customer) session.get(Customer.class, 1);
		session.delete(customer);
		
	}
	
	@Test
	public void testUpdate(){
		Order1 order = (Order1) session.get(Order1.class, 1);
		order.getCustomer().setCustomerName("AAA");
		
	}
	
	@Test
	public void testMany2OneGet(){
		//1.若查询的一端的一个对象，则默认情况下，只会查询多的一端的对象，而没有查询关联的 1 的那一端的对象
		Order1 order = (Order1) session.get(Order1.class, 1);
		System.out.println(order.getOrder1Name());
		
		System.out.println(order.getCustomer().getClass().getName());
		
		session.close();
		
		//2.在需要使用到关联的对象时，才发送对应的SQL 语句
		Customer customer = order.getCustomer();
		System.out.println(customer.getCustomerName());
		//3. 在查询Customer 对象时，由多的一端导航到 1 的一端时，
		//若此时 session 已经关闭，则默认情况下会发生LazyInitializationException异常
		
		//4.获取Order 对象时，默认情况下，其关联的Customer 对象时一个代理对象
		
	}
	
	
	
	/***********************双向关联关系************************/
	
	@Test
	public void testUpdate2(){
		
		Customer customer = (Customer) session.get(Customer.class,1);
		customer.getOrders().iterator().next().setOrder1Name("GGG");
		
	}
	
	@Test
	public void testOne2ManyGet(){
		//1. 对  n 的一端的集合使用延迟加载
		Customer customer = (Customer) session.get(Customer.class, 1);
		System.out.println(customer.getCustomerName());
		
		
		
		//2.返回的多的一端的集合时Hibernate 内置的集合类型
		//该类型具有延迟加载和存放代理对象的功能。
		System.out.println(customer.getOrders().getClass());
		
//		session.close();
//		//3.可能会抛出    LazyInitializationException  异常
//		System.out.println(customer.getOrders().size());
		
		//4.再需要使用集合中元素的时候进行初始化
	}
	
	
	
	@Test
	public void testMany2OneSave(){
		Customer customer = new Customer();
		customer.setCustomerName("CC");
		
		Order1 order1 = new Order1();
		order1.setOrder1Name("OEDER1-5");
		
		Order1 order2 = new Order1();
		order2.setOrder1Name("OEDER1-6");
		
		//设定关联关系
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		//执行save操作:先插入Customer,再插入Order1,3条INSERT, 2条 UPDATE
		//因为 1 的一端 和 n 的一端都维护关联关系 ，所以会多出UPDATE
		//可以在 1 的一端的 set 节点中 指定 inverse = true,来使 1 的一端放弃维护关联关系
		//键义设定 set 的 inverse=true , 建议先插入 1 的一端，后插入多的一端
		//好处使不会多出UPDATE 语句
//		session.save(customer);
//		
//		session.save(order1);
//		session.save(order2);
//		
		//先插入Order1，再插入Customer.3条 INSERT，4条UPDATE
		
		session.save(order1);
		session.save(order2);
		session.save(customer);
	}
	
	/****************************Hibernate_set 的 3 个属性*********************************/
	
	@Test
	public void testCascade(){
		Customer customer = (Customer) session.get(Customer.class, 3);
		customer.getOrders().clear();
	}

}

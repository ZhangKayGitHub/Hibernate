package com.kay.hibernate.entities.n21;

import static org.junit.Assert.*;

import org.hibernate.LazyInitializationException;
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
	
	
	
	
	@Test
	public void testMay2OneSave(){
		Customer customer = new Customer();
		customer.setCustomerName("CC");
		
		Order1 order1 = new Order1();
		order1.setOrder1Name("OEDER1-5");
		
		Order1 order2 = new Order1();
		order2.setOrder1Name("OEDER1-6");
		
		//设定关联关系
		
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//执行save操作:先插入Customer,再插入Order1,3条INSERT
		//先插入1 的一端，再插入 n的一端，只有INSERT 语句。
//		session.save(customer);
//		session.save(order1);
//		session.save(order2);
		
		//先插入Order1，再插入Customer.3条 INSERT，2条UPDATE
		//先插入n的一端，再插入 n 的一端，会多处UPDATE 语句！
		//因为再插入多的一端时，无法确定 1 的一端的外键值，所以只能 等 1 的一端插入之后，再额外发送UPDATE语句
		//推荐先插入 1 的一端，然后插入 n 的一端
		session.save(order1);
		session.save(order2);
		session.save(customer);
	}

}

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
		 * 1.lazy 取值为 proxy 和 false 分别代表对应对应属性采用延迟检索和立即检索
		 * 2.fetch 取值为join，表示使用迫切左外连接的方式初始化 n 关系的 1 的一端的属性，忽略 lazy 属性
		 * 3.batch-size， 该属性需要设置在 1 那一端的 class 元素中:
		 * <class name="Customer" table="CUSTOMERS" lazy="true" batch-size="5">
		 * 作用: 一次初始化代理对象的个数
		 * */
		
	}
	
	
	
	
	/*
	 * set 集合的fetch 属性: 确实初始化 orders 集合的方式。
	 * 1. 默认值为select，通过正常的方式来初始化 set 元素。
	 * 2. 可以取值为subselect。 通过子查询的方式来初始化所有的set集合，子查询作为where 子句的in的条件出现，
	 * 子查询查询所有 1 的一端的ID, 此时lazy 有效，但batch-size()失效。
	 * 3. 若取值为jion， 则
	 * 3.1 在加载 1 的一端的对象时，使用迫切左外连接(使用左外连接进行查询， 且把集合属性进行初始化)的方式检索 n 的一端的 集合属性
	 * 3.2 忽略 lazy 属性。
	 * 3.3 HQL 查询忽略 fetch=join 的取值
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
		//set 元素的 batch-size 属性；设定一次初始化 set 集合的数量
		
	}
	
	
	
	
	@Test
	public void testOne2ManyLevelStrategy(){
		Customer customer = (Customer) session.get(Customer.class, 1);
		System.out.println(customer.getCustomerName());
		
		System.out.println(customer.getOrders().size());
		
		Order1 order = new Order1();
		
		order.setOrder1Id(1);
		
		System.out.println(customer.getOrders().contains(order));
		
		//对集合的代理对象进行初始化操作
		Hibernate.initialize(customer.getOrders());
		
		
		//-------------set 的 lazy 属性----------------
		//1.1-n 或 n-n 的集合属性默认使用懒加载检索策略
		//2.可以通过 set 的 lazy属性来修改默认的检索策略默认为true
		//并不建议设置为false
		//3.实际还可以设置为extra增强的延迟检索，该取值会尽可能的延迟集合初始化的时机。
		
	}
	
	
	
	
	@Test
	public void testClassLevelstrategy(){
		Customer customer = (Customer) session.load(Customer.class, 1);
		System.out.println(customer.getClass());
		
		System.out.println(customer.getCustomerId());
		
		System.out.println(customer.getCustomerName());
		
	}
	
}
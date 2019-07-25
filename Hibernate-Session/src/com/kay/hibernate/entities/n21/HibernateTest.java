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
		//�ڲ��趨������ϵ������£���1 ��һ�˵Ķ��������ã�����ֱ��ɾ�� 1 ��һ�˵Ķ���
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
		//1.����ѯ��һ�˵�һ��������Ĭ������£�ֻ���ѯ���һ�˵Ķ��󣬶�û�в�ѯ������ 1 ����һ�˵Ķ���
		Order1 order = (Order1) session.get(Order1.class, 1);
		System.out.println(order.getOrder1Name());
		
		System.out.println(order.getCustomer().getClass().getName());
		
		session.close();
		
		//2.����Ҫʹ�õ������Ķ���ʱ���ŷ��Ͷ�Ӧ��SQL ���
		Customer customer = order.getCustomer();
		System.out.println(customer.getCustomerName());
		//3. �ڲ�ѯCustomer ����ʱ���ɶ��һ�˵����� 1 ��һ��ʱ��
		//����ʱ session �Ѿ��رգ���Ĭ������»ᷢ��LazyInitializationException�쳣
		
		//4.��ȡOrder ����ʱ��Ĭ������£��������Customer ����ʱһ���������
		
	}
	
	
	
	
	@Test
	public void testMay2OneSave(){
		Customer customer = new Customer();
		customer.setCustomerName("CC");
		
		Order1 order1 = new Order1();
		order1.setOrder1Name("OEDER1-5");
		
		Order1 order2 = new Order1();
		order2.setOrder1Name("OEDER1-6");
		
		//�趨������ϵ
		
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//ִ��save����:�Ȳ���Customer,�ٲ���Order1,3��INSERT
		//�Ȳ���1 ��һ�ˣ��ٲ��� n��һ�ˣ�ֻ��INSERT ��䡣
//		session.save(customer);
//		session.save(order1);
//		session.save(order2);
		
		//�Ȳ���Order1���ٲ���Customer.3�� INSERT��2��UPDATE
		//�Ȳ���n��һ�ˣ��ٲ��� n ��һ�ˣ���ദUPDATE ��䣡
		//��Ϊ�ٲ�����һ��ʱ���޷�ȷ�� 1 ��һ�˵����ֵ������ֻ�� �� 1 ��һ�˲���֮���ٶ��ⷢ��UPDATE���
		//�Ƽ��Ȳ��� 1 ��һ�ˣ�Ȼ����� n ��һ��
		session.save(order1);
		session.save(order2);
		session.save(customer);
	}

}

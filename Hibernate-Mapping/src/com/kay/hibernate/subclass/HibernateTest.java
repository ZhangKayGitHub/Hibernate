package com.kay.hibernate.subclass;

import static org.junit.Assert.*;

import java.util.List;

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
	
	
	/*
	 * ȱ��:
	 * 1.ʹ�ñ�����С�
	 * 2.������е��Զ�������ӷǿ�Լ��
	 * 3.���̳еĲ�ν�������ݱ���ֶ�Ҳ��϶�
	 * */
	/*
	 * ��ѯ��
	 * 1.��ѯ�����¼��ֻ��Ҫ��ѯһ�����ݱ�
	 * 2.���������¼��Ҳֻ��Ҫ��ѯһ�����ݱ�
	 * */
	
	@Test
	public void testQuery(){
		List<Person> persons = session.createQuery("FROM Person").list();
		System.out.println(persons.size());
		
		List<Student> stus = session.createQuery("FROM Student").list();
		System.out.println(stus.size());
	}
	
	
	/*
	 * �������:
	 * 1.�����������ֻ��Ѽ�¼���뵽һ�����ݱ��С�
	 * 2.���������Hibernate �Զ�ά��
	 * 
	 * */
	@Test
	public void testSave(){
		
		Person person = new Person();
		person.setAge(11);
		person.setName("AA");
		
		session.save(person);
		Student stu = new Student();
		stu.setAge(22);
		stu.setName("BB");
		stu.setSchool("Kay");
		session.save(stu);
		
	}
}

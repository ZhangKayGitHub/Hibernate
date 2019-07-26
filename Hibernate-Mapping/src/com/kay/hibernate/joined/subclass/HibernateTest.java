package com.kay.hibernate.joined.subclass;

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
	 * �ŵ�:
	 * 1.����Ҫʹ�ñ�����С�
	 * 2.������е��ֶ�����ӷǿ�Լ��
	 * 3.û��������ֶ�
	 * */
	/*
	 * ��ѯ��
	 * 1.��ѯ�����¼����һ���������Ӳ�ѯ
	 * 2.���������¼����һ���������Ӳ�ѯ
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
	 * 1.�����������������Ҫ���뵽�������ݱ���
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

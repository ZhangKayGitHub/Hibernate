package com.kay.hibernate.one2one.foreign;

import static org.junit.Assert.*;

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
	public void testGet2(){
		//�ڲ�ѯû�������ʵ�����ʱ��ʹ�õ��������Ӳ�ѯ��һ����ѯ��������Ķ���
		//���Ѿ����г�ʼ��
		Manager mgr = (Manager) session.get(Manager.class, 1);
		System.out.println(mgr.getMgrName());
		System.out.println(mgr.getDept().getDeptName());
				
	}
	
	@Test
	public void testGet(){
		
		//1.Ĭ������¶Թ�������ʹ��������
		
		Department dept = (Department) session.get(Department.class, 1);
		System.out.println(dept.getDeptName());
		
		
		//2.���Ի�����������쳣�����⣬��session.close()�رպ�Ҫ�������
//		session.close();
//		
//		Manager mgr = dept.getMgr();
//		System.out.println(mgr.getClass());
//		//��session.close()�ر�ʱ������Ҫ��������ǲŻᷢ���쳣��������ϱ�ô����������ǲ��ᷢ���쳣
//		System.out.println(mgr.getMgrName());
		
		//3.��ѯManager �������������Ӧ����dept.manager_id = mgr.manager_id
		//����Ӧ���� dept.dept_id = mgr.manager_id
		Manager mgr = dept.getMgr();
		System.out.println(mgr.getMgrName());
	}
	
	
	@Test
	public void testSave() {
		Department department = new Department();
		department.setDeptName("DEPT-BB");
		
		Manager manager = new Manager();
		manager.setMgrName("MGR-BB");
		
		//�趨������ϵ
		department.setMgr(manager);
		manager.setDept(department);
		//�������
//		session.save(manager);
//		session.save(department);
		
		
		//˳��ߵ����ж��һ��UPDATE���
		//�����ȱ���û������еĶ������������UPDATE ���
		session.save(department);
		session.save(manager);
	}

}

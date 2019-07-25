package com.kay.hibernate.helloworld;

import static org.junit.Assert.*;

import java.sql.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Test;

public class HibernateTest {

	@Test
	public void test() {
		//1������һ��SessionFactory����
		/*
		 * SessionFactory: ��Ե������ݿ�ӳ���ϵ�����������ڴ澵�����̰߳�ȫ�ġ�
		 * SessionFactory����һ��������ϣ����������ض���������Ϣ
		 * SessionFactory������Session�Ĺ���
		 * ����SessionFactory��������Դ��һ�������һ��Ӧ��ֻ��ʼ��һ��SessionFactory����
		 * Hibernate4������һ����ServiceRegistry�ӿڣ����еĻ���Hibernate�����û��߷��񶼱���ͳһ�����ServiceRegistryע��������Ч
		 * Hibernate4�д���SessionFactory�Ĳ���:
		 * 
		 * */
		SessionFactory sessionFactory = null;
		
		//1).����һ��configuration����:��Ӧhibernate�Ļ�����h��Ϣ�Ͷ����ϵӳ����Ϣ
		/*
		 * Configuration�������hibernate ��������Ϣ��
		 * Hibernate���еĵײ���Ϣ�����ݿ��URL���û��������롢JDBC���������ݿ�Diabetic���ݿ����ӳص�(��Ӧhibernate.cfg.xml�ļ�)
		 * �־û��������ݱ��ӳ���ϵ(*.hbm.xml�ļ�)
		 * ����Configuration�����ַ�ʽ��
		 * 1�������ļ�(hibernate.properties):
		 * 	.Configuration cfg = new Configuration();
		 * 2��Xml�ļ�(hibernate.cfg.xml)
		 * 	.Configuration cfg = new Configuration().configure();
		 * 3��Configuration �� configure ������֧�ִ������ķ���
		 * 	.File file = new File("simple.xml");
		 * 	.Configuration cfg = new Configuration().configure(file);
		 * */
		Configuration configuration = new Configuration().configure();
		
		//4.0֮ǰ��������
//		sessionFactory = configuration.buildSessionFactory();
		//2).����һ��ServiceRegistry����:hibernate 4.x ����ӵĶ���
		//hibernate ���κ����úͷ���������Ҫ�ڸö�����ע����������
		ServiceRegistry serviceRegistry = 
				new ServiceRegistryBuilder().applySettings(configuration.getProperties())
				.buildServiceRegistry();
		//3).
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		//2������һ��Session����
		/*
		 * .Session�ӿ�ʱHibernate��Ӧ�ó����ṩ�Ĳ������ݵ�����Ҫ�Ľӿڣ����ṩ�����ı��棬���£�ɾ���ͼ���Java����ķ�����
		 * .Sessionʱ����һ�����棬λ�ڻ����еĶ����Ϊ�־û������������ݿ�����صļ�¼��Ӧ��Session�ܹ���ĳЩʱ��㣬���ջ�����
		 * ����ı仯��ִ����Ӧ��SQL��䣬��ͬ���������ݿ⣬��һ���̱���Ϊˢ�»���(flush)
		 * .վ�ڳ־û��ĽǶȣ�Hibernate�Ѷ����Ϊ4��״̬:�־û�״̬����ʱ״̬������״̬��ɾ��״̬��
		 * Session���ض��ķ�����ʹ�����һ��״̬ת������һ��״̬
		 * 
		 * */
		/*
		 * Session ��ķ�����
		 * -ȡ�ó־û�����ķ�����get()load()
		 * -�־û����󶼵õ����棬���º�ɾ����
		 * save(),update(),saveOrUpdate(),delete()
		 * -��������beginTransaction();
		 * -����Session�ķ���:isOpen(),flush(),clear(),evict(),close()��
		 * */
		Session session = sessionFactory.openSession();
		//3����������
		/*
		 * Transcation(����):
		 * 	.����һ��ԭ�Ӳ��������������ݿ�����ĸ�����г־ò㶼Ӧ������������½��У���ʹ��ֻ��������
		 * 	Transaction tx = session.beginTrasaction();
		 * ���÷���:
		 * 	-commit():�ύ�������sessionʵ��
		 * 	-rollback():�����������
		 * 	-wasCommitted():��������Ƿ��ύ
		 * */
		Transaction transaction = session.beginTransaction();
		
		//4��ִ�б������
		News news = new News("Java","kay",new Date(new java.util.Date().getTime()));
		session.save(news);
		//5���ύ����
		transaction.commit();
		
		//6���ر�Session
		session.close();
		
		//7���ر�SessionFactory����
		sessionFactory.close();
		
	}

}

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
		//1、创建一个SessionFactory对象
		/*
		 * SessionFactory: 针对单个数据库映射关系经过编译后的内存镜像，是线程安全的。
		 * SessionFactory对象一旦构造完毕，即被赋予特定的配置信息
		 * SessionFactory是生产Session的工厂
		 * 构造SessionFactory很消耗资源，一般情况下一个应用只初始化一个SessionFactory对象。
		 * Hibernate4新增了一个额ServiceRegistry接口，所有的基于Hibernate的配置或者服务都必须统一向这个ServiceRegistry注册后才能生效
		 * Hibernate4中创建SessionFactory的步骤:
		 * 
		 * */
		SessionFactory sessionFactory = null;
		
		//1).创建一个configuration对象:对应hibernate的基本配h信息和对象关系映射信息
		/*
		 * Configuration负责管理hibernate 的配置信息。
		 * Hibernate运行的底层信息：数据库的URL、用户名、密码、JDBC驱动，数据库Diabetic数据库连接池等(对应hibernate.cfg.xml文件)
		 * 持久化类与数据表的映射关系(*.hbm.xml文件)
		 * 创建Configuration的两种方式：
		 * 1）属性文件(hibernate.properties):
		 * 	.Configuration cfg = new Configuration();
		 * 2）Xml文件(hibernate.cfg.xml)
		 * 	.Configuration cfg = new Configuration().configure();
		 * 3）Configuration 的 configure 方法还支持带参数的访问
		 * 	.File file = new File("simple.xml");
		 * 	.Configuration cfg = new Configuration().configure(file);
		 * */
		Configuration configuration = new Configuration().configure();
		
		//4.0之前这样创建
//		sessionFactory = configuration.buildSessionFactory();
		//2).创建一个ServiceRegistry对象:hibernate 4.x 新添加的对象
		//hibernate 的任何配置和服务器都需要在该对象中注册后才能邮箱
		ServiceRegistry serviceRegistry = 
				new ServiceRegistryBuilder().applySettings(configuration.getProperties())
				.buildServiceRegistry();
		//3).
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		//2、创建一个Session对象
		/*
		 * .Session接口时Hibernate向应用程序提供的操作数据的最主要的接口，它提供基本的保存，更新，删除和加载Java对象的方法。
		 * .Session时具有一个缓存，位于缓存中的对象称为持久化对象，它和数据库中相关的记录对应。Session能够在某些时间点，按照缓存中
		 * 对象的变化来执行响应的SQL语句，来同步更新数据库，这一过程被称为刷新缓存(flush)
		 * .站在持久化的角度，Hibernate把对象分为4种状态:持久化状态、临时状态、游离状态、删除状态。
		 * Session的特定的方法能使对象从一个状态转换到另一个状态
		 * 
		 * */
		/*
		 * Session 类的方法：
		 * -取得持久化对象的方法：get()load()
		 * -持久化对象都得到保存，更新和删除：
		 * save(),update(),saveOrUpdate(),delete()
		 * -开启事务：beginTransaction();
		 * -管理Session的方法:isOpen(),flush(),clear(),evict(),close()等
		 * */
		Session session = sessionFactory.openSession();
		//3、开启事务
		/*
		 * Transcation(事务):
		 * 	.代表一次原子操作，它具有数据库事务的概念。所有持久层都应该在事务管理下进行，即使是只读操作。
		 * 	Transaction tx = session.beginTrasaction();
		 * 常用方法:
		 * 	-commit():提交相关联的session实例
		 * 	-rollback():撤销事务操作
		 * 	-wasCommitted():检查事务是否提交
		 * */
		Transaction transaction = session.beginTransaction();
		
		//4、执行保存操作
		News news = new News("Java","kay",new Date(new java.util.Date().getTime()));
		session.save(news);
		//5、提交事务
		transaction.commit();
		
		//6、关闭Session
		session.close();
		
		//7、关闭SessionFactory对象
		sessionFactory.close();
		
	}

}

package com.kay.hibernate.entities;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
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
	/****************************hibernate ��ɹ�ϵӳ��********************************/
	@Test
	public void testComponent(){
		Worker worker = new Worker();
		Pay pay = new Pay();
		pay.setMonthlyPay(1000);
		pay.setYearPay(100000);
		pay.setVocationWithPay(5);
		
		worker.setName("ABCD");
		worker.setPay(pay);
		session.save(worker);
		
	}
	
	/**************************Hibernate ��Ķ������͵�ӳ��
	 * @throws IOException 
	 * @throws SQLException **********************/
	@Test
	public void testBlob() throws IOException, SQLException{
//		News news = new News();
//		news.setAuthor("cc");
//		news.setContent("CONTENT");
//		news.setDate(new Date());
//		news.setDesc("DESC");
//		news.setTitle("CC");
//		InputStream stream = new FileInputStream("m_b_v2.jpg");
//		Blob image = Hibernate.getLobCreator(session).createBlob(stream,stream.available());
//		news.setImage(image);
//		
//		session.save(news);
		
		//��ȡ
		News news = (News) session.get(News.class, 1);
		Blob image = news.getImage();
		
		InputStream in = image.getBinaryStream();
		System.out.println(in.available());
		
	}
	
	/*******************************Hibernate ��������****************************/
	/*
	 * <property name="desc" formula="(SELECT concat(author,':',title) FROM NEWS n WHERE n.id=id)">
	 * */
	@Test
	public void testPropertyUpdate(){
		
		News news = (News) session.get(News.class, 1);
		news.setTitle("bbbb");
		System.out.println(news.getDesc());
		System.out.println(news.getDate());
		
	}
	
	/************************Hibernate ӳ��**********************************/
	
	/*
	 * ��̬����
	 * ��News.hbm.xmlӳ���ļ��� class��ǩ�����  dynamic-update="true" ��������̬���µĲ���
	 * */
	@Test
	public void testDynamicUpdate(){
		
		News news = (News)session.get(News.class, 1);
		news.setAuthor("JDBC");//�����Ļ�update����л���������ֶ�
		
	}
	/***********************Hibernate OID
	 * @throws InterruptedException *******************************/
	
	
	/*
	 * Generator
	 * 
	 *	.Hibernateʹ�ö����ʶ��(OID)�������ڴ��еĶ�������ݿ���м�¼�Ķ�Ӧ��ϵ.�����OID�����ݱ��������Ӧ.Hibernateͨ����ʶ����������Ϊ������ֵ
	 *	.Hibernate �Ƽ� �����ݱ���ʹ�ô�������,�����߱�ҵ������ֶ�.��������ͨ��Ϊ��������,��Ϊ�������ͱ��ַ�������Ҫ��ʡ��������ݿ�ռ�.
	 *	.�ڶ���-��ϵӳ���ļ��У�<id>Ԫ���������ö����ʶ��.<generator>��Ԫ�������趨��ʶ��������
	 *	.Hibernate�ṩ�˱�ʶ���������ӿ�: ldentifierGenerator,���ṩ�˸�������ʵ��
	 * 
	 * 

	 * 
	 * 
	 * 
	 * */
	
	@Test
	public void testIdGrnerator() throws InterruptedException{
		News news = new News(null,"AA","aa",new Date());
		session.save(news);//�����̰߳�ȫ����
			Thread.sleep(5000);
		
	}
	
	/****************************Session���ķ���********************************/
	/*
	 * ���д洢����
	 * Hibernate���ô洢����
	 * 		.Work�ӿڣ�ֱ��ͨ��JDBC API���������ݿ�Ĳ���
	 * 		.Session��doWork(work) ��������ִ��Work����ָ���Ĳ�����
	 * 		�����õ���Work �Ķ���� execute()������Session��ѵ�ǰʹ�õ����ݿ����Ӵ��ݸ�execute()������
	 * */
	@Test
	public void testDoWork(){
		session.doWork(new Work(){
			@Override
			public void execute(Connection connetion) throws SQLException {
				// TODO Auto-generated method stub
				System.out.println(connetion);
				
				//���ô洢����
			}
		});
		
	}
	
	/*
	 * evict:��session �����а�ָ���ĳ־û������Ƴ�
	 * */
	@Test
	public void testEvict(){
		News news1 = (News) session.get(News.class, 1);
		News news2 = (News) session.get(News.class, 2);
		news1.setTitle("AA");
		news2.setTitle("BB");
		
		session.evict(news1);//�ò�����news1��session���Ƴ������ύ��ʱ��Ϳ�����news1��UPDATE�Ĳ�����
		
	}
	
	/*
	 * Session �� delete()����
	 * .Session �� delete()����������ɾ��һ���������Ҳ����ɾ��һ���־û�����
	 * .Session �� delete() �����������
	 * 	-�ƻ� ִ��һ��delete���
	 * 	-�Ѷ����Session������ɾ�����ö������ɾ��״̬��
	 * .Hibernate �� cfg.xml�����ļ�����һ�� hibernate.use_identifier_rollback���ԣ���Ĭ��ֵΪfalse��
	 * ����������Ϊtrue�����ı�delete() ������������Ϊ:delete()������ѳ־û��������������OID����Ϊnull�������Ǳ�Ϊ��ʱ����
	 * 
	 * 
	 * delete:ִ��ɾ��������ֻҪOID �����ݱ���һ����¼��Ӧ���ͻ�׼��ִ��delete����
	 * ��OID �����ݱ���û�ж�Ӧ�ļ�¼�����׳��쳣
	 * 
	 * ����ͨ������hibernate �����ļ� hibernate.use_identifier_rollback Ϊ true��
	 * ʹɾ������󣬰���OID ��Ϊnull��
	 * 
	 * */
	@Test
	public void testDelete(){
//		News news = new News();
//		news.setId(1);//�����Ӧ�����������
		
		News news = (News) session.get(News.class, 65536);//����һ���־û�����
		session.delete(news);
		
		session.delete(news);
		
	}
	
	
	
	/*
	 * Session��saveOrUpdate() ������
	 * .Session��saveOrUpdate() ����ͬʱ������save()�� update() �����Ĺ���
	 * .�ж�����Ϊ��ʱ����ı�׼��
	 * 	-Java�����OIDΪnull
	 * 	-ӳ���ļ���Ϊ<id> ������ unsaved-value ���ԣ�����Java�����OIDȡֵ�����unsaved-value ����ֵƥ��
	 * 
	 * ע�⣺
	 * 1.��OID ��λnull�������ݱ��л�û�к����Ӧ�ļ�¼�����׳��쳣
	 * 2.�˽�:OID ֵ����id �� unsaved-value ����ֵ�Ķ���Ҳ����Ϊ��һ������Ķ���
	 * 
	 * */
	@Test 
	public void testSaveOrUpdate(){
		News news = new News(null,"CC","cc",new Date());
		news.setId(1);//ִ�иò���ʱ����OIDΪ1�Ķ����Ӧ�ļ�¼����Ϊ��¼���Ѿ���һ��OIDΪ1�ļ�¼�ˡ�
		session.saveOrUpdate(news);
		
	}
	
	
	
	/*
	 * Session �� update()����
	 * .Session��update()����ʹһ������Ķ����Ϊ�־û����󣬲��Ҽƻ�ִ��һ��update��䡣
	 * .��ϣ��Session�����޸���News���������ʱ����ִ��update()��䣬
	 * ���԰�ӳ���ļ��е�<class>Ԫ�ص�select-before-update��Ϊtrue��������Ĭ��ΪΪfalse
	 * .��update()��������һ���������ʱ�������Session�Ļ������Ѿ�������ͬ��OID�ĳ־û����󣬻��׳��쳣
	 * .��update()��������һ���������ʱ������������в�������Ӧ�ļ�¼��Ҳ���׳��쳣
	 * 
	 * */
	/*
	 * 1.��Ҫ����һ���־û����󣬲���Ҫ��ʾ�ĵ���update ��������Ϊ�ڵ���Transaction ��commit()����ʱ������ִ��session ��flush������
	 * 2.����һ�����������Ҫ��ʽ�ĵ��� session �� update ���������԰�һ����������Ϊ�־û�����
	 * 
	 * ��Ҫע���:
	 * 1.����Ҫ���µ������������ݱ�ļ�¼�Ƿ�һ�£����ᷢ��UPADATE��䡣
	 * 	�������update ��������äĿ�Ĵ���update����أ�
	 * 	��.hbm.xml �ļ���class�ڵ�����select-befor-update=true (Ĭ��Ϊfalse)����ͨ������Ҫ���ø����ԣ��������û�����SELECT��䣩
	 * 
	 * 2.�����ݱ���û�ж�Ӧ�ļ�¼����������update ���������׳��쳣
	 * 
	 * 3.��update()��������һ���������ʱ�������Session �Ļ������Ѿ�������ͬOID �ĳ־û��Ķ��󣬻��׳��쳣��
	 * ��Ϊ��Session �����в���������OID ��ͬ�Ķ���
	 * */
	@Test
	public void testUpdate(){
		News news = (News) session.get(News.class, 1);
		news.setAuthor("Oracle");
		
		//session.update(news);
		
		transaction.commit();
		session.close();
		
		//news.setId(100);//������׳��쳣
		
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
		
		//News news2 = (News) session.get(News.class, 1);/news2��OID��news��OID��ͬ��ʹ��update���г־û�ʱ���׳��쳣
		
		news.setAuthor("Oracle");//news ������һ������Ķ���
		session.update(news);//����һ������Ķ������۸ö����Ƿ��޸Ķ������UPDATE���
	}
	
	
	
	/*
	 * 1.ִ��get����:���������ض���
	 * 	 ִ��load����������ʹ�øö����򲻻�����ִ�в�ѯ������������һ���������
	 * 
	 *		get ������������ load ���ӳټ���
	 *
	 *	2.load �������ܻ��׳� LazyInitializationException �쳣:����Ҫ��ʼ���������֮ǰ�Ѿ��ر���Session
	 *
	 *	3.�����ݿ���û�ж�Ӧ�ļ�¼����SessionҲû�б��رգ�ͬʱ��Ҫʹ�ö���ʱ
	 *		get ���� null
	 *		load ����ʹ�øö�����κ����ԣ�û���⣻����Ҫ��ʼ���ˣ��׳��쳣��
	 *	
	 * */
	
	@Test
	public void testLoad(){
		News news = (News) session.load(News.class, 1);
		//News news = (News) session.load(News.class, 10);
		System.out.println(news.getClass().getName());
		
		session.close();
		
		System.out.println(news);
		
	}
	
	
	@Test
	public void testGet(){
		News news = (News) session.get(News.class, 1);
		//News news = (News) session.get(News.class, 10);
		
		System.out.println(news.getClass().getName());
		
		session.close();
		
		System.out.println(news);
	}
	
	
	/*
	 * Session��save()����
	 * . Session��save()����ʹһ����ʱ����ת��Ϊ�־ö���
	 * 	.Session��save()�������һ�²���:
	 * 		-��News������뵽Session�����У�ʹ������־û�״̬
	 * 		-ѡ��ӳ���ļ�ָ���ı�ʶ����������Ϊ�־û��������Ψһ��OID
	 * 		��ʹ�ô��������������.setId()����ΪNews��������OIDʹ��Ч��
	 * 		-�ƻ�ִ��һ��insert���:��ˢ�»����ʱ��
	 * 	.Hibernateͨ���־û������OID��ά���������ݿ���ؼ�¼�Ķ�Ӧ��ϵ����News�����ڳ־û�״̬ʱ����������������޸�����ID
	 * 	.persist()��save()����:
	 * 	-����һ��OID��ΪNUll�Ķ���ִ��save()����ʱ����Ѹö�����һ���µ�oid���浽���ݿ��У���ִ��persist()����ʱ���׳�һ���쳣
	 * */
	
	/*
	 * persistͬ��Ҳ��ִ��INSERT���
	 * 
	 * ��save()������:
	 * �ڵ���persist����֮ǰ���������Ѿ���id�ˣ��򲻻�ִ��INSERT�������׳��쳣
	 * */
	
	@Test
	public void testpersist(){
		News news = new News();
		news.setTitle("BB");
		news.setAuthor("bb");
		news.setDate(new Date());
		
		System.out.println(news);
		session.persist(news);
		System.out.println(news);
		
	}
	
	/*
	 * 1.save()����
	 * 1).ʹһ����ʱ�������ɳ־û�����
	 * 2).Ϊ�������ID
	 * 3).��flush����ʱ����һ��INSERT���
	 * 4).��save ����֮ǰ��id ʱ��Ч��
	 * 5).�־û��Ķ����ID�ǲ��ܱ��޸ĵ�
	 * */
	
	@Test
	public void testSave(){
		News news = new News();
		news.setTitle("AA");
		news.setAuthor("aa");
		news.setDate(new Date());
		news.setId(100);
		
		System.out.println(news);
		session.save(news);
		//news.setId(100);
		System.out.println(news);
		
		
	}
	
	
	
	
	
	/************************************************Sessionһ������****************************************************/
	@Test
	public void testClear(){
		News news1 = (News) session.get(News.class, 1);
		
		session.clear();
		//�ϱߵ�һ����������˻�������������������Ҫִ�л�Ҫִ��SELECT���
		News news2 = (News) session.get(News.class, 1);
	}
	
	
	@Test
	public void testRefresh(){
		
		News news = (News) session.get(News.class, 1);
		System.out.println(news);
		
		
		session.refresh(news);
		/*�������öϵ㣬ִ�жϵ㵽����ʱ�ֶ��ĵ������иı�һ������,��һ�����Ч������һ�£�
		 *��ȻҲ�ٴ�ִ����һ��SELECT��䣬�������һ���ģ�ԭ���ǣ�����MySQL�ĸ��뼶������ģ�
		 *
		 *refresh():��ǿ�Ʒ���SELECT��䣬��ʹSession�����ж����״̬�����ݱ��ж�Ӧ�ļ�¼����һ��
		 *
		 *���ݿ�ĸ��뼶��:
		 *.����ͬʱ���еĶ�����񣬵���Щ����������ݿ�����ͬ������ʱ�������û�в�ȡ��Ҫ�ĸ�����ƣ��ͻᵼ�¸��ֲ�������:
		 *	-���:������������T1��T2��T1��ȡ���Ѿ���T2���µ���û�б��ύ���ֶ�֮����T2�ع���T1��ȡ�����ݾ�ʹ��ʱ����Ч�ġ�
		 *	-�����ظ���:������������T1,T2,T1��ȡ��һ���ֶΣ�Ȼ��T2�����˸��ֶ�֮��T�ٴζ�ȡͬһ���ֶΣ�ֵ�Ͳ�ͬ��
		 *	-�ö�:������������T1,T2,T1��һ�����ж�ȡһ���ֶΣ�Ȼ��T2�ٸ��ֶ��в�����һЩ�µ��У�֮�����T1�ٴ��ٴζ�ȡͬһ���ֶΣ�ֵ�Ͳ�ͬ�ˡ�
		 *.���ݿ�����ĸ�����:���ݿ�ϵͳ������и����Բ������и�������������������ǲ����໥Ӱ�죬������ֲ������⡣
		 *.һ�������������������ĳ̶ȳ�Ϊ���뼶�����ݿ�涨�˸��ָ��뼶�𣬲�ͬ�ĸ��뼶���Ӧ��ͬ�ĸ��ų̶ȣ����뼶��Խ�ߣ�����һ���Ծ�Խ�ã��������Ծ�Խ��
		 *
		 *
		 *��MySQL�����ø��뼶��
		 *.ÿ����һ��mysql���򣬾ͻ���һ�����������ݿ����ӣ�ÿ�����ݿ����Ӷ���һ��ȫ�ֱ���@@tx_isolation,��ʾ��ǰ��������뼶��MySQLĬ�ϵĸ��뼶��ΪRepeatableRead
		 *.�鿴��ǰ�ĸ��뼶��:SELECT @@tx_isolation;
		 *.���õ�ǰmySQL���ӵĸ��뼶��:
		 *	-settransaction isolation level read committed;
		 *.�������ݿ�ϵͳ��ȫ�ָ��뼶��:
		 *	-set globaltransaction isolation level read committed;
		 *
		 *��Hibernate�����ø��뼶��
		 *.JDBC���ݿ�����ʹ�����ݿ�ϵͳĬ�ϸ��뼶����Hibernate �������ļ��п�����ʾ�����ø��뼶��ÿһ�����뼶�𶼶�Ӧһ������:
		 *	- 1.READ UNCOMMITED
		 *	- 2.READ COMMITRD
		 *	- 4.REPEATABLEREAD
		 *	- 8.SERIALIZEABLE
		 *. Hibernateͨ��ΪHinernate ӳ���ļ�ָ��
		 *	hibenate.connection.isolation��������������ĸ��뼶��
		 **/
		System.out.println(news);
		
	}
	
	
	/*
	 * flush:ʹ���ݱ��еļ�¼��Session�����еĶ����״̬����һ�¡�Ϊ�˱���һ�£�����ܷ��Ͷ�Ӧ��SQL���
	 * 1.��Transaction��commit()�����У��ȵ���session��flush���������ύ����
	 * 2.flush()��������ܻᷢ��SQL��䣬�������ύ����
	 * 3.ע�⣺��δ�ύ�������ʾ�ĵ���session��flush()����֮ǰ��Ҳ���ܻ����flush() ������
	 * 1����ִ��HQL �� QBC ��ѯ�����Ƚ���flush() �������Եõ����ݱ����µļ�¼
	 * 2��������¼��ID���ɵײ����ݿ�ʹ�������ķ�ʽ���ɵ����ٵ���save()����ʱ���ͻ���������INSERT ���
	 * ��Ϊsave �����󣬱��뱣֤�����ID �Ǵ��ڵģ�
	 * */
	/*
	 * flush :Session ���ջ����ж�������Ա仯��ͬ���������ݿ�
	 * .Ĭ�������Session��һ��ʱ���ˢ�»���:
	 * 	-��ʾ�ĵ���Session �� flush()������
	 * 	-��Ӧ�ó������Transaction��commit()����ʱ���÷�����flushˢ�»��棬Ȼ�������ݿ��ύ����
	 * 	-��Ӧ�ó���ִ��һ����ѯ(HQL,Criteria)����ʱ�������������г־û�����������Ѿ������˱仯��
	 * ��flush���棬�Ա�֤��ѯ����ܹ���ӳ�־û����������״̬
	 * .flush������������:�������ʹ�õ���native����������OID����ô������Session��save()�����������ʱ��
	 * ������ִ�������ݿ�����ʵ���insert��䡣
	 * .commit()��flush()����������:flushִ��һϵ��sql��䣬�����ύ����commit�����ȵ���flush()������
	 * Ȼ���ύ�������ζ�Ŷ����ݿ�Ĳ�����Զ����������
	 * 
	 * ��ϣ���ı�flush��Ĭ��ʱ��㣬����ͨ��Session��setFlushMode()������ʾ�趨flush��ʱ���
	 * */
	@Test
	public void testSessionFlush2(){
		News news = new News(null, "Java","SUN",new Date());
		session.save(news);
	}
	@Test
	public void testSessionFlush(){
		News news = (News) session.get(News.class, 1);
		news.setAuthor("Oracle");
		//���ö϶����е���
//		session.flush();
//		System.out.println("flush");
		
		//ִ��HQL �� QBC ��ѯ��
		News news2 = (News) session.createCriteria(News.class).uniqueResult();
		System.out.println(news2);
	}
	
	
	@Test
	public void testSessionCache() {
		/*
		 *��Session�ӿڵ�ʵ�����а�����һϵ�е�Java���ϣ���Щ���Ϲ�����Session���棬
		 *ֻҪSessionʵ��û�н����������ڣ���û���������棬�������������еĶ���Ҳ��������������ڣ�
		 *Session����ɼ���HibernateӦ�ó���������ݿ��Ƶ��
		 * �������Ĵ������λ�ȡͬһ�����ݣ��ͷ�����һ�����ݿ�
		 * */
		News news = (News) session.get(News.class,1);
		System.out.println(news);
		
		News news2 = (News) session.get(News.class,1);
		System.out.println(news2);
	}
}

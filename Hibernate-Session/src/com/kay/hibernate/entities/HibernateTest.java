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
	/****************************hibernate 组成关系映射********************************/
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
	
	/**************************Hibernate 大的对象类型的映射
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
		
		//获取
		News news = (News) session.get(News.class, 1);
		Blob image = news.getImage();
		
		InputStream in = image.getBinaryStream();
		System.out.println(in.available());
		
	}
	
	/*******************************Hibernate 派生属性****************************/
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
	
	/************************Hibernate 映射**********************************/
	
	/*
	 * 动态更新
	 * 在News.hbm.xml映射文件的 class标签中添加  dynamic-update="true" 就是允许动态更新的操作
	 * */
	@Test
	public void testDynamicUpdate(){
		
		News news = (News)session.get(News.class, 1);
		news.setAuthor("JDBC");//这样的化update语句中会包含所有字段
		
	}
	/***********************Hibernate OID
	 * @throws InterruptedException *******************************/
	
	
	/*
	 * Generator
	 * 
	 *	.Hibernate使用对象标识符(OID)来建立内存中的对象和数据库表中记录的对应关系.对象的OID和数据表的主键对应.Hibernate通过标识符生成器来为主键赋值
	 *	.Hibernate 推荐 在数据表中使用代理主键,即不具备业务含义的字段.代理主键通常为整数类型,因为整数类型比字符串类型要节省更多的数据库空间.
	 *	.在对象-关系映射文件中，<id>元素用来设置对象标识符.<generator>子元素用来设定标识符生成器
	 *	.Hibernate提供了标识符生成器接口: ldentifierGenerator,并提供了各种内置实现
	 * 
	 * 

	 * 
	 * 
	 * 
	 * */
	
	@Test
	public void testIdGrnerator() throws InterruptedException{
		News news = new News(null,"AA","aa",new Date());
		session.save(news);//存在线程安全问题
			Thread.sleep(5000);
		
	}
	
	/****************************Session核心方法********************************/
	/*
	 * 自行存储过程
	 * Hibernate调用存储过程
	 * 		.Work接口：直接通过JDBC API来访问数据库的操作
	 * 		.Session的doWork(work) 方法用于执行Work对象指定的操作，
	 * 		即调用调用Work 的对象的 execute()方法，Session会把当前使用的数据库连接传递给execute()方法。
	 * */
	@Test
	public void testDoWork(){
		session.doWork(new Work(){
			@Override
			public void execute(Connection connetion) throws SQLException {
				// TODO Auto-generated method stub
				System.out.println(connetion);
				
				//调用存储过程
			}
		});
		
	}
	
	/*
	 * evict:从session 缓存中把指定的持久化对象移除
	 * */
	@Test
	public void testEvict(){
		News news1 = (News) session.get(News.class, 1);
		News news2 = (News) session.get(News.class, 2);
		news1.setTitle("AA");
		news2.setTitle("BB");
		
		session.evict(news1);//该操作将news1从session中移除，在提交的时候就看不到news1的UPDATE的操作了
		
	}
	
	/*
	 * Session 的 delete()方法
	 * .Session 的 delete()方法即可以删除一个游离对象，也可以删除一个持久化对象
	 * .Session 的 delete() 方法处理过程
	 * 	-计划 执行一条delete语句
	 * 	-把对象从Session缓存中删除，该对象进入删除状态。
	 * .Hibernate 的 cfg.xml配置文件中有一个 hibernate.use_identifier_rollback属性，其默认值为false，
	 * 若把它设置为true，将改变delete() 方法的运行行为:delete()方法会把持久化对象或游离对象的OID设置为null，是他们变为临时对象
	 * 
	 * 
	 * delete:执行删除操作。只要OID 和数据表中一条记录对应，就会准备执行delete操作
	 * 若OID 在数据表中没有对应的记录，则抛出异常
	 * 
	 * 可以通过设置hibernate 配置文件 hibernate.use_identifier_rollback 为 true，
	 * 使删除对象后，把其OID 置为null。
	 * 
	 * */
	@Test
	public void testDelete(){
//		News news = new News();
//		news.setId(1);//这里对应的是游离对象
		
		News news = (News) session.get(News.class, 65536);//这是一个持久化对象
		session.delete(news);
		
		session.delete(news);
		
	}
	
	
	
	/*
	 * Session的saveOrUpdate() 方法：
	 * .Session的saveOrUpdate() 方法同时包含了save()与 update() 方法的功能
	 * .判定对象为临时对象的标准：
	 * 	-Java对象的OID为null
	 * 	-映射文件中为<id> 设置了 unsaved-value 属性，并且Java对象的OID取值与这个unsaved-value 属性值匹配
	 * 
	 * 注意：
	 * 1.若OID 部位null，但数据表中还没有和其对应的记录，会抛出异常
	 * 2.了解:OID 值等于id 的 unsaved-value 属性值的对象，也被认为是一个游离的对象
	 * 
	 * */
	@Test 
	public void testSaveOrUpdate(){
		News news = new News(null,"CC","cc",new Date());
		news.setId(1);//执行该操作时更新OID为1的对象对应的记录，因为记录中已经有一个OID为1的记录了。
		session.saveOrUpdate(news);
		
	}
	
	
	
	/*
	 * Session 的 update()方法
	 * .Session的update()方法使一个游离的对象变为持久化对象，并且计划执行一条update语句。
	 * .若希望Session仅当修改了News对象的属性时，才执行update()语句，
	 * 可以把映射文件中的<class>元素的select-before-update设为true，该属性默认为为false
	 * .当update()方法关联一个游离对象时，如果在Session的缓存中已经存在相同的OID的持久化对象，会抛出异常
	 * .当update()方法关联一个游离对象时，如果在数据中不存在响应的记录，也会抛出异常
	 * 
	 * */
	/*
	 * 1.若要更新一个持久化对象，不需要显示的调用update 方法，因为在调用Transaction 的commit()方法时，会先执行session 的flush方法。
	 * 2.更新一个游离对象，需要显式的调用 session 的 update 方法。可以把一个游离对象变为持久化对象
	 * 
	 * 需要注意的:
	 * 1.无论要更新的游离对象和数据表的记录是否一致，都会发送UPADATE语句。
	 * 	如何能让update 方法不再盲目的触发update语句呢？
	 * 	在.hbm.xml 文件的class节点设置select-befor-update=true (默认为false)，但通常不需要设置该属性（这样配置会增加SELECT语句）
	 * 
	 * 2.若数据表中没有对应的记录，但还调用update 方法，会抛出异常
	 * 
	 * 3.当update()方法关联一个游离对象时，如果在Session 的缓存中已经存在相同OID 的持久化的对象，会抛出异常，
	 * 因为在Session 缓存中不能有两个OID 相同的对象！
	 * */
	@Test
	public void testUpdate(){
		News news = (News) session.get(News.class, 1);
		news.setAuthor("Oracle");
		
		//session.update(news);
		
		transaction.commit();
		session.close();
		
		//news.setId(100);//这里会抛出异常
		
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
		
		//News news2 = (News) session.get(News.class, 1);/news2的OID与news的OID相同当使用update进行持久化时会抛出异常
		
		news.setAuthor("Oracle");//news 现在是一个游离的对象
		session.update(news);//更新一个游离的对象，无论该对象是否被修改都会出发UPDATE语句
	}
	
	
	
	/*
	 * 1.执行get方法:会立即加载对象
	 * 	 执行load方法，若不使用该对象，则不会立即执行查询操作，而返回一个代理对象
	 * 
	 *		get 是立即检索， load 是延迟检索
	 *
	 *	2.load 方法可能会抛出 LazyInitializationException 异常:在需要初始化代理对象之前已经关闭了Session
	 *
	 *	3.若数据库中没有对应的记录，且Session也没有被关闭，同时需要使用对象时
	 *		get 返回 null
	 *		load 若不使用该对象的任何属性，没问题；若需要初始化了，抛出异常。
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
	 * Session的save()方法
	 * . Session的save()方法使一个临时对象转化为持久对象
	 * 	.Session的save()方法完成一下操作:
	 * 		-把News对象加入到Session缓存中，使他进入持久化状态
	 * 		-选用映射文件指定的标识符生成器，为持久化对象分配唯一的OID
	 * 		在使用代理主键的情况下.setId()方法为News对象设置OID使无效的
	 * 		-计划执行一条insert语句:在刷新缓存的时候
	 * 	.Hibernate通过持久化对象的OID来维持他和数据库相关记录的对应关系，当News对象处于持久化状态时，不允许程序随意修改它的ID
	 * 	.persist()和save()区别:
	 * 	-当对一个OID不为NUll的对象执行save()方法时，会把该对象以一个新的oid保存到数据库中，但执行persist()方法时会抛出一个异常
	 * */
	
	/*
	 * persist同样也会执行INSERT语句
	 * 
	 * 和save()的区别:
	 * 在调用persist方法之前，若对象已经有id了，则不会执行INSERT，而是抛出异常
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
	 * 1.save()方法
	 * 1).使一个临时化对象变成持久化对象
	 * 2).为对象分配ID
	 * 3).在flush缓存时发送一条INSERT语句
	 * 4).在save 方法之前的id 时无效的
	 * 5).持久化的对象的ID是不能别修改的
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
	
	
	
	
	
	/************************************************Session一级缓存****************************************************/
	@Test
	public void testClear(){
		News news1 = (News) session.get(News.class, 1);
		
		session.clear();
		//上边的一条代码清除了缓存所以下面的这条语句要执行还要执行SELECT语句
		News news2 = (News) session.get(News.class, 1);
	}
	
	
	@Test
	public void testRefresh(){
		
		News news = (News) session.get(News.class, 1);
		System.out.println(news);
		
		
		session.refresh(news);
		/*这里设置断点，执行断点到这里时手动的到数据中改变一下数据,看一下输出效果还是一致（
		 *虽然也再次执行了一下SELECT语句，结果还是一样的，原因是：由于MySQL的隔离级别决定的）
		 *
		 *refresh():会强制发送SELECT语句，以使Session缓存中对象的状态和数据表中对应的记录保持一致
		 *
		 *数据库的隔离级别:
		 *.对于同时运行的多个事务，当这些事务访问数据库中相同的数据时，如果，没有采取必要的隔离机制，就会导致各种并发问题:
		 *	-脏读:对于两个事物T1，T2，T1读取了已经被T2更新但还没有被提交的字段之后，若T2回滚，T1读取的内容就使临时且无效的。
		 *	-不可重复读:对于两个事物T1,T2,T1读取了一个字段，然后T2更新了该字段之后，T再次读取同一个字段，值就不同。
		 *	-幻读:对于两个事物T1,T2,T1从一个表中读取一个字段，然后T2再该字段中插入了一些新的行，之后，如果T1再次再次读取同一个字段，值就不同了。
		 *.数据库事务的隔离性:数据库系统必须具有隔离性并发运行各个事务的能力，是他们不会相互影响，避免各种并发问题。
		 *.一个事务与其他事务隔离的程度称为隔离级别，数据库规定了各种隔离级别，不同的隔离级别对应不同的干扰程度，隔离级别越高，数据一致性就越好，但并发性就越弱
		 *
		 *
		 *在MySQL中设置隔离级别：
		 *.每启动一个mysql程序，就会获得一个单独的数据库连接，每个数据库连接都有一个全局变量@@tx_isolation,表示当前的事务隔离级别，MySQL默认的隔离级别为RepeatableRead
		 *.查看当前的隔离级别:SELECT @@tx_isolation;
		 *.设置当前mySQL连接的隔离级别:
		 *	-settransaction isolation level read committed;
		 *.设置数据库系统的全局隔离级别:
		 *	-set globaltransaction isolation level read committed;
		 *
		 *在Hibernate中设置隔离级别
		 *.JDBC数据库连接使用数据库系统默认隔离级别，在Hibernate 的配置文件中可以显示的设置隔离级别，每一个隔离级别都对应一个整数:
		 *	- 1.READ UNCOMMITED
		 *	- 2.READ COMMITRD
		 *	- 4.REPEATABLEREAD
		 *	- 8.SERIALIZEABLE
		 *. Hibernate通过为Hinernate 映射文件指定
		 *	hibenate.connection.isolation属性来设置事务的隔离级别
		 **/
		System.out.println(news);
		
	}
	
	
	/*
	 * flush:使数据表中的记录和Session缓存中的对象的状态保持一致。为了保持一致，则可能发送对应的SQL语句
	 * 1.在Transaction的commit()方法中；先调用session的flush方法，再提交事务
	 * 2.flush()方法会可能会发送SQL语句，但不会提交事务
	 * 3.注意：再未提交事务后显示的调用session。flush()方法之前，也可能会进行flush() 操作。
	 * 1）。执行HQL 或 QBC 查询，会先进行flush() 操作，以得到数据表最新的记录
	 * 2）。若记录的ID是由底层数据库使用自增的方式生成的则再调用save()方法时，就会立即发送INSERT 语句
	 * 因为save 方法后，必须保证对象的ID 是存在的！
	 * */
	/*
	 * flush :Session 按照缓存中对象的属性变化来同步更新数据库
	 * .默认情况下Session再一下时间点刷新缓存:
	 * 	-显示的调用Session 的 flush()方法；
	 * 	-当应用程序调用Transaction的commit()方法时，该方法先flush刷新缓存，然后向数据库提交事务
	 * 	-当应用程序执行一个查询(HQL,Criteria)操作时，如果如果缓存中持久化对象的属性已经发生了变化，
	 * 先flush缓存，以保证查询结果能够反映持久化对象的最新状态
	 * .flush缓存的例外情况:如果对象使用的是native生成器生成OID，那么当调用Session的save()方法保存对象时，
	 * 会立即执行向数据库插入该实体的insert语句。
	 * .commit()和flush()方法的区别:flush执行一系列sql语句，但不提交事务；commit方法先调用flush()方法，
	 * 然后提交事务的意味着对数据库的操作永远保存下来。
	 * 
	 * 若希望改变flush的默认时间点，可以通过Session的setFlushMode()方法显示设定flush的时间点
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
		//设置断定进行调试
//		session.flush();
//		System.out.println("flush");
		
		//执行HQL 或 QBC 查询，
		News news2 = (News) session.createCriteria(News.class).uniqueResult();
		System.out.println(news2);
	}
	
	
	@Test
	public void testSessionCache() {
		/*
		 *在Session接口的实现类中包含了一系列的Java集合，这些集合构成了Session缓存，
		 *只要Session实例没有结束生命周期，且没有起立缓存，则存放在它缓存中的对象也不会结束生命周期，
		 *Session缓存可减少Hibernate应用程序访问数据库的频率
		 * 因此下面的代码两次获取同一条数据，就访问了一次数据库
		 * */
		News news = (News) session.get(News.class,1);
		System.out.println(news);
		
		News news2 = (News) session.get(News.class,1);
		System.out.println(news2);
	}
}

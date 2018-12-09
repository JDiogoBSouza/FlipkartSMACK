package actors.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.rdd.CassandraJavaRDD;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.Function2;
import datatypes.MapOrder;
import datatypes.SparkMessage;
import models.Kart;
import models.Order;
import models.Product;
import scala.Tuple2;
import utils.ProductsComparator;

public class SparkActor extends AbstractActor 
{
	SparkConf conf;
	JavaSparkContext context;
	private Cluster cluster;
	private Session session;
	
	@Override
	public void preStart()
	{
		System.out.println("Abrindo Conexao Spark com Cassandra");
		conf = new SparkConf().setAppName("flipkartSMACK").setMaster("local");		
		context = new JavaSparkContext(conf);

		//Initialise the resources to be used by actor e.g. db
		cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		session = cluster.connect("mykeyspace");
	}
	
	public static Props getProps()
	{
	        return Props.create(SparkActor.class, SparkActor::new);
	}
	
	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
	        .match(MapOrder.class, message -> { sender().tell( handleMessage(message), self());
	        })
	        .build();
	}
	
	private SparkMessage handleMessage(MapOrder message)
	{		
		//System.out.println("MapOrder Received by SparkActor");

		
		HashMap<Integer, Product> bestProducts = new HashMap<Integer, Product>();
		
		SparkMessage sparkMessage = new SparkMessage();
		
		Kart kart = message.getKart();
		sparkMessage.setKart(kart);
		
		long count = 0;
		
		if( !kart.isEmpty() )
		{
			System.out.println("Pedido Nao Vazio");
			
			long start = System.currentTimeMillis();
			
			JavaRDD<Product> rdd = CassandraJavaUtil.javaFunctions(context)
			        .cassandraTable("mykeyspace", "products", mapRowTo( Product.class ))
			        .select("product_id", "name", "price", "type", "quantity").repartition(context.defaultParallelism());
			        //.where("type = ?", product.getType() );
			
			long elapsedTimeMillis = System.currentTimeMillis()-start;
			System.out.println("Tempo de Leitura Spark - Cassandra " + elapsedTimeMillis + " ms");
			
//			// ONLY CASSANDRA QUERY
//			//ResultSet results = session.execute("SELECT * FROM products WHERE product_id = " + '0'); 
//			ResultSet results = session.execute("SELECT * FROM products"); 
//			
//			elapsedTimeMillis = System.currentTimeMillis()-start;
//			System.out.println("Tempo de Leitura Akka - Cassandra " + elapsedTimeMillis + " ms");
//			
//
//			start = System.currentTimeMillis();
//			Iterator<Row> iterator = results.iterator(); 
//			
//			if( iterator.hasNext() )
//			{
//				// Update
//				Row row = iterator.next();
//				String line = "Product_id = " + row.getInt("product_id") + " Name = "+ row.getString("name");
//				System.out.println(line);
//			}
			
			start = System.currentTimeMillis();
			
			List<Product> lista = rdd.collect();
			
			elapsedTimeMillis = System.currentTimeMillis()-start;
			System.out.println("Tempo de Coleta do RDD " + elapsedTimeMillis + " ms");
			

			JavaRDD<Product> productsRDD = context.parallelize(lista);		
			List<Product> listaFiltered = productsRDD.filter(  p -> kart.isInKart(p) ).collect();
			

			JavaRDD<Product> listaFilteredRDD = context.parallelize(listaFiltered);	
	        JavaPairRDD<Integer, Iterable<Product>> rddY = listaFilteredRDD.groupBy(p -> p.getType());

			
			for( Tuple2<Integer, Iterable<Product>> pair : rddY.collect() )
			{
				for( Product prod : pair._2 )
				{
					
					System.out.println("Tipo: " + pair._1);
					System.out.println("Produto: " + prod.getName() );
				}
			}
			
			for( Tuple2<Integer, Iterable<Product>> pair : rddY.collect() )
			{
				@SuppressWarnings("unchecked")
				List<Product> list = (ArrayList<Product>) IteratorUtils.toList(pair._2.iterator());
				
				JavaRDD<Product> typedList = context.parallelize(list);	
				Product bestProduct = typedList.min(new ProductsComparator());
				
				bestProducts.put(pair._1, bestProduct);
			}
		}
		else
		{
			System.out.println("Pedido Vazio");
		}
		
		sparkMessage.setResult(bestProducts);
		sparkMessage.setMessage(("Quantidade de Produtos Econtrados: " + count));
		sparkMessage.setControllerRef( message.getControllerRef() );
		
		return sparkMessage;
	}
	
	@Override
	public void postStop()
	{
		if( context != null )
		{
			System.out.println("Fechando Conexao Spark com Cassandra");
			context.close();
		}
	}
}

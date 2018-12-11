package actors.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.fasterxml.jackson.databind.JsonNode;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;

import akka.actor.AbstractActor;
import akka.actor.Props;
import datatypes.MapOrder;
import datatypes.MapSearch;
import datatypes.ProductSearchGet;
import datatypes.SearchResults;
import datatypes.SparkMessage;
import datatypes.TransactionDetails;
import datatypes.ValidOrder;
import models.Kart;
import models.Order;
import models.Product;
import play.libs.Json;
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
		
		conf = new SparkConf(true)
				.setAppName("flipkartSMACK")
		        .setMaster("local[*]");
		
		context = new JavaSparkContext(conf);
		
		//Initialise the resources to be used by actor e.g. db
		//cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		//session = cluster.connect("mykeyspace");
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
            .match(MapSearch.class, message -> { sender().tell( handleMessage(message), self());
            })
            .match(ProductSearchGet.class, message -> { sender().tell( handleMessage(message), self());
            })
            .match(ValidOrder.class, message -> { sender().tell( handleMessage(message), self());
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
			//System.out.println("Pedido Nao Vazio");
			
			long start = System.currentTimeMillis();
			
			JavaPairRDD<Integer, Iterable<Product>> listaFiltered = CassandraJavaUtil.javaFunctions(context)
			        .cassandraTable("mykeyspace", "products", mapRowTo( Product.class ))
			        .select("product_id", "name", "price", "type", "quantity")
			        .repartition(context.defaultParallelism())
			        .filter(  p -> kart.isInKart(p) )
					.groupBy(p -> p.getType());
			
			/*JavaRDD<Product> listaFiltered = CassandraJavaUtil.javaFunctions(context)
			        .cassandraTable("mykeyspace", "products", mapRowTo( Product.class ))
			        .select("product_id", "name", "price", "type", "quantity")
			        .repartition(context.defaultParallelism())
					.filter(  p -> kart.isInKart(p) );*/
			
			        //.where("type = ?", product.getType() );
			
			long elapsedTimeMillis = System.currentTimeMillis()-start;
			System.out.println("Tempo de Leitura + Filtragem - Spark - Cassandra " + elapsedTimeMillis + " ms");
			
			
			//start = System.currentTimeMillis();
			
			// -------------------- ONLY CASSANDRA QUERY -----------------------------
			
			//ResultSet results = session.execute("SELECT * FROM products WHERE product_id = " + '0'); 
			/*ResultSet results = session.execute("SELECT * FROM products"); 
			
			List<Product> cassandraList = new ArrayList<Product>();
			
			start = System.currentTimeMillis();
			Iterator<Row> iterator = results.iterator(); 
			
			MappingManager manager = new MappingManager(session);
			Mapper<Product> mapper = manager.mapper(Product.class);

			Result<Product> cassandraProducts = mapper.map(results);
			
			for (Product p : cassandraProducts)
			{
			    //System.out.println("Produto : " + p.getName() );
			    cassandraList.add(p);
			}
			
			JavaRDD<Product> productsRDD = context.parallelize(cassandraList);		
			JavaPairRDD<Integer, Iterable<Product>> listaFilteredCassandra = productsRDD.filter(  p -> kart.isInKart(p) )
													 						   			.groupBy(p -> p.getType());
			*/
			
			// --------------------------------- FINAL QUERY CASSANDRA -----------------------------------

			//elapsedTimeMillis = System.currentTimeMillis()-start;
			//System.out.println("Tempo de Leitura + Filtragem - Akka - Cassandra " + elapsedTimeMillis + " ms");
			
			/*for( Tuple2<Integer, Iterable<Product>> pair : listaFiltered.collect() )
			{
				for( Product prod : pair._2 )
				{
					
					System.out.println("Tipo: " + pair._1);
					System.out.println("Produto: " + prod.getName() );
				}
			}*/
		
			for( Tuple2<Integer, Iterable<Product>> pair : listaFiltered.collect() )
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
			//System.out.println("Pedido Vazio");
		}
		
		sparkMessage.setResult(bestProducts);
		sparkMessage.setMessage(("Quantidade de Produtos Econtrados: " + count));
		sparkMessage.setControllerRef( message.getControllerRef() );
		
		return sparkMessage;
	}
	
	private SearchResults handleMessage(MapSearch message)
	{		
		//System.out.println("MapOrder Received by SparkActor");
		
		Product prod = message.getProduct();
		
		JsonNode results = null;
		SearchResults searchResults = new SearchResults();
		
		if(prod != null)
		{
			long start = System.currentTimeMillis();
			
			List<Product> listaFiltered = CassandraJavaUtil.javaFunctions(context)
		        .cassandraTable("mykeyspace", "products", mapRowTo( Product.class ))
		        .select("product_id", "name", "price", "type", "quantity")
		        .repartition(context.defaultParallelism())
				.filter(  p -> p.getType() == prod.getType() )
				.takeOrdered(10, new ProductsComparator());
				
				long elapsedTimeMillis = System.currentTimeMillis()-start;
				System.out.println("Tempo de Leitura + Filtragem - Spark - Cassandra - SEARCH " + elapsedTimeMillis + " ms");
				
				results = Json.toJson(listaFiltered);
		}
		else
		{
			//System.out.println("Busca Invalida");	
		}
		
		
		searchResults.setJsonNode(results);
		searchResults.setControllerRef( message.getControllerRef() );
		
		return searchResults;
	}
	
	private SearchResults handleMessage(ProductSearchGet message)
	{		
		//System.out.println("MapOrder Received by SparkActor");
	
		JsonNode results = null;
		SearchResults searchResults = new SearchResults();
		
		if(message.getType() >= 0)
		{
			long start = System.currentTimeMillis();
			
			List<Product> listaFiltered = CassandraJavaUtil.javaFunctions(context)
		        .cassandraTable("mykeyspace", "products", mapRowTo( Product.class ))
		        .select("product_id", "name", "price", "type", "quantity")
		        .repartition(context.defaultParallelism())
				.filter(  p -> p.getType() == message.getType() )
				.takeOrdered(10, new ProductsComparator());
				
				long elapsedTimeMillis = System.currentTimeMillis()-start;
				System.out.println("Tempo de Leitura + Filtragem - Spark - Cassandra - SEARCH " + elapsedTimeMillis + " ms");
				
				results = Json.toJson(listaFiltered);
		}
		else
		{
			//System.out.println("Busca Invalida");	
		}
		
		
		searchResults.setJsonNode(results);
		searchResults.setControllerRef( message.getControllerRef() );
		
		return searchResults;
	}
	
	private TransactionDetails handleMessage(ValidOrder message)
	{
		List<Order> orders = message.getKart().getOrders();
		List<Product> products = new ArrayList<Product>();
		
		for( Order o : orders)
		{
			products.add(o.getProduct());
		}
		
		JavaRDD<Product> productsRDD = context.parallelize(products);
		
		CassandraJavaUtil.javaFunctions(productsRDD)
        .writerBuilder("mykeyspace", "products", mapToRow(Product.class)).saveToCassandra();
		
		TransactionDetails transactionDetails = new TransactionDetails("Successful Transaction", new Date());
		transactionDetails.setControllerRef( message.getControllerRef() );
		
		return transactionDetails;
	}
	
	@Override
	public void postStop()
	{
		if( context != null )
		{
			System.out.println("Fechando Conexao Spark com Cassandra");
			context.close();
		}
		
		if( cluster != null)
		{
			cluster.close();
		}
		
		if( session != null)
		{
			session.close();
		}
	}
}

package actors.basic;

import java.util.List;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import akka.actor.AbstractActor;
import akka.actor.Props;
import datatypes.MapOrder;
import datatypes.SparkMessage;

public class SparkActor extends AbstractActor 
{
	SparkConf conf;
	JavaSparkContext context;
	
	@Override
	public void preStart()
	{
		//conf = new SparkConf().setAppName("flipkartSMACK").setMaster("local");
		//context = new JavaSparkContext(conf);
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
		
		/*CassandraJavaRDD<CassandraRow> rdd = CassandraJavaUtil.javaFunctions(context)
		        .cassandraTable("mykeyspace", "products")
		        .select("product_id", "name", "quantity", "price");

		
		long count = rdd.count();
		System.out.println("Count: " + count);
		
		List<CassandraRow> lista = rdd.collect();

		for( Object row : lista )
		{
			CassandraRow r = (CassandraRow) row;
			int product_id = r.getInt("product_id");
			String name = r.getString("name");
			int quantity = r.getInt("quantity");
			double price = r.getDouble("price");
			
			System.out.println("Product_ID: " + product_id + " Name: " + name + " Quantity: " + quantity + " Price: " + price);
		}*/
		
		/*CassandraJavaPairRDD< Map<String, Integer> > cassandraRdd = CassandraJavaUtil.javaFunctions(context)
		        .cassandraTable("mykeyspace", "words", mapRowTo( Map.class ))
		        .select("key", "value");*/
    	
		/*HashMap<String, Integer> map = new HashMap<String, Integer>(); 
		Map<String, Integer> line = cassandraRdd.collectAsMap();
		map.putAll(line);
		
		for( Map.Entry<String, Integer> entry : map.entrySet() )
		{
			System.out.println("CARAI " + entry.getKey());
			System.out.println("DESGRAÇA " + entry.getValue());
		}*/
		
		SparkMessage sparkMessage = new SparkMessage("Quantidade de Produtos Econtrados: "); //+ count);
		sparkMessage.setControllerRef( message.getControllerRef() );
		
		return sparkMessage;
	}
	
	@Override
	public void postStop()
	{
		if( context != null )
		{
			context.close();
		}
	}
}

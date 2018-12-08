package actors.basic;

import java.util.ArrayList;
import java.util.List;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.rdd.CassandraJavaRDD;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.*;

import akka.actor.AbstractActor;
import akka.actor.Props;
import datatypes.MapOrder;
import datatypes.SparkMessage;
import models.Order;
import models.Product;
import utils.ProductsComparator;

public class SparkActor extends AbstractActor 
{
	SparkConf conf;
	JavaSparkContext context;
	
	@Override
	public void preStart()
	{
		conf = new SparkConf().setAppName("flipkartSMACK").setMaster("local");
		context = new JavaSparkContext(conf);
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

		SparkMessage sparkMessage = new SparkMessage();
		
		List<Order> orders = message.getArrayList();
		long count = 0;
		
		if( !orders.isEmpty() )
		{
			System.out.println("Pedido Nao Vazio");
			
			for( Order order : orders )
			{
				Product product = order.getProduct();
				int quantity = order.getQuantity();
				
				System.out.println("Produto Comprado: " + product.getName() + " Tipo: " + product.getType() + " Quantidade: " + quantity);
			
		
				CassandraJavaRDD<Product> rdd = CassandraJavaUtil.javaFunctions(context)
				        .cassandraTable("mykeyspace", "products", mapRowTo( Product.class ))
				        .select("product_id", "name", "price", "type", "quantity")
				        .where("type = ?", product.getType() );
		
		
				count = rdd.count();
				
				List<Product> lista = rdd.takeOrdered(1, new ProductsComparator());
		
				for( Product p : lista )
				{
					System.out.println("Product_ID: " + p.getProduct_id() + " Name: " + p.getName() + " Type: " + p.getType() +" Price: " + p.getPrice() + " Quantity: " + p.getQuantity());
					order.setProduct(p);
					sparkMessage.getResult().add(order);
				}		
			}
		}
		else
		{
			System.out.println("Pedido Vazio");
		}
		
		sparkMessage.setMessage(("Quantidade de Produtos Econtrados: " + count));
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

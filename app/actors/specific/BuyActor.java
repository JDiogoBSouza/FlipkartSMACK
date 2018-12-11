package actors.specific;

import java.util.Date;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import akka.actor.AbstractActor;
import akka.actor.Props;
import datatypes.TransactionDetails;
import datatypes.ValidOrder;
import models.Order;
import models.Product;

public class BuyActor extends AbstractActor
{
	private Cluster cluster;
	private Session session;
	
	@Override
	public void preStart()
	{
		//Initialise the resources to be used by actor e.g. db
		cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		session = cluster.connect("mykeyspace");
	}
	
	public static Props getProps()
	{
	        return Props.create(BuyActor.class, BuyActor::new);
	}
	
	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
            .match(ValidOrder.class,  
            		message -> { sender().tell( handleMessage(message), self());
            })
            .build();
	}
	
	private TransactionDetails handleMessage(ValidOrder message)
	{
		//System.out.println("ValidOrder Received by BuyActor");
		
		long start = System.currentTimeMillis();
		
		// -------------------- CASSANDRA SAVE -----------------------------

		MappingManager manager = new MappingManager(session);
		Mapper<Product> mapper = manager.mapper(Product.class);
		
		for( Order o : message.getKart().getOrders() )
		{
			Product p = o.getProduct();
			mapper.save(p);
		}
		
		// ------------------- FINAL CASSANDRA SAVE --------------------------

		long elapsedTimeMillis = System.currentTimeMillis()-start;
		System.out.println("Tempo de Save Akka - Cassandra " + elapsedTimeMillis + " ms");
		
		// Update values on database and return message and timestamp.
		
		TransactionDetails transactionDetails = new TransactionDetails("Successful Transaction", new Date());
		transactionDetails.setControllerRef( message.getControllerRef() );
		
		return transactionDetails;
	}
	
	@Override
	public void postStop()
	{
		// free up the resources held by actor e.g. db connection		
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
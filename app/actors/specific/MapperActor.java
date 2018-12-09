package actors.specific;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.AbstractActor;
import akka.actor.Props;
import datatypes.MapOrder;
import datatypes.RequestOrder;
import models.Kart;
import models.Order;
import play.libs.Json;

public class MapperActor extends AbstractActor
{
	@Override
	public void preStart()
	{
		//Initialise the resources to be used by actor e.g. db
	}
	
	public static Props getProps()
	{
	        return Props.create(PopuleStoreActor.class, PopuleStoreActor::new);
	}
	
	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
            .match(RequestOrder.class,  
            		message -> { sender().tell( handleMessage(message), self());
            })
            .build();
	}
	
	private MapOrder handleMessage(RequestOrder message)
	{
		//System.out.println("JsonNode Received by MapperActor");
		
		JsonNode productsBuy = message.getJsonNode();

		Kart kart = new Kart();
		
		for (JsonNode order : productsBuy.withArray("orders"))
		{
			Order newOrder = Json.fromJson(order, Order.class);
			
			// Desnecessary
			/*JsonNode product = order.with("product");
			Product newProduct = Json.fromJson(product, Product.class);
			
			newOrder.setProduct(newProduct);*/
			
			System.out.println("Tipo: " + newOrder.getProduct().getType());
			System.out.println("Quantidade: " + newOrder.getQuantity());
			kart.addOrder(newOrder);
		}
		
		MapOrder mapOrder = new MapOrder(kart);
		mapOrder.setControllerRef( message.getControllerRef() );
		
		return mapOrder;
	}
	
	@Override
	public void postStop()
	{
		// free up the resources held by actor e.g. db connection
	}
}
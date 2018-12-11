package actors.specific;

import java.util.HashMap;
import java.util.List;

import akka.actor.AbstractActor;
import akka.actor.Props;
import datatypes.AbstractOrder;
import datatypes.InvalidOrder;
import datatypes.SparkMessage;
import datatypes.ValidOrder;
import models.Order;
import models.Product;

public class CheckReserveActor extends AbstractActor
{	
	@Override
	public void preStart()
	{
		//Initialise the resources to be used by actor e.g. db
	}
	
	public static Props getProps()
	{
	        return Props.create(CheckReserveActor.class, CheckReserveActor::new);
	}
	
	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
            .match(SparkMessage.class,  
            		message -> { sender().tell( handleMessage(message), self()) ;
            })
            .build();
	}
	
	private AbstractOrder handleMessage(SparkMessage message)
	{		
		//System.out.println("SparkMessage Received by CheckReserveActor");
		//System.out.println("Mensagem Recebida: " + message.getMessage() );
		
		// Check products on database and send the new quantities after buy.
		boolean isValid = true;
		
		ValidOrder validOrder = new ValidOrder();
		validOrder.setControllerRef( message.getControllerRef() );
		
		HashMap<Integer, Product> bestProducts = message.getResult();
		List<Order> orders = message.getKart().getOrders();
		
		if(!bestProducts.isEmpty() && !orders.isEmpty() )
		{
			for( Order order : orders )
			{									
				Product stockProduct = bestProducts.get(order.getProduct().getType());
				int stockQuantity = bestProducts.get(order.getProduct().getType()).getQuantity();
				
				System.out.println("Quantidade em Estoque: " + stockQuantity);
				System.out.println("Quantidade Comprada:" + order.getQuantity());
				
				if( stockQuantity >= order.getQuantity() )
				{				
					stockProduct.setQuantity( stockQuantity - order.getQuantity() );
					
					order.setProduct(stockProduct);
					validOrder.getKart().addOrder(order);
				}
				else
				{
					isValid = false;
					break;
				}
			}
		}
		
		
		if( isValid )
		{
			/*System.out.println("New Products Values:");
			
			for( Order o : validOrder.getKart().getOrders() )
			{
				System.out.println("Nome: " + o.getProduct().getName() + " Type:" + o.getProduct().getType() + " Price:" + o.getProduct().getPrice() + " Quantity:" + o.getProduct().getQuantity());
			}*/
			
			return validOrder;
		}
		else
		{
			InvalidOrder invalidOrder = new InvalidOrder("Have some product(s) with insuficient stock.");
			invalidOrder.setControllerRef( message.getControllerRef() );
			
			return invalidOrder;
		}
	}
	
	@Override
	public void postStop()
	{
		// free up the resources held by actor e.g. db connection
	}
}
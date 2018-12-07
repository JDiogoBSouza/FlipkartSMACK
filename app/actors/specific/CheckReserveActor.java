package actors.specific;

import akka.actor.AbstractActor;
import akka.actor.Props;
import datatypes.AbstractOrder;
import datatypes.InvalidOrder;
import datatypes.SparkMessage;
import datatypes.ValidOrder;

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
		
		/*for( Order order : mapOrder.getArrayList() )
		{				
			// check if have enough products on stock database.
			
			ArrayList<Order> resultFromTypedActor = null;
			
			if( resultFromTypedActor == null )
			{
				validOrder.addOrders(resultFromTypedActor);
			}
			else
			{
				isValid = false;
				break;
			}
		}*/
		
		if( isValid )
		{
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
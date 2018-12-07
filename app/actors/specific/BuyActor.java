package actors.specific;

import java.util.Date;

import akka.actor.AbstractActor;
import akka.actor.Props;
import datatypes.TransactionDetails;
import datatypes.ValidOrder;

public class BuyActor extends AbstractActor
{
	@Override
	public void preStart()
	{
		//Initialise the resources to be used by actor e.g. db
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
		
		// Update values on database and return message and timestamp.
		
		TransactionDetails transactionDetails = new TransactionDetails("Successful Transaction", new Date());
		transactionDetails.setControllerRef( message.getControllerRef() );
		
		return transactionDetails;
	}
	
	@Override
	public void postStop()
	{
		// free up the resources held by actor e.g. db connection
	}
}
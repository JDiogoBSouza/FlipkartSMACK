package actors.specific;

import java.util.HashMap;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class PopuleStoreActor extends AbstractActor
{
	@Override
	public void preStart()
	{
		//Initialise the resources to be used by actor e.g. db
	}
	
	public static Props getProps()
	{
	        return Props.create(PopuleStoreActor.class);
	}
	
	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
            .match(String.class,  
            		message -> { sender().tell( handleMessage(message), self());
            })
            .build();
	}
	
	private HashMap<Integer,Integer> handleMessage(String message)
	{
		HashMap<Integer,Integer> products = new HashMap<Integer,Integer>();

		// Get all types and quantities of products on database and return to render on index page.
		
		return products;
	}
	
	@Override
	public void postStop()
	{
		// free up the resources held by actor e.g. db connection
	}
}
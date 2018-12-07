package actors.basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Singleton;

import actors.specific.BuyActor;
import actors.specific.CheckReserveActor;
import actors.specific.MapperActor;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import datatypes.MapOrder;
import datatypes.RequestOrder;
import datatypes.SparkMessage;
import datatypes.InvalidOrder;
import datatypes.TransactionDetails;
import datatypes.ValidOrder;

@Singleton
public class MasterFlipkartActor extends AbstractActor
{
	private final ActorRef mapperActor;
	private final ActorRef checkReserveActor;
	private final ActorRef buyActor;
	private final ActorRef sparkActor;
	private ActorRef firstRef;
	private int count;
	
	public MasterFlipkartActor()
	{
		mapperActor 		=	getContext().actorOf( new RoundRobinPool(100).props(Props.create(MapperActor.class)), "mapper");
		checkReserveActor 	= 	getContext().actorOf( new RoundRobinPool(100).props(Props.create(CheckReserveActor.class)), "checkreserve");
		buyActor 			=	getContext().actorOf( new RoundRobinPool(100).props(Props.create(BuyActor.class)), "buy");	
		sparkActor 			=	getContext().actorOf( new RoundRobinPool(100).props(Props.create(SparkActor.class)), "spark");
		count = 0;
	}
	
	public static Props getProps()
	{
	        return Props.create(MasterFlipkartActor.class, MasterFlipkartActor::new);
	}
	
	@Override
	public void preStart()
	{
		//Initialise the resources to be used by actor e.g. db
		System.out.println("First Ref: " + firstRef);
		System.out.println("Mapper: " + mapperActor);
		System.out.println("Check: " + checkReserveActor);
		System.out.println("Buy: " + buyActor);
		System.out.println("Spark: " + sparkActor);
	}
	
	@Override
	public Receive createReceive()
	{						
		return receiveBuilder()
				.match(RequestOrder.class, message -> { mapperActor.tell( handleMessage(message), self() );
                })
				.match(MapOrder.class, message -> { sparkActor.tell( handleMessage(message), self() );
                })
				.match(SparkMessage.class, message -> { checkReserveActor.tell( handleMessage(message), self() );
                })
                .match(ValidOrder.class, message -> { buyActor.tell( handleMessage(message), self() );
                })
                .match(InvalidOrder.class, message -> { message.getControllerRef().tell( handleMessage(message), self() );
                })
                .match(TransactionDetails.class, message -> { message.getControllerRef().tell( handleMessage(message), self() );
                })
                .build();
	}
	
	private RequestOrder handleMessage(RequestOrder message)
	{			
		//System.out.println("JSON Received by MasterActor");
	
		message.setControllerRef( getSender() );
		
		return message;
	}
	
	private SparkMessage handleMessage(SparkMessage message)
	{			
		//System.out.println("SparkMessage Received by MasterActor");
		
		return message;
	}
	
	private MapOrder handleMessage(MapOrder message)
	{			
		//System.out.println("MapOrder Received by MasterActor");
		
		return message;
	}
	
	private ValidOrder handleMessage(ValidOrder message)
	{
		//System.out.println("ValidOrder Received by MasterActor");
		
		return message;
	}
	
	private String handleMessage(InvalidOrder message)
	{
		//System.out.println("InvalidOrder Received by MasterActor");

		System.out.print("InvalidOrder: "); //+ result.toString() );
		System.out.println( message.getMessage() );
		
		return message.getMessage();
	}
	
	private String handleMessage(TransactionDetails message)
	{
		//System.out.print("TransactionDetails "); //+ result.toString() );
		//System.out.println( message.getMessage() );

		count++;
		System.out.println("ENDS Count: " + count);
		System.out.println("Enviando Final para: " + message.getControllerRef() );
		
		return message.getMessage();
	}
	
	@Override
	public void postStop()
	{
		// free up the resources held by actor e.g. db connection
	}
}

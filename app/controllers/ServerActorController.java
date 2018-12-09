package controllers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import actors.basic.MasterFlipkartActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import datatypes.RequestOrder;
import play.mvc.*;
import scala.compat.java8.FutureConverters;

import static akka.pattern.Patterns.ask;

@Singleton
public class ServerActorController extends Controller 
{
    final ActorRef flipkartActor;
    
    @Inject public ServerActorController(ActorSystem system) 
    {
    	flipkartActor  = system.actorOf(MasterFlipkartActor.getProps(), "masterFlipkartActor");
    }

    public CompletionStage<Result> buy()
    {
    	JsonNode json = request().body().asJson();
		
	    if(json == null)
	    {
	    	return CompletableFuture.completedFuture( badRequest("Expecting Json data") );
	    }
	    else
	    { 
	    	RequestOrder requestOrder = new RequestOrder(json);
	    	
	        return FutureConverters.toJava( ask(flipkartActor, requestOrder, 60000) ).thenApply(response -> response.toString().contains("Successful Transaction") ? ok((String) response) : Results.badRequest((String) response));
	    }
	    
    }
}

package controllers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import actors.basic.MasterFlipkartActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import datatypes.ProductSearch;
import datatypes.ProductSearchGet;
import datatypes.RequestOrder;
import play.libs.Json;
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
    
    public CompletionStage<Result> search()
    {
    	JsonNode json = request().body().asJson();
		
	    if(json == null)
	    {
	    	return CompletableFuture.completedFuture( badRequest("Expecting Json data") );
	    }
	    else
	    { 
	    	ProductSearch productSearch = new ProductSearch(json);
	    	
	        return FutureConverters.toJava( ask(flipkartActor, productSearch, 60000) ).thenApply(response ->  ok( Json.toJson(response) ));
	    } 
    }
    
    public CompletionStage<Result> searchGet(Long type)
    {
    	
    	ProductSearchGet search = new ProductSearchGet( type.intValue() );
    	
    	return FutureConverters.toJava( ask(flipkartActor, search, 60000) ).thenApply(response ->  ok( Json.toJson(response) ));
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

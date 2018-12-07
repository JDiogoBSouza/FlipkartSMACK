package datatypes;

import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

import models.Order;
import play.libs.Json;

public class TransactionDetails extends ControllerReference
{
	private String message;
	private Date timeStamp;
	private ArrayList<Order> orders;
	
	public TransactionDetails(String message, Date timeStamp, ArrayList<Order> orders)
	{
		this.message = message;
		this.timeStamp = timeStamp;
		this.orders = orders;
	}
	
	public TransactionDetails(String message, Date timeStamp)
	{
		this.message = message;
		this.timeStamp = timeStamp;
		this.orders = new ArrayList<Order>();
	}
	
	public TransactionDetails()
	{
		this.message = "";
		this.timeStamp = new Date();
		this.orders = new ArrayList<Order>();
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Date getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public ArrayList<Order> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<Order> orders) {
		this.orders = orders;
	}

	public JsonNode getJSON()
	{
		return Json.toJson(this);
	}
}

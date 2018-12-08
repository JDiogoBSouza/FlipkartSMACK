package datatypes;

import java.util.ArrayList;
import java.util.List;

import models.Order;

public class SparkMessage extends ControllerReference
{
	private List<Order> result;
	
	private String message;

	public SparkMessage()
	{
		result = new ArrayList<Order>();
	}
	
	public SparkMessage(String message)
	{
		this.message = message;
		result = new ArrayList<Order>();
	}
		
	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Order> getResult() {
		return result;
	}

	public void setResult(List<Order> result) {
		this.result = result;
	}
}

package datatypes;

import java.util.HashMap;
import models.Kart;
import models.Product;

public class SparkMessage extends ControllerReference
{
	private Kart kart;
	private HashMap<Integer, Product> result;
	
	private String message;

	public SparkMessage()
	{
		result = new HashMap<Integer, Product>();
	}
	
	public SparkMessage(String message)
	{
		this.message = message;
		result = new HashMap<Integer, Product>();
	}
		
	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Kart getKart() {
		return kart;
	}

	public void setKart(Kart kart) {
		this.kart = kart;
	}

	public HashMap<Integer, Product> getResult() {
		return result;
	}

	public void setResult(HashMap<Integer, Product> result) {
		this.result = result;
	}
}

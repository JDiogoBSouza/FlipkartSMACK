package models;

import java.io.Serializable;

public class Product implements Serializable, Comparable<Product>
{
	private int product_id;
	private String name;
	private int type;
	private double price;
	private int quantity;

	public Product()
	{
		this.name = "";
		this.type = -1;
		this.price = 0.0;
		this.quantity = 0;
	}
	
	public Product(int type)
	{
		this.name = "";
		this.type = type;
		this.price = 0.0;
		this.quantity = 0;
	}
	
	public Product(String name, int type, double price, int quantity)
	{
		this.name = name;
		this.type = type;
		this.price = price;
		this.quantity = quantity;
	}
	
	public Product(int product_id, String name, int type, double price, int quantity)
	{
		this.product_id = product_id;
		this.name = name;
		this.type = type;
		this.price = price;
		this.quantity = quantity;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public int compareTo(Product o)
	{
		if( this.price < o.getPrice() )
		{
			return -1;
		}
		else if( this.price > o.getPrice() )
		{
			return 1;
		}
		
		return 0;
	}
}

package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Kart implements Serializable
{
	List<Order> orders;
	
	public Kart()
	{
		this.orders = new ArrayList<Order>();
	}
	
	public Kart(List<Order> orders)
	{
		this.orders = orders;
	}
	
	public boolean isInKart( Product product )
	{		
		for( Order o : orders )
		{
			if( o.getProduct().getType() == product.getType() )
				return true;
		}
		
		return false;
	}
	
	public void addOrder(Order order)
	{
		orders.add(order);
	}
	
	public void addOrder( int type, int quantity )
	{
		Product product = new Product(type);
		Order order = new Order(product, quantity);

		orders.add(order);
	}
	
	public boolean removeProduct(int type)
	{
		boolean result = false;
		
		for( Order order : orders )
		{
			if( order.getProduct().getType() == type )
			{
				result = orders.remove(order);
			}
		}
		
		return result;
	}
	
	public boolean isEmpty()
	{
		return orders.isEmpty();
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
}

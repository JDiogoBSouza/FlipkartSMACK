package datatypes;

import java.util.ArrayList;
import java.util.List;

import models.Order;
import models.Product;

public class MapOrder extends ControllerReference
{
	List<Order> orders;
	
	public MapOrder()
	{
		// Verificar publicação segura ?
		orders = new ArrayList<Order>();
	}
	
	public MapOrder(List<Order> orders)
	{
		// Verificar publicação segura ?
		this.orders = orders;
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
	
	public List<Order> getArrayList()
	{
		return orders;
	}
	
	public void setArrayList(List<Order> orders)
	{
		this.orders = orders;
	}
}

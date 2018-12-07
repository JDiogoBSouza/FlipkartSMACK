package datatypes;

import java.util.ArrayList;
import models.Order;

public class ValidOrder extends AbstractOrder
{
	ArrayList<Order> orders;
	
	public ValidOrder()
	{
		// Verificar publicação segura ?
		orders = new ArrayList<Order>();
	}

	public void addOrder(Order order)
	{
		orders.add(order);
	}

	public void addOrders(ArrayList<Order> orders)
	{
		for( Order order : orders )
		{
			this.orders.add(order);
		}
	}
	
	public boolean removeProduct(int key)
	{
		boolean result = false;
		
		for( Order order : orders )
		{
			if( order.getProduct().getId() == key )
			{
				result = orders.remove(order);
			}
		}
		
		return result;
	}
	
	public ArrayList<Order> getArrayList()
	{
		return orders;
	}
}

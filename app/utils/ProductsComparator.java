package utils;

import java.io.Serializable;
import java.util.Comparator;

import models.Product;

public class ProductsComparator implements Comparator<Product>, Serializable
{
	@Override
	public int compare(Product o1, Product o2)
	{
		return o1.compareTo( o2 );
	}

}

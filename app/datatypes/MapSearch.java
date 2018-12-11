package datatypes;

import models.Product;

public class MapSearch extends ControllerReference 
{
	private Product product;

	public MapSearch(Product product) {
		this.product = product;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}

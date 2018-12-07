package models;

public class Order
{
	Product product;
	int quantity;
	
	public Order()
	{
		product = new Product();
		quantity = 0;
	}

	public Order(Product product, int quantity)
	{
		this.product = product;
		this.quantity = quantity;
	}
	
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}	
}

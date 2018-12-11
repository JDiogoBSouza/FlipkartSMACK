package datatypes;

import java.io.Serializable;

public class ProductSearchGet extends ControllerReference implements Serializable
{
	private int type;

	public ProductSearchGet(int type) {
		super();
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}

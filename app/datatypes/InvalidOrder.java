package datatypes;

public class InvalidOrder extends AbstractOrder
{
	private String message;

	public InvalidOrder(String message)
	{
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

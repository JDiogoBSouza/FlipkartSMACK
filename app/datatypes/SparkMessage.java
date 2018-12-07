package datatypes;

public class SparkMessage extends ControllerReference
{
	private String message;

	public SparkMessage(String message)
	{
		this.message = message;
	}
		
	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

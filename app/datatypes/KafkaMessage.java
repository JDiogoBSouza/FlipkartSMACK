package datatypes;

public class KafkaMessage extends ControllerReference
{
	private String message;

	public KafkaMessage(String message)
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

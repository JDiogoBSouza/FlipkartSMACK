package datatypes;

import com.fasterxml.jackson.databind.JsonNode;

public class RequestOrder extends ControllerReference
{
	private JsonNode jsonNode;
	
	public RequestOrder(JsonNode jsonNode)
	{
		this.jsonNode = jsonNode;
	}

	public JsonNode getJsonNode() {
		return jsonNode;
	}

	public void setJsonNode(JsonNode jsonNode) {
		this.jsonNode = jsonNode;
	}
}

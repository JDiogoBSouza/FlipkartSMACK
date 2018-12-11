package datatypes;

import com.fasterxml.jackson.databind.JsonNode;

import models.Product;

public class ProductSearch extends ControllerReference
{
	private JsonNode jsonNode;
	
	public ProductSearch(JsonNode jsonNode)
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

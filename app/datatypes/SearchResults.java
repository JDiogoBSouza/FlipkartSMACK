package datatypes;

import com.fasterxml.jackson.databind.JsonNode;

public class SearchResults extends ControllerReference
{	
	private JsonNode jsonNode;
	
	
	public SearchResults()
	{
		this.jsonNode = null;
	}
	
	public SearchResults(JsonNode jsonNode)
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

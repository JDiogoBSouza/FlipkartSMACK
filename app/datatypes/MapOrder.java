package datatypes;

import models.Kart;

public class MapOrder extends ControllerReference
{
	private Kart kart;
	
	public MapOrder()
	{
		// Verificar publicação segura ?
		this.kart = new Kart();
	}
	
	public MapOrder(Kart kart)
	{
		// Verificar publicação segura ?
		this.kart = kart;
	}

	public Kart getKart() {
		return kart;
	}

	public void setKart(Kart kart) {
		this.kart = kart;
	}
}

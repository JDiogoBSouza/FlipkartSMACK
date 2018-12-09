package datatypes;

import java.util.ArrayList;

import models.Kart;
import models.Order;

public class ValidOrder extends AbstractOrder
{
	private Kart kart;
	
	public ValidOrder()
	{
		// Verificar publicação segura ?
		kart = new Kart();
	}

	public Kart getKart() {
		return kart;
	}

	public void setKart(Kart kart) {
		this.kart = kart;
	}
}

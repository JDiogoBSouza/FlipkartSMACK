package datatypes;

import akka.actor.ActorRef;

public class ControllerReference 
{
	private ActorRef controllerRef;
	
	public ActorRef getControllerRef() {
		return controllerRef;
	}

	public void setControllerRef(ActorRef controllerRef) {
		this.controllerRef = controllerRef;
	}
}

package datatypes;

import models.Product;
import scala.concurrent.Future;
import akka.japi.Option;

public interface ISearchActor
{
	public Option searchProductBloq(Product produto, int quantidade);
	public Future searchProductNonBloq(Product produto, int quantidade);
}
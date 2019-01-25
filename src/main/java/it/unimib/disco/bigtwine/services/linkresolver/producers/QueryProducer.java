package it.unimib.disco.bigtwine.services.linkresolver.producers;

import it.unimib.disco.bigtwine.commons.models.Link;
import it.unimib.disco.bigtwine.services.linkresolver.QueryType;

public interface QueryProducer {
    QueryType getQueryType();
    String buildQuery(Link link);
}

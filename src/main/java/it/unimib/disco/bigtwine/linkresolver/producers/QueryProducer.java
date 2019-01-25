package it.unimib.disco.bigtwine.linkresolver.producers;

import it.unimib.disco.bigtwine.linkresolver.QueryType;

public interface QueryProducer {
    QueryType getQueryType();
    String buildQuery(String url);
}

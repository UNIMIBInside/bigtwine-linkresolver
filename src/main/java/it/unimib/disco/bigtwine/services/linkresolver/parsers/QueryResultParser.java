package it.unimib.disco.bigtwine.services.linkresolver.parsers;

import it.unimib.disco.bigtwine.commons.models.Resource;
import it.unimib.disco.bigtwine.services.linkresolver.QueryType;

public interface QueryResultParser {
    QueryType getQueryType();
    Resource parse();
}

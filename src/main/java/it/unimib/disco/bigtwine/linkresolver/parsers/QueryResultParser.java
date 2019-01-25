package it.unimib.disco.bigtwine.linkresolver.parsers;

import it.unimib.disco.bigtwine.commons.models.Resource;
import it.unimib.disco.bigtwine.linkresolver.QueryType;

public interface QueryResultParser {
    QueryType getQueryType();
    Resource parse();
}

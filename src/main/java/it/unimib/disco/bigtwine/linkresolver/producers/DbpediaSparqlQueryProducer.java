package it.unimib.disco.bigtwine.linkresolver.producers;

import it.unimib.disco.bigtwine.linkresolver.QueryType;

public final class DbpediaSparqlQueryProducer implements SparqlQueryProducer {

    @Override
    public QueryType getQueryType() {
        return QueryType.sparql;
    }

    @Override
    public String buildQuery(String url) {
        String szQuery =
            "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                "PREFIX dbp: <http://dbpedia.org/property/> \n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" +
                "SELECT ?uri ?name ?name_f ?name_w ?thumb ?abstract ?wiki_id ?lat ?lng ?uid\n" +
                "WHERE {\n" +
                "    VALUES ?uid {'{RESOURCE_URI}'}\n" +
                "    OPTIONAL {?uri dbo:wikiPageID ?wiki_id.}\n" +
                "    OPTIONAL {?uri rdfs:label ?name . FILTER(LANG(?name)='en')}\n" +
                "    OPTIONAL {?uri dbp:name ?name_w . FILTER(LANG(?name_w)='en')}\n" +
                "    OPTIONAL {?uri foaf:name ?name_f . FILTER(LANG(?name_f)='en')}\n" +
                "    OPTIONAL {?uri dbo:abstract ?abstract . FILTER(LANG(?abstract)='en') }\n" +
                "    OPTIONAL {?uri dbo:thumbnail ?thumb.}\n" +
                "    OPTIONAL {?uri wgs:lat ?lat.}\n" +
                "    OPTIONAL {?uri wgs:long ?lng.}\n" +
                "    FILTER (?uri = <{RESOURCE_URI}>)\n" +
                "}\n" +
                "LIMIT 1";

        return szQuery.replace("{RESOURCE_URI}", url);
    }
}

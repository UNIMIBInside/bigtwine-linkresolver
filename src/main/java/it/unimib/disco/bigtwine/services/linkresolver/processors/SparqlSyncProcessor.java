package it.unimib.disco.bigtwine.services.linkresolver.processors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.services.linkresolver.domain.ExtraField;
import it.unimib.disco.bigtwine.services.linkresolver.domain.Link;
import it.unimib.disco.bigtwine.services.linkresolver.domain.Resource;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.linkresolver.QueryType;
import it.unimib.disco.bigtwine.services.linkresolver.executors.SparqlSyncExecutor;
import it.unimib.disco.bigtwine.services.linkresolver.parsers.SparqlQueryResultParser;
import it.unimib.disco.bigtwine.services.linkresolver.producers.SparqlQueryProducer;
import org.apache.jena.query.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


@QueryBasedProcessor(supportedQueryTypes = { QueryType.sparql })
public abstract class SparqlSyncProcessor implements Processor {

    private final Logger log = LoggerFactory.getLogger(SparqlSyncProcessor.class);

    protected SparqlQueryProducer queryProducer;
    protected SparqlQueryResultParser resultParser;
    protected SparqlSyncExecutor executor;
    protected ProcessorListener<Resource> listener;

    public SparqlSyncProcessor(SparqlQueryProducer queryProducer, SparqlQueryResultParser resultParser, SparqlSyncExecutor executor) {
        this.queryProducer = queryProducer;
        this.resultParser = resultParser;
        this.executor = executor;
    }

    @Override
    public void setExecutor(Executor executor) {
        if (!(executor instanceof SparqlSyncExecutor)) {
            throw new IllegalArgumentException("Invalid executor type, SparqlSyncExecutor needed");
        }

        this.executor = (SparqlSyncExecutor)executor;
    }

    @Override
    public Executor getExecutor() {
        return this.executor;
    }

    public SparqlSyncExecutor getSparqlSyncExecutor() {
        return this.executor;
    }

    @Override
    public void setListener(ProcessorListener<Resource> listener) {
        this.listener = listener;
    }

    @Override
    public boolean configureProcessor() {
        return true;
    }

    @Override
    public boolean process(String tag, Link item, ExtraField[] extraFields) {
        return this.process(tag, new Link[] {item}, extraFields);
    }

    @Override
    public boolean process(String tag, Link[] items, ExtraField[] extraFields) {
        List<Resource> resources = new ArrayList<>();

        for (Link item : items) {
            log.debug("Starting to resolve link: {}", item.getUrl());
            String query = this.queryProducer.buildQuery(item, extraFields);
            ResultSet resultSet = this.executor.query(query);
            log.debug("Link {} resolved", resultSet);
            if (resultSet != null) {
                Resource resource = this.resultParser.parse(resultSet, extraFields);

                if (resource != null) {
                    resources.add(resource);
                    log.debug("Resource found for link {} {}", item.getUrl(), resource.getName());
                }
            }
        }

        if (this.listener != null) {
            this.listener.onProcessed(this, tag, resources.toArray(new Resource[0]));
        }

        return items.length == 0 || resources.size() > 0;
    }
    @Override
    public boolean process(String tag, Link item) {
        return this.process(tag, item, null);
    }

    @Override
    public boolean process(String tag, Link[] items) {
        return this.process(tag, items, null);
    }

}

package it.unimib.disco.bigtwine.services.linkresolver.processors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.commons.models.Resource;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.linkresolver.QueryType;
import it.unimib.disco.bigtwine.services.linkresolver.executors.SparqlSyncExecutor;
import it.unimib.disco.bigtwine.services.linkresolver.parsers.SparqlQueryResultParser;
import it.unimib.disco.bigtwine.services.linkresolver.producers.SparqlQueryProducer;
import org.apache.jena.query.ResultSet;

import java.util.ArrayList;
import java.util.List;


@QueryBasedProcessor(supportedQueryTypes = { QueryType.sparql })
public abstract class SparqlSyncProcessor implements Processor {

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
    public boolean process(String tag, String item) {
        return this.process(tag, new String[] {item});
    }

    @Override
    public boolean process(String tag, String[] items) {
        List<Resource> resources = new ArrayList<>();

        for (String item : items) {
            String query = this.queryProducer.buildQuery(item);
            ResultSet resultSet = this.executor.query(query);
            if (resultSet != null) {
                Resource resource = this.resultParser.parse(resultSet);
                resources.add(resource);
            }
        }

        if (this.listener != null && resources.size() > 0) {
            this.listener.onProcessed(this, tag, resources.toArray(new Resource[0]));
        }

        return items.length == 0 || resources.size() > 0;
    }
}

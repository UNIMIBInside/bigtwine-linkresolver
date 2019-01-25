package it.unimib.disco.bigtwine.services.linkresolver.config;

import it.unimib.disco.bigtwine.services.linkresolver.executors.ExecutorFactory;
import it.unimib.disco.bigtwine.services.linkresolver.parsers.QueryResultParserFactory;
import it.unimib.disco.bigtwine.services.linkresolver.processors.ProcessorFactory;
import it.unimib.disco.bigtwine.services.linkresolver.producers.QueryProducerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LinkResolverConfiguration {


    @Bean
    public ExecutorFactory getExecutorFactory() {
        return new ExecutorFactory();
    }

    @Bean
    public QueryProducerFactory getQueryProducerFactory() {
        return new QueryProducerFactory();
    }

    @Bean
    public QueryResultParserFactory getQueryResultParserFactory() {
        return new QueryResultParserFactory();
    }

    @Bean
    public ProcessorFactory getProcessorFactory() {
        return new ProcessorFactory(
            this.getQueryProducerFactory(),
            this.getExecutorFactory(),
            this.getQueryResultParserFactory());
    }
}

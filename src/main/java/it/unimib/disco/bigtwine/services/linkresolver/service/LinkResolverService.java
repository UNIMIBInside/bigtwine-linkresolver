package it.unimib.disco.bigtwine.services.linkresolver.service;

import it.unimib.disco.bigtwine.commons.messaging.LinkResolverRequestMessage;
import it.unimib.disco.bigtwine.commons.messaging.LinkResolverResponseMessage;
import it.unimib.disco.bigtwine.commons.models.Resource;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.linkresolver.LinkType;
import it.unimib.disco.bigtwine.services.linkresolver.processors.Processor;
import it.unimib.disco.bigtwine.services.linkresolver.processors.ProcessorFactory;
import it.unimib.disco.bigtwine.services.linkresolver.messaging.LinkResolverRequestsConsumerChannel;
import it.unimib.disco.bigtwine.services.linkresolver.messaging.LinkResolverResponsesProducerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LinkResolverService implements ProcessorListener<Resource> {

    private final Logger log = LoggerFactory.getLogger(LinkResolverService.class);
    private MessageChannel channel;
    private ProcessorFactory processorFactory;
    private Map<LinkType, Processor> processors = new HashMap<>();

    public LinkResolverService(
        LinkResolverResponsesProducerChannel channel,
        ProcessorFactory processorFactory) {
        this.channel = channel.linkResolverResponsesChannel();
        this.processorFactory = processorFactory;
    }

    private Processor getProcessor(LinkType linkType) {
        Processor processor;
        if (this.processors.containsKey(linkType)) {
            processor = this.processors.get(linkType);
        }else {
            try {
                processor = this.processorFactory.getProcessor(linkType);
            } catch (Exception e) {
                System.err.println("Cannot create processor");
                log.error("Cannot create processor");
                return null;
            }
            processor.setListener(this);
            boolean processorReady = processor.configureProcessor();
            if (processorReady) {
                this.processors.put(linkType, processor);
            }else {
                System.err.println("Processor not ready: " + linkType.toString());
                log.error("Processor not ready: " + linkType.toString());
                return null;
            }
        }

        log.info("Processor ready: " + processor.getClass().toString());

        return processor;
    }

    private void processResolveRequest(LinkResolverRequestMessage request) {
        for (String url : request.getLinks()) {
            LinkType linkType = LinkType.getTypeOfLink(url);

            if (linkType == null) {
                return;
            }

            Processor processor = this.getProcessor(linkType);

            if (processor == null) {
                return;
            }

            processor.process(request.getRequestId(), url);
        }
    }

    private void sendResponse(Processor processor, String tag, Resource[] resources) {
        // for (LinkedTweet tweet : tweets) {
        //      System.out.println("Linked tweet: " + tweet.getId());
        // }
        LinkResolverResponseMessage response = new LinkResolverResponseMessage();
        response.setResources(resources);
        response.setRequestId(tag);
        Message<LinkResolverResponseMessage> message = MessageBuilder
            .withPayload(response)
            // .setHeader(KafkaHeaders.TOPIC, "linkresolver-responses-test")
            .build();
        this.channel.send(message);
        log.info("Request Processed: {}.", tag);
    }

    @StreamListener(LinkResolverRequestsConsumerChannel.CHANNEL)
    public void onNewDecodeRequest(LinkResolverRequestMessage request) {
        log.info("Request Received: {}.", request.getRequestId());
        this.processResolveRequest(request);
    }

    @Override
    public void onProcessed(GenericProcessor processor, String tag, Resource[] processedItems) {
        if (!(processor instanceof Processor)) {
            throw new AssertionError("Invalid processor type");
        }

        this.sendResponse((Processor)processor, tag, processedItems);
    }
}

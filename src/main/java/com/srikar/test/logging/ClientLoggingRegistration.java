package com.srikar.test.logging;

import javax.inject.Singleton;

import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.http.netty.channel.ChannelPipelineCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.netty.LogbookClientHandler;

@Singleton
public class ClientLoggingRegistration implements BeanCreatedEventListener<ChannelPipelineCustomizer> {

    private static final Logger LOG = LoggerFactory.getLogger(ClientLoggingRegistration.class);

    private final Logbook logbook = Logbook.builder()
            .sink(new CustomLoggingSink("httpclient"))
            .build();

    @Override
    public ChannelPipelineCustomizer onCreated(BeanCreatedEvent<ChannelPipelineCustomizer> event) {
        var customizer = event.getBean();
        if (customizer.isClientChannel()) {
            customizer.doOnConnect(pipeline -> {
                return pipeline.addAfter(ChannelPipelineCustomizer.HANDLER_HTTP_CLIENT_CODEC, "logbook", new LogbookClientHandler(logbook));
            });
        }
        return customizer;
    }
}

package com.jn.agileway.eimessage.core.endpoint.pubsub;


import com.jn.agileway.eimessage.core.Message;
import com.jn.agileway.eimessage.core.channel.OutboundChannel;
import com.jn.agileway.eimessage.core.endpoint.mapper.MessageMapper;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;

public class DefaultMessageProducer<T> extends AbstractMessagePubSubEndpoint<T> implements MessageProducer<T> {
    private OutboundChannel outboundChannel;
    private Logger logger = Loggers.getLogger(getClass());

    @Override
    protected void doStart() {
        outboundChannel.startup();
    }

    @Override
    protected void doStop() {
        outboundChannel.shutdown();
    }

    public boolean send(T obj) {
        Preconditions.checkNotNull(obj);
        MessageMapper<T> messageMapper = getMessageMapper();
        Message<?> message = null;
        if (messageMapper != null) {
            message = messageMapper.map(obj);
        }
        if (message == null) {
            logger.warn("unsupported class: " + Reflects.getFQNClassName(obj.getClass()));
        }
        return outboundChannel.send(message);
    }

    @Override
    public OutboundChannel getOutboundChannel() {
        return this.outboundChannel;
    }

    @Override
    public void setOutboundChannel(OutboundChannel channel) {
        this.outboundChannel = channel;
    }
}

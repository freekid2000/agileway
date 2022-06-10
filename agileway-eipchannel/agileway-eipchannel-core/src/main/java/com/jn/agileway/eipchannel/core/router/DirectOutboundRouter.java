package com.jn.agileway.eipchannel.core.router;

import com.jn.agileway.eipchannel.core.channel.OutboundChannel;
import com.jn.agileway.eipchannel.core.message.Message;
import com.jn.langx.util.collection.Collects;

import java.util.Collection;
import java.util.List;

public class DirectOutboundRouter extends AbstractMessageRouter {
    public DirectOutboundRouter(OutboundChannel defaultOutboundChannel) {
        setDefaultOutputChannel(defaultOutboundChannel);
    }

    public DirectOutboundRouter() {

    }

    @Override
    protected boolean determineSentToDefaultOutputChannel(OutboundChannel defaultOutboundChannel, Collection<OutboundChannel> branchesOutboundChannels) {
        return true;
    }

    @Override
    protected List<Object> getChannelIdentifiers(Message<?> message) {
        return Collects.<Object>asList(getDefaultOutputChannel());
    }
}

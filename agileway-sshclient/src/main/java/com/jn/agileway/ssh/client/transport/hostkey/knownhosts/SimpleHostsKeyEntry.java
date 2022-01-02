package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.HostKeyType;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;

import java.util.Set;

public class SimpleHostsKeyEntry extends AbstractHostsKeyEntry {
    private transient Set<String> hostList = null;

    public SimpleHostsKeyEntry() {
    }

    public SimpleHostsKeyEntry(String hosts, HostKeyType keyType, Object publicKey) {
        this(null, hosts, keyType, publicKey);
    }

    public SimpleHostsKeyEntry(Marker marker, String hosts, HostKeyType keyType, Object publicKey) {
        super(marker, hosts, keyType, publicKey);
    }

    @Override
    public void setHosts(String hosts) {
        if (this.hostList == null) {
            this.hostList = Collects.emptyHashSet();
        }
        super.setHosts(hosts);
        if (hosts != null) {
            this.hostList.addAll(Collects.asSet(Strings.split(hosts, ",")));
        }
    }

    @Override
    protected boolean containsHost(String host) {
        if (this.hostList == null) {
            return false;
        }
        return hostList.contains(host);
    }
}

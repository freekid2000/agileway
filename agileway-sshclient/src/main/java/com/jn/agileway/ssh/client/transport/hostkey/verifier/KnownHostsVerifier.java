package com.jn.agileway.ssh.client.transport.hostkey.verifier;

import com.jn.agileway.ssh.client.transport.hostkey.HostKeyType;
import com.jn.agileway.ssh.client.transport.hostkey.HostsKeyRepository;
import com.jn.agileway.ssh.client.transport.hostkey.StrictHostKeyChecking;
import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.HostsKeyEntry;
import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.SimpleHostsKeyEntry;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.IOException;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

public class KnownHostsVerifier implements HostKeyVerifier {
    private static final Logger logger = Loggers.getLogger(KnownHostsVerifier.class);
    private StrictHostKeyChecking strictHostKeyChecking;
    private HostsKeyRepository repository;

    public KnownHostsVerifier(HostsKeyRepository repository, StrictHostKeyChecking strictHostKeyChecking) {
        this.repository = repository;
        this.strictHostKeyChecking = strictHostKeyChecking;
    }

    protected String getKeyType(PublicKey publicKey) {
        if (publicKey instanceof RSAPublicKey) {
            return HostKeyType.SSH_RSA.getName();
        }
        if (publicKey instanceof DSAPublicKey) {
            return HostKeyType.SSH_DSS.getName();
        }
        if ("ECDSA".equals(publicKey.getAlgorithm())) {
            // 此时可能有多种，按 nistp256 曲线来
            return "ecdsa-sha2-nistp256";
        }
        return null;
    }

    @Override
    public boolean verify(@NonNull String hostname, int port, @Nullable String serverHostKeyAlgorithm, @NonNull Object publicKey) {
        Preconditions.checkNotNull(hostname);
        Preconditions.checkNotNull(publicKey);

        if (serverHostKeyAlgorithm == null && publicKey instanceof PublicKey) {
            serverHostKeyAlgorithm = getKeyType((PublicKey) publicKey);
        }
        if (Strings.isEmpty(serverHostKeyAlgorithm)) {
            return false;
        }

        final String adjustedHostname = (port != 22 && port > 0) ? ("[" + hostname + "]:" + port) : hostname;

        List<HostsKeyEntry> entries = repository.find(adjustedHostname, serverHostKeyAlgorithm);

        if (Objs.isNotEmpty(entries)) {
            HostsKeyEntry e = entries.get(0);
            try {
                if (!e.verify(publicKey)) {
                    return hostKeyChanged(e, adjustedHostname, publicKey);
                }
                return true;
            } catch (IOException ioe) {
                logger.error("Error with {}: {}", e, ioe);
                return false;
            }
        }

        return unknownHostKey(adjustedHostname, serverHostKeyAlgorithm, publicKey);
    }

    protected boolean hostKeyChanged(HostsKeyEntry entry, String hostname, Object publicKey) throws IOException {
        Preconditions.checkNotNull(strictHostKeyChecking);
        logger.warn("host key changed: hostname: {}, keyType:{}", hostname, entry.getKeyType());
        if (this.strictHostKeyChecking == StrictHostKeyChecking.NO) {
            synchronized (repository) {
                repository.remove(hostname, entry.getKeyType());
                SimpleHostsKeyEntry newEntry = new SimpleHostsKeyEntry(hostname, entry.getKeyType(), publicKey);
                if (newEntry.isValid()) {
                    repository.add(newEntry);
                    return true;
                }
                return false;
            }
        } else {
            return false;
        }
    }

    protected boolean unknownHostKey(String hostname, String keyType, Object publicKey) {
        Preconditions.checkNotNull(strictHostKeyChecking);
        logger.info("unknown host key: hostname: {}, keyType:{}", hostname, keyType);
        if (this.strictHostKeyChecking == StrictHostKeyChecking.NO) {
            SimpleHostsKeyEntry entry = new SimpleHostsKeyEntry(hostname, keyType, publicKey);
            if (entry.isValid()) {
                synchronized (repository) {
                    repository.add(entry);
                }
                return true;
            }
            return false;
        } else {
            return false;
        }
    }
}

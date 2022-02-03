package com.dsgaur.spring.embedded.demo.tests.config;

import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Feature;
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.distribution.Versions;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Overrides the default configuration to enable journaling.
 * Most of the code is copied from {@link EmbeddedMongoAutoConfiguration}
 *
 * @see EmbeddedMongoAutoConfiguration
 */
@Configuration
@AutoConfigureBefore(EmbeddedMongoAutoConfiguration.class)
@EnableConfigurationProperties({MongoProperties.class, EmbeddedMongoProperties.class})
public class EmbeddedMongoConfigOverride {

    /**
     * Copied as is from {@link EmbeddedMongoAutoConfiguration}
     */
    private static final byte[] IP4_LOOPBACK_ADDRESS = {127, 0, 0, 1};

    /**
     * Copied as is from {@link EmbeddedMongoAutoConfiguration}
     */
    private static final byte[] IP6_LOOPBACK_ADDRESS = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};

    private final MongoProperties properties;

    public EmbeddedMongoConfigOverride(MongoProperties properties) {
        this.properties = properties;
    }

    /**
     * Overrides the default embedded mongo configuration to enable journaling.
     * Actual magic happens here.
     *
     * @return Mongod configuration which is used to set up the embedded mongo server as well as the mongo clients
     * ({@link MongoRepository}, {@link MongoTemplate}, etc.)
     */
    @Bean
    @ConditionalOnMissingBean
    public MongodConfig embeddedMongoConfiguration(EmbeddedMongoProperties embeddedProperties) throws IOException {
        ImmutableMongodConfig.Builder builder = MongodConfig.builder().version(determineVersion(embeddedProperties));
        EmbeddedMongoProperties.Storage storage = embeddedProperties.getStorage();
        if (storage != null) {
            String databaseDir = storage.getDatabaseDir();
            String replSetName = storage.getReplSetName();
            int oplogSize = (storage.getOplogSize() != null) ? (int) storage.getOplogSize().toMegabytes() : 0;
            builder.replication(new Storage(databaseDir, replSetName, oplogSize));

            // This line enables the required journaling. This line is missing from actual spring boot's implementation.
            builder.cmdOptions(MongoCmdOptions.builder().useNoJournal(false).build());
        }
        Integer configuredPort = this.properties.getPort();
        if (configuredPort != null && configuredPort > 0) {
            builder.net(new Net(getHost().getHostAddress(), configuredPort, Network.localhostIsIPv6()));
        } else {
            builder.net(new Net(getHost().getHostAddress(), Network.getFreeServerPort(getHost()),
                    Network.localhostIsIPv6()));
        }
        return builder.build();
    }

    /**
     * Copied as is from {@link EmbeddedMongoAutoConfiguration}
     */
    private IFeatureAwareVersion determineVersion(EmbeddedMongoProperties embeddedProperties) {
        Assert.state(embeddedProperties.getVersion() != null, "Set the spring.mongodb.embedded.version property or "
                + "define your own MongodConfig bean to use embedded MongoDB");
        if (embeddedProperties.getFeatures() == null) {
            for (Version version : Version.values()) {
                if (version.asInDownloadPath().equals(embeddedProperties.getVersion())) {
                    return version;
                }
            }
            return Versions.withFeatures(createEmbeddedMongoVersion(embeddedProperties));
        }
        return Versions.withFeatures(createEmbeddedMongoVersion(embeddedProperties),
                embeddedProperties.getFeatures().toArray(new Feature[0]));
    }

    /**
     * Copied as is from {@link EmbeddedMongoAutoConfiguration}
     */
    private de.flapdoodle.embed.process.distribution.Version.GenericVersion createEmbeddedMongoVersion(EmbeddedMongoProperties embeddedProperties) {
        return de.flapdoodle.embed.process.distribution.Version.of(embeddedProperties.getVersion());
    }

    /**
     * Copied as is from {@link EmbeddedMongoAutoConfiguration}
     */
    private InetAddress getHost() throws UnknownHostException {
        if (this.properties.getHost() == null) {
            return InetAddress.getByAddress(Network.localhostIsIPv6() ? IP6_LOOPBACK_ADDRESS : IP4_LOOPBACK_ADDRESS);
        }
        return InetAddress.getByName(this.properties.getHost());
    }
}

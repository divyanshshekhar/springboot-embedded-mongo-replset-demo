package com.dsgaur.spring.embedded.demo.tests.config;

import com.dsgaur.spring.embedded.demo.config.ApplicationConfig;
import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Configuration
@AutoConfigureBefore(ApplicationConfig.class)
public class UnitTestsConfig {

    private static final Logger logger = LoggerFactory.getLogger(UnitTestsConfig.class);

    @Resource
    private MongoClient mongoClient;

    @PostConstruct
    public void waitForReplicaSetStatusOk() {
        mongoClient
                .getDatabase("admin")
                .runCommand(new Document("replSetInitiate", new Document()));

        await().atMost(10, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> isReplicaSetReady(mongoClient));
    }

    private Boolean isReplicaSetReady(MongoClient mongoClient) {
        final String adminDb = "admin";

        double replSetStatusOk = (double) mongoClient
                .getDatabase(adminDb)
                .runCommand(new Document("replSetGetStatus", 1))
                .get("ok");
        if (replSetStatusOk == 1.0) {
            logger.debug("ReplStatusOK is 1.0");
            boolean currentIsMaster = (boolean) mongoClient
                    .getDatabase(adminDb)
                    .runCommand(new Document("isMaster",
                    1)).get("ismaster");
            if (!currentIsMaster) {
                logger.debug("Replica set is not ready. Waiting for node to become master.");
            } else {
                logger.debug("Replica set is ready. Node is now master.");
            }
            return currentIsMaster;
        } else {
            logger.debug("Replica set is not ready. Waiting for replStatusOK to be 1.0. Currently {}", replSetStatusOk);
            return false;
        }
    }
}

package ru.syntez.integration.router;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ResourceUtils;
import ru.syntez.integration.router.components.JmsProducer;
import javax.jms.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.exit;

/**
 * Main class for console running
 *
 * @author Skyhunter
 * @date 14.01.2021
 */
@SpringBootApplication
public class IntegrationRouterMain {

    private static Logger LOG = LogManager.getLogger(IntegrationRouterMain.class);

    public static void main(String[] args) {

        LOG.info("STARTING THE APPLICATION");

        ConfigurableApplicationContext context = SpringApplication.run(IntegrationRouterMain.class, args);

        ConnectionFactory jmsConnectionFactory = context.getBean(ConnectionFactory.class);
        JmsProducer jmsProducer = context.getBean(JmsProducer.class);
        try {
            Connection jmsamqconn = jmsConnectionFactory.createConnection();
            jmsamqconn.start();
            List<File> xmlFileList = Arrays.asList(
                    ResourceUtils.getFile(IntegrationRouterMain.class.getResource("/xmls/router_doc_1.xml")),
                    ResourceUtils.getFile(IntegrationRouterMain.class.getResource("/xmls/router_doc_2.xml"))
            );
            long startTime = System.currentTimeMillis();
            LOG.info("Starting send: " + startTime);
            for (int i = 0; i < 100; i++) {
                jmsProducer.sendMessage(xmlFileList);
            }
            long finishTime = System.currentTimeMillis();
            LOG.info("Send all: " + finishTime);
            LOG.info("Total time: " + (finishTime - startTime) + " ms.");
            jmsamqconn.stop();
        } catch (Exception e) {
            LOG.error(String.format("Error send files %s", e.getMessage()));
        }
        exit(0);
        LOG.info("APPLICATION FINISHED");
    }

}
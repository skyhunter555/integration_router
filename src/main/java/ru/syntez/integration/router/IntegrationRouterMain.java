package ru.syntez.integration.router;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ResourceUtils;
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
        RabbitTemplate rabbitTemplate = context.getBean(RabbitTemplate.class);
        try {
            List<File> xmlFileList = Arrays.asList(
                ResourceUtils.getFile(IntegrationRouterMain.class.getResource("/xmls/router_doc_1.xml")),
                ResourceUtils.getFile(IntegrationRouterMain.class.getResource("/xmls/router_doc_2.xml"))
            );
            long startTime = System.currentTimeMillis();
            LOG.info("Starting send: " + startTime);
            for (int i = 0; i < 100; i++) {
                xmlFileList.forEach(xmlFile ->
                    rabbitTemplate.convertAndSend("inputqueue", xmlFile)
                );
            }
            long finishTime = System.currentTimeMillis();
            LOG.info("Send all: " + finishTime);
            LOG.info("Total time: " + (finishTime - startTime) + " ms.");
        } catch (Exception e) {
            LOG.error(String.format("Error send files %s", e.getMessage()));
        }
        exit(0);
        LOG.info("APPLICATION FINISHED");
    }

}
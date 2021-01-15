package ru.syntez.integration.router.config;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.router.ExpressionEvaluatingRouter;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.util.ErrorHandler;
import ru.syntez.integration.router.IntegrationRouterMain;
import javax.jms.Session;
import java.util.Arrays;

/**
 * Configuration for spring integration jms
 * 1. Start AMQ BrokerService
 * 2. Created ConnectionFactory
 * 3. Created JmsTemplate
 *
 * @author Skyhunter
 * @date 14.01.2021
 */
@Configuration
@EnableJms
public class JmsConfig {

    private static Logger LOG = LogManager.getLogger(IntegrationRouterMain.class);

    @Value("${activemq.broker.connector}")
    private final String brokerConnector = "tcp://localhost:61616";

    @Value("${jms.input.queue}")
    private final String jmsInputQueue = "jmsComponent:queue:inputqueue";

    @Bean
    public BrokerService brokerService() {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("activemq");
        broker.setPersistent(false);
        broker.setUseShutdownHook(false);
        broker.setUseJmx(false);
        try {
            broker.addConnector(brokerConnector);
            broker.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return broker;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerConnector);
        connectionFactory.setTrustedPackages(Arrays.asList("ru.syntez.integration.router.entities"));
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setDefaultDestinationName(jmsInputQueue);
        jmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        return jmsTemplate;
    }

    @Bean
    public DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setPubSubDomain(false);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("5");
        factory.setErrorHandler(errorHandler());
        return factory;
    }

    private ErrorHandler errorHandler() {
        return throwable -> {
            LOG.error(throwable.getCause().getMessage());
            throwable.printStackTrace();
        };
    }

    @Router(inputChannel = jmsInputQueue)
    @Bean
    public ExpressionEvaluatingRouter router() {
        ExpressionEvaluatingRouter router = new ExpressionEvaluatingRouter("selector");
        router.setChannelMapping("order", "jmsComponent:queue:orderoutputqueue");
        router.setChannelMapping("invoice", "jmsComponent:queue:invoiceoutputqueue");
        return router;
    }
}


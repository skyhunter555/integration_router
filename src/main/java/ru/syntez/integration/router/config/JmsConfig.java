package ru.syntez.integration.router.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import ru.syntez.integration.router.IntegrationRouterMain;
import ru.syntez.integration.router.entities.DocumentTypeEnum;
import ru.syntez.integration.router.entities.RoutingDocument;
import ru.syntez.integration.router.utils.JmsXmlMessageConverter;
import java.nio.charset.Charset;

/**
 * Configuration for spring integration jms
 * 1. Create ConnectionFactory
 * 2. Create queue and binds
 * 3. Create RabbitTemplate
 * 4. Create IntegrationFlow to routing documents
 *
 * @author Skyhunter
 * @date 18.01.2021
 */
@Configuration
@EnableRabbit
public class JmsConfig {

    private static Logger LOG = LogManager.getLogger(IntegrationRouterMain.class);

    @Value("${jms.rabbit-mq.host}")
    private String brokerHost = "localhost";

    @Value("${jms.rabbit-mq.port}")
    private Integer brokerPort = 5672;

    @Value("${jms.rabbit-mq.virtual-host}")
    private String virtualHost = "user";

    @Value("${jms.rabbit-mq.username}")
    private String username = "user";

    @Value("${jms.rabbit-mq.password}")
    private String password = "user";

    @Value("${jms.rabbit-mq.exchange-input-name}")
    private String exchangeInputName = "user";

    @Value("${jms.rabbit-mq.queue-input-name}")
    private String queueInputName = "input";

    @Value("${jms.rabbit-mq.queue-output-order-name}")
    private String queueOutputOrderName = "order";

    @Value("${jms.rabbit-mq.queue-output-invoice-name}")
    private String queueOutputInvoiceName = "invoice";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(brokerHost);
        connectionFactory.setPort(brokerPort);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public DirectExchange directInputExchange() {
        return new DirectExchange(exchangeInputName);
    }

    @Bean
    public Queue inputQueue() {
        return QueueBuilder.durable(queueInputName).build();
    }

    @Bean
    public Queue outputOrderQueue() {
        return QueueBuilder.durable(queueOutputOrderName).build();
    }

    @Bean
    public Queue outputInvoiceQueue() {
        return QueueBuilder.durable(queueOutputInvoiceName).build();
    }

    @Bean
    public Binding bindingInputQueue() {
        return BindingBuilder.bind(inputQueue()).to(directInputExchange()).withQueueName();
    }

    @Bean
    public Binding bindingOutputOrderQueue() {
        return BindingBuilder.bind(outputOrderQueue()).to(directInputExchange()).withQueueName();
    }

    @Bean
    public Binding bindingOutputInvoiceQueue() {
        return BindingBuilder.bind(outputInvoiceQueue()).to(directInputExchange()).withQueueName();
    }

    @Bean
    public MessageConverter converter() {
        return new JmsXmlMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setExchange(exchangeInputName);
        template.setMessageConverter(converter());
        return template;
    }

    @Bean
    public ObjectMapper xmlMapper() {
        JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(false);
        return new XmlMapper(xmlModule);
    }


    @Bean
    public IntegrationFlow inbound() {
        return IntegrationFlows.from(Amqp.inboundAdapter(connectionFactory(), inputQueue()))
                .handle(message -> {
                    final byte[] payload = (byte[]) message.getPayload();
                    final String xmlPayload = new String(payload, Charset.defaultCharset());
                    try {
                        RoutingDocument document = xmlMapper().readValue(xmlPayload, RoutingDocument.class);
                        if (DocumentTypeEnum.order.equals(document.getDocType())) {
                            LOG.info(String.format("******** SELECTED ORDER OUTPUT QUEUE FOR DOCTYPE: %s", document.getDocType()));
                            rabbitTemplate().convertAndSend(queueOutputOrderName, xmlPayload);
                        }
                        if (DocumentTypeEnum.invoice.equals(document.getDocType())) {
                            LOG.info(String.format("******** SELECTED INVOICE OUTPUT QUEUE FOR DOCTYPE: %s", document.getDocType()));
                            rabbitTemplate().convertAndSend(queueOutputInvoiceName, xmlPayload);
                        }
                    } catch (Exception e) {
                        LOG.error(String.format("Error send files %s", e.getMessage()));
                    }
                })
                .get();
    }

}


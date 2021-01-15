package ru.syntez.integration.router.components;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.MessagePostProcessor;
import ru.syntez.integration.router.entities.RoutingDocument;
import ru.syntez.integration.router.utils.JmsXmlMessageConverter;
import javax.jms.ObjectMessage;
import java.io.File;
import java.util.List;

/**
 * Producer for message to set selector property and send to input queue
 *
 * @author Skyhunter
 * @date 14.01.2021
 */
@Component
public class JmsProducer {

    private static Logger LOG = LogManager.getLogger(JmsProducer.class);

    @Value("${jms.input.queue}")
    private final String jmsInputQueue = "jmsComponent:queue:inputqueue";

    private final JmsTemplate jmsTemplate;

    public JmsProducer(JmsTemplate jmsTemplate, JmsXmlMessageConverter JmsXmlMessageConverter) {
        this.jmsTemplate = jmsTemplate;
        this.jmsTemplate.setMessageConverter(JmsXmlMessageConverter);
    }

    public void sendMessage(List<File> xmlFileList) {
        xmlFileList.forEach(xmlFile ->
            jmsTemplate.convertAndSend(jmsInputQueue, xmlFile, messagePostProcessor)
        );
    }

    private MessagePostProcessor messagePostProcessor = message -> {
        message.setJMSReplyTo(new ActiveMQQueue(jmsInputQueue));
        if (message instanceof ObjectMessage) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                RoutingDocument document = (RoutingDocument) objectMessage.getObject();
                message.setStringProperty("selector", document.getDocType());
            } catch (Exception e) {
                LOG.error(String.format("Error read value from xml: %s", e.getMessage()));
            }
        }
        return message;
    };

}

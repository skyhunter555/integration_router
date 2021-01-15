package ru.syntez.integration.router.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ru.syntez.integration.router.entities.RoutingDocument;
import javax.jms.ObjectMessage;

/**
 * Consumer for message with type invoice
 *
 * @author Skyhunter
 * @date 14.01.2021
 */
@Component
public class JmsInvoiceReceiver {

    private static Logger LOG = LogManager.getLogger(JmsInvoiceReceiver.class);

    @Value("${jms.input.queue}")
    private final String jmsInputQueue = "jmsComponent:queue:inputqueue";

    @JmsListener(containerFactory = "defaultJmsListenerContainerFactory", destination = jmsInputQueue, selector = "selector = 'invoice'")
    public void processMessage(ObjectMessage message) {
        try {
            if (message.getObject() instanceof RoutingDocument) {
                RoutingDocument document = (RoutingDocument) message.getObject();
                LOG.info(String.format("******** SELECTED INVOICE OUTPUT QUEUE FOR DOCTYPE: %s",  document.getDocType()));
            }
        } catch (Exception e) {
            LOG.error(String.format("Error read value from xml: %s", e.getMessage()));
        }
    }

}


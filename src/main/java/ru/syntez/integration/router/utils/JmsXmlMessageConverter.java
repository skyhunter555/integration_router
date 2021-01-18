package ru.syntez.integration.router.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import ru.syntez.integration.router.entities.RoutingDocument;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Convert xml file to RouteDocument model on message
 *
 * @author Skyhunter
 * @date 14.01.2021
 */
@Component
public class JmsXmlMessageConverter implements MessageConverter {

    private static Logger LOG = LogManager.getLogger(JmsXmlMessageConverter.class);

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {

        if (object instanceof File) {
            File filePayload = (File) object;
            try {
                String xmlPayload = FileUtils.readFileToString(filePayload, StandardCharsets.UTF_8);
                return new Message(xmlPayload.getBytes(), messageProperties);
            } catch (IOException e) {
                LOG.error("Error converting form file", e);
            }
        }
        if (object instanceof String) {
            return new Message(((String) object).getBytes(), messageProperties);
        }
        return null;
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        return null;
    }

    /*
    @Override
    public Message toMessage(Object object, Session session) throws JMSException {

        ObjectMessage message = session.createObjectMessage();
        if (object instanceof File) {
            File filePayload = (File) object;
            try {
                String xmlPayload = FileUtils.readFileToString(filePayload, StandardCharsets.UTF_8);
                RoutingDocument document = mapper.readValue(xmlPayload, RoutingDocument.class);
                message.setObject(document);
            } catch (IOException e) {
                LOG.error("Error converting form file", e);
            }
        }
        return message;
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        return null;
    }*/


}

# Service for testing Spring Integration with ActiveMQ 
Library name: integration-router

  В примере создается одна входящая очередь (jmsComponent:queue:inputqueue)
и две исходящих очереди (jmsComponent:queue:orderoutputqueue и jmsComponent:invoiceoutputqueue)
для разных типов документов.
При отправке сообщения в виде XML файла проводиться проверка типа документа "docType".
В зависимости от значени "order" или "invoice", сообщение отправляется в первую или во вторую исходящую очередь.
Отправляется по 100 сообщений каждого типа.

После обработки сообщений в логе выводиться информация:
******** SELECTED INVOICE OUTPUT QUEUE FOR DOCTYPE: invoice
******** SELECTED ORDER OUTPUT QUEUE FOR DOCTYPE: order

Ссылки на использованную документацию:
https://docs.spring.io/spring-integration/docs/5.1.0.M1/reference/html/messaging-routing-chapter.html
https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/boot-features-messaging.html
https://springframework.guru/spring-boot-example-of-spring-integration-and-activemq/
https://www.linkedin.com/pulse/springboot-jms-content-based-routing-activemq-demirci

## Example
java -jar integration-router-1.0.0.jar

## Build
mvn clean install

# Service for testing Spring Integration with ActiveMQ 
Library name: integration-router

  � ������� ��������� ���� �������� ������� (jmsComponent:queue:inputqueue)
� ��� ��������� ������� (jmsComponent:queue:orderoutputqueue � jmsComponent:invoiceoutputqueue)
��� ������ ����� ����������.
��� �������� ��������� � ���� XML ����� ����������� �������� ���� ��������� "docType".
� ����������� �� ������� "order" ��� "invoice", ��������� ������������ � ������ ��� �� ������ ��������� �������.
������������ �� 100 ��������� ������� ����.

����� ��������� ��������� � ���� ���������� ����������:
******** SELECTED INVOICE OUTPUT QUEUE FOR DOCTYPE: invoice
******** SELECTED ORDER OUTPUT QUEUE FOR DOCTYPE: order

������ �� �������������� ������������:
https://docs.spring.io/spring-integration/docs/5.1.0.M1/reference/html/messaging-routing-chapter.html
https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/boot-features-messaging.html
https://springframework.guru/spring-boot-example-of-spring-integration-and-activemq/
https://www.linkedin.com/pulse/springboot-jms-content-based-routing-activemq-demirci

## Example
java -jar integration-router-1.0.0.jar

## Build
mvn clean install

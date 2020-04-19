package com.sen.gmall.payment.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

/**
 * @Author: Sen
 * @Date: 2019/11/11 19:43
 * @Description:
 */
public class ActivemqProducerTest {
    public static void main(String[] args) {

        ConnectionFactory connect = new ActiveMQConnectionFactory("tcp://192.168.161.141:61616");
        try {
            Connection connection = connect.createConnection();
            connection.start();
            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue testqueue = session.createQueue("drink");

            MessageProducer producer = session.createProducer(testqueue);
            TextMessage textMessage=new ActiveMQTextMessage();
            textMessage.setText("老板：我口渴了，谁能去给我倒一杯水");
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(textMessage);
            session.commit();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }


}

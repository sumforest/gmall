package com.sen.gmall.payment.test;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @Author: Sen
 * @Date: 2019/11/11 19:44
 * @Description: 话题订阅者
 */
public class ActivemqConsumerTopicTest {
    public static void main(String[] args) {
        ConnectionFactory connect = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,ActiveMQConnection.DEFAULT_PASSWORD,"tcp://192.168.161.141:61616");
        try {
            Connection connection = connect.createConnection();
            connection.setClientID("student");
            connection.start();
            //第一个值表示是否使用事务，如果选择true，第二个值相当于选择0
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("全国十九大");

            MessageConsumer consumer = session.createDurableSubscriber(topic, "student");
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if(message instanceof TextMessage){
                        try {
                            String text = ((TextMessage) message).getText();
                            System.out.println(text);
                            System.out.println("学习艰苦奋斗精神");

                            //session.rollback();
                        } catch (JMSException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();;
        }
    }

}

package com.sen.gmall.util;

import javax.jms.*;

/**
 * @Auther: Sen
 * @Date: 2019/11/12 00:18
 * @Description:
 */
public class ProductMessageUtil {

    public static Session sendMessage(ActiveMQUtil activeMQUtil,MapMessage mapMessage,String queueName) {
        Connection connection = null;
        Session session = null;
        try {
            //创建MQ的发送者
            ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue payhment_success_queue = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(payhment_success_queue);

            //方式消息的参数
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mapMessage);

            //提交事务
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
            //事务回滚
            try {
                session.rollback();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        } finally {
            //归还连接
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        return session;
    }
}
package com.xiaoli.jms.test;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.leveldb.replicated.SlaveLevelDBStore;

public class HelloPublisher {

	TopicConnection topicConnection;// JMS���ӣ�����Pub/Sub��ʽ������
	TopicSession topicSession; // JMS�Ự������Pub/Sub��ʽ�ĻỰ
	TopicPublisher topicPublisher; // ��Ϣ������
	Topic topic; // ����

	public HelloPublisher(String factoryJNDI, String topicJNDI) throws JMSException, NamingException {

		// ��������JMS������������(context)
		Context context = new InitialContext();

		// ͨ�����ӹ�����JNDI������ConnectionFactory
		TopicConnectionFactory topicFactory = (TopicConnectionFactory) context.lookup(factoryJNDI);

		// �����ӹ�������һ��JMS����
		topicConnection = topicFactory.createTopicConnection();

		// ͨ��JMS���Ӵ���һ��Session
		topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

		// ͨ�������Ĳ��ҵ�һ������(topic)
		topic = (Topic) context.lookup(topicJNDI);

		// ��session������һ���ض��������Ϣ������
		topicPublisher = topicSession.createPublisher(topic);
	}

	/**
	 * ����һ���ı���Ϣ
	 * 
	 * @param msg
	 *            ����������Ϣ
	 * @throws JMSException
	 */
	public void publish(String msg) throws JMSException {
		// ��session������һ���ı����͵���Ϣ
		TextMessage message = topicSession.createTextMessage();
		message.setText(msg);// ������Ϣ����
		topicPublisher.publish(topic, message);// ��Ϣ���ͣ����͵��ض�����
	}

	public void close() throws JMSException {
		topicSession.close();// �ر�session
		topicConnection.close();// �ر�����
	}

	public static void main(String[] args) throws JMSException, NamingException {
		HelloPublisher publisher = new HelloPublisher("TopicConnectionFactory", "topic1");
		try {
			for (int i = 1; i < 11; i++) {
				String msg = "Hello World no. " + i;
				publisher.publish(msg);
			}
			Thread.sleep(2000);
			for (int i = 11; i < 21; i++) {
				String msg = "Hello World no. " + i;
				publisher.publish(msg);
			}
			publisher.close();// session��connection����֮��һ���ǵùر�
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
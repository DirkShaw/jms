package com.xiaoli.jms.test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class HelloSubscriber implements MessageListener {
	TopicConnection topicConnection;
	TopicSession topicSession;
	TopicSubscriber topicSubscriber;
	Topic topic;

	public HelloSubscriber(String factoryJNDI, String topicJNDI) throws JMSException, NamingException {
		Context context = new InitialContext();

		TopicConnectionFactory topicFactory = (TopicConnectionFactory) context.lookup(factoryJNDI);
		// ��������
		topicConnection = topicFactory.createTopicConnection();
		topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);// ����session
		topic = (Topic) context.lookup(topicJNDI);// ���ҵ�����
		// ��session����һ���ض�queue����Ϣ������
		topicSubscriber = topicSession.createSubscriber(topic);
		// ע��������������õļ������Լ�����Ϊ�����Ѿ�ʵ����MessageListener�ӿڣ�
		// һ��queueReceiver���յ�����Ϣ���ͻ���ñ����onMessage����
		topicSubscriber.setMessageListener(this);
		System.out.println("HelloSubscriber subscribed to topic: " + topicJNDI);
		System.out.println(System.currentTimeMillis());
		topicConnection.start();// �������ӣ���ʱ��������������Ч
	}

	public void onMessage(Message msg) {
		try {
			if (msg instanceof TextMessage) {
				// ��Message ת�ͳ� TextMessage ����ȡ��Ϣ����
				String msgTxt = ((TextMessage) msg).getText();
				System.out.println("HelloSubscriber got message: " + msgTxt);
			}
		} catch (JMSException ex) {
			System.err.println("Could not get text message: " + ex);
			ex.printStackTrace();
		}
	}

	public void close() throws JMSException {
		topicSession.close();
		topicConnection.close();
	}

	public static void main(String[] args) {
		try {
			new HelloSubscriber("TopicConnectionFactory", "topic1");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
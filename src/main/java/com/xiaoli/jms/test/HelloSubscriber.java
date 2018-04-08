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
		// 创建连接
		topicConnection = topicFactory.createTopicConnection();
		topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);// 创建session
		topic = (Topic) context.lookup(topicJNDI);// 查找到主题
		// 用session创建一个特定queue的消息接收者
		topicSubscriber = topicSession.createSubscriber(topic);
		// 注册监听，这里设置的监听是自己，因为本类已经实现了MessageListener接口，
		// 一旦queueReceiver接收到了消息，就会调用本类的onMessage方法
		topicSubscriber.setMessageListener(this);
		System.out.println("HelloSubscriber subscribed to topic: " + topicJNDI);
		System.out.println(System.currentTimeMillis());
		topicConnection.start();// 启动连接，这时监听器才真正生效
	}

	public void onMessage(Message msg) {
		try {
			if (msg instanceof TextMessage) {
				// 把Message 转型成 TextMessage 并提取消息内容
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
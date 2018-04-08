package com.xiaoli.jms.test;

import java.io.*;
import javax.jms.*;
import javax.naming.*;
 
public class Chat implements javax.jms.MessageListener{
    private TopicSession pubSession;
    private TopicPublisher publisher;
    private TopicConnection connection;
    private String username;
 
    /* Constructor used toInitialize Chat */
    public Chat(String topicFactory, String topicName, String username) 
        throws Exception {
      
       //Obtain a JNDI connection using the jndi.properties file
        InitialContext ctx = new InitialContext();
 
        // Look up aJMS connection factory
       TopicConnectionFactory conFactory = 
             (TopicConnectionFactory)ctx.lookup(topicFactory);
 
        // Create aJMS connection
       TopicConnection connection = conFactory.createTopicConnection();
 
        // Create twoJMS session objects
        TopicSession pubSession = connection.createTopicSession(
             false,Session.AUTO_ACKNOWLEDGE);        
        TopicSession subSession = connection.createTopicSession(
             false,Session.AUTO_ACKNOWLEDGE);
 
        // Look up aJMS topic
        Topic chatTopic = (Topic)ctx.lookup(topicName);
 
        // Create aJMS publisher and subscriber
        TopicPublisher publisher = 
           pubSession.createPublisher(chatTopic);
       TopicSubscriber subscriber = 
           subSession.createSubscriber(chatTopic, null, true);
 
        // Set a JMSmessage listener
       subscriber.setMessageListener(this);
 
        // Intializethe Chat application variables
       this.connection = connection;
       this.pubSession = pubSession;
        this.publisher= publisher;
        this.username= username;
 
        // Start theJMS connection; allows messages to be delivered
       connection.start( );
    }
 
    /* Receive Messages FromTopic Subscriber */
    public void onMessage(Message message) {
        try {
           TextMessage textMessage = (TextMessage) message;
           String text = textMessage.getText( );
           System.out.println(text);
        } catch(JMSException jmse){ jmse.printStackTrace( ); }
    }
 
    /* Create and Send MessageUsing Publisher */
    protected void writeMessage(String text) throws JMSException {
        TextMessage message = pubSession.createTextMessage( );
       message.setText(username+": "+text);
       publisher.publish(message);
    }
 
    /* Close the JMS Connection*/
    public void close( ) throws JMSException {
       connection.close( );
    }
 
    /* Run the Chat Client */
    public static void main(String [] args){
        try{
           if (args.length!=3)
               System.out.println("Factory, Topic, or usernamemissing");
 
           // args[0]=topicFactory; args[1]=topicName; args[2]=username
           Chat chat = new Chat(args[0],args[1],args[2]);
 
           // Read from command line
           BufferedReader commandLine = new 
             java.io.BufferedReader(new InputStreamReader(System.in));
 
           // Loop until the word "exit" is typed
           while(true){
               String s = commandLine.readLine( );
               if (s.equalsIgnoreCase("exit")){
                   chat.close( ); // close down connection
                   System.exit(0);// exit program
               } else 
                   chat.writeMessage(s);
           }
        } catch(Exception e){ e.printStackTrace( ); }
    }
}
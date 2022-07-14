/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messageBeans;

import beanManagers.AccountManager;
import jakarta.annotation.Resource;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author prunellaparisa & abbylaloli
 */
@MessageDriven(activationConfig
        = {
            @ActivationConfigProperty(propertyName = "clientId",
                    propertyValue = "jms/AccountMessageQueue"),
            @ActivationConfigProperty(propertyName = "destinationLookup",
                    propertyValue = "jms/AccountMessageQueue"),
            @ActivationConfigProperty(propertyName = "subscriptionDurability",
                    propertyValue = "Durable"),
            @ActivationConfigProperty(propertyName = "subscriptionName",
                    propertyValue = "jms/AccountMessageQueue"),
            @ActivationConfigProperty(propertyName = "destinationType",
                    propertyValue = "jakarta.jms.Queue")
        })
public class AccountMessage implements MessageListener {

    @EJB
    private AccountManager manager;
    @Resource(mappedName = "jms/AccountConnectionFactory")
    private ConnectionFactory connectionFactory;
    private Logger logger;
    private Connection sendingConn;
    private Session sendingSession;

    public AccountMessage() {
        logger = java.util.logging.Logger.getLogger(getClass().getName());
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                logger.info("DonationMessage received text message: "
                        + ((TextMessage) message).getText());
            } else if (message instanceof MapMessage) {
                String method = (String) ((MapMessage) message).getObject("method");
                if (method.equals("addAccount")) {
                    createSendingConn();
                    String username = (String) ((MapMessage) message).getObject("name");
                    String email = (String) ((MapMessage) message).getObject("email");
                    String password = (String) ((MapMessage) message).getObject("password");
                    MessageProducer replyProducer = sendingSession.createProducer(message.getJMSReplyTo());
                    TextMessage replyMessage = sendingSession.createTextMessage();
                    String response = (String) manager.addAccount(username, email, password);
                    try {
                        replyMessage.setText(response);
                        // optionally tell sender which message this reply is for 
                        replyMessage.setJMSCorrelationID(message.getJMSMessageID());
                        replyProducer.send(replyMessage);
                    } catch (JMSException ex) {
                        Logger.getLogger(AccountMessage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    replyProducer.close(); // close if no longer required
                    logger.info("Received text message from queue: method_" + method + ", username_" + username + ", email_" + email);
                } else if (method.equals("getOneAccount")) {
                    createSendingConn();
                    MessageProducer replyProducer = sendingSession.createProducer(message.getJMSReplyTo());
                    TextMessage replyMessage = sendingSession.createTextMessage();
                    String username = (String) ((MapMessage) message).getObject("name");
                    String password = (String) ((MapMessage) message).getObject("password");
                    String response = (String) manager.getOneAccount(username, password);
                    try {
                        replyMessage.setText(response);
                        // optionally tell sender which message this reply is for 
                        replyMessage.setJMSCorrelationID(message.getJMSMessageID());
                        replyProducer.send(replyMessage);
                    } catch (JMSException ex) {
                        Logger.getLogger(AccountMessage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    replyProducer.close(); // close if no longer required
                    logger.info("Received text message from queue: method_" + method);
                }
            } else {
                logger.info("DonationMessage received non-text message: " + message);
            }
        } catch (JMSException e) {
            logger.warning("Exception with incoming message: " + e);
        }
    }

    public void createSendingConn() {
        if (connectionFactory == null) {
            logger.warning("Dependency injection of jms/AccountConnectionFactory failed");
        } else {
            try {
                // obtain a connection to the JMS provider
                sendingConn = connectionFactory.createConnection();
                // obtain an untransacted context for producing messages
                sendingSession = sendingConn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                logger.info("Dependency injection of jms/AccountConnectionFactory was successful");
            } catch (JMSException e) {
                logger.warning("Error while creating sessions: " + e);
            }
        }
    }

}

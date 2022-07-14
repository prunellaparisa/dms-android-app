/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.inject.Named;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.logging.Logger;

/**
 *
 * @author prunellaparisa & abbylaloli
 */
@Named
@Path("/accounts")
public class AccountResources {

    @Resource(mappedName = "jms/AccountConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/AccountMessageQueue")
    private Queue queue;
    private Connection sendingConn;
    private Session sendingSession;
    private Logger logger;

    /**
     * Creates a new instance of AccountResources
     */
    public AccountResources() {
    }

    @PostConstruct
    public void setupJMSSessions() {
        logger = Logger.getLogger(getClass().getName());
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
        if (queue == null) {
            logger.warning("Dependency injection of jms/AccountMessageQueue failed");
        } else {
            logger.info("Dependency injection of jms/AccountMessageQueue was successful");
        }
    }

    @PreDestroy
    public void closeJMSSessions() {
        try {
            if (sendingSession != null) {
                sendingSession.close();
            }
            if (sendingConn != null) {
                sendingConn.close();
            }
        } catch (JMSException e) {
            logger.warning("Unable to close connection: " + e);
        }
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{username}/{email}/{password}")
    public String addAccount(@PathParam("username") String username, @PathParam("email") String email,
            @PathParam("password") String password) {
        // send messages to the queue
        try {
            Queue tempQueue = sendingSession.createTemporaryQueue();
            // obtain a producer of messages to send to the queue
            MessageProducer producer = sendingSession.createProducer(queue);
            // create and send a string message
            MapMessage message = sendingSession.createMapMessage();
            message.setJMSReplyTo(tempQueue);
            message.setObject("method", "addAccount");
            message.setObject("name", username);
            message.setObject("email", email);
            message.setObject("password", password);
            logger.info("Sending message: " + message.getObject("method"));
            producer.send(message);

            // trying to receive message from temporary queue
            MessageConsumer consumer = sendingSession.createConsumer(tempQueue);
            sendingConn.start();
            Message nextMessage = null;
            String receivedMessage = "";
            do {
                nextMessage = consumer.receive(1000);
                if (nextMessage != null && nextMessage instanceof TextMessage) {
                    receivedMessage = (String) ((TextMessage) nextMessage).getText();
                    logger.info("Received text message from queue: " + receivedMessage);
                }
            } while (nextMessage != null);
            return receivedMessage;
        } catch (JMSException e) {
            logger.warning("Error while sending messages: " + e);
        }
        return null;
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{username}/{password}")
    public String getOneAccount(@PathParam("username") String username, @PathParam("password") String password) {
        // send messages to the queue
        try {
            Queue tempQueue = sendingSession.createTemporaryQueue();
            // obtain a producer of messages to send to the queue
            MessageProducer producer = sendingSession.createProducer(queue);
            // create and send a string message
            MapMessage message = sendingSession.createMapMessage();
            message.setJMSReplyTo(tempQueue);
            message.setObject("method", "getOneAccount");
            message.setObject("name", username);
            message.setObject("password", password);
            logger.info("Sending message: " + message.getObject("method"));
            producer.send(message);

            // trying to receive message from temporary queue
            MessageConsumer consumer = sendingSession.createConsumer(tempQueue);
            sendingConn.start();
            TextMessage receivedMessage = (TextMessage) consumer.receive(1000);
            return receivedMessage.getText();
        } catch (JMSException e) {
            logger.warning("Error while sending messages: " + e);
        }
        return null;
    }

}

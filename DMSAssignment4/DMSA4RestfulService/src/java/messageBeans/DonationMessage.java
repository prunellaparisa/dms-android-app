package messageBeans;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author prunellaparisa & abbylaloli
 */
import beanManagers.DonationManager;
import beans.Donation;
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
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author prunellaparisa & abbylaloli
 */
@MessageDriven(activationConfig
        = {
            @ActivationConfigProperty(propertyName = "clientId",
                    propertyValue = "jms/DonationMessageQueue"),
            @ActivationConfigProperty(propertyName = "destinationLookup",
                    propertyValue = "jms/DonationMessageQueue"),
            @ActivationConfigProperty(propertyName = "subscriptionDurability",
                    propertyValue = "Durable"),
            @ActivationConfigProperty(propertyName = "subscriptionName",
                    propertyValue = "jms/DonationMessageQueue"),
            @ActivationConfigProperty(propertyName = "destinationType",
                    propertyValue = "jakarta.jms.Queue")
        })
public class DonationMessage implements MessageListener {

    @EJB
    private DonationManager manager;
    @Resource(mappedName = "jms/DonationConnectionFactory")
    private ConnectionFactory connectionFactory;
    private Logger logger;
    private Connection sendingConn;
    private Session sendingSession;

    public DonationMessage() {
        logger = Logger.getLogger(getClass().getName());
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                logger.info("DonationMessage received text message: "
                        + ((TextMessage) message).getText());
            } else if (message instanceof MapMessage) {
                String method = (String) ((MapMessage) message).getObject("method");
                if (method.equals("addDonation")) {
                    String username = (String) ((MapMessage) message).getObject("name");
                    String food = (String) ((MapMessage) message).getObject("food");
                    String latitude = (String) ((MapMessage) message).getObject("latitude");
                    String longitude = (String) ((MapMessage) message).getObject("longitude");
                    manager.addDonation(username, food, latitude, longitude);
                    logger.info("Received text message from queue: method_" + method + ", username_" + username + ", food_" + food);
                } else if (method.equals("getAllDonations")) {
                    createSendingConn();
                    MessageProducer replyProducer = sendingSession.createProducer(message.getJMSReplyTo());
                    ObjectMessage replyMessage = sendingSession.createObjectMessage();
                    ArrayList<Donation> donations = (ArrayList<Donation>) manager.getAllDonations();
                    donations.forEach(i -> {
                        try {
                            replyMessage.setObject(i);
                            // optionally tell sender which message this reply is for 
                            replyMessage.setJMSCorrelationID(message.getJMSMessageID());
                            replyProducer.send(replyMessage);
                        } catch (JMSException ex) {
                            Logger.getLogger(DonationMessage.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

                    replyProducer.close(); // close if no longer required
                    logger.info("Received text message from queue: method_" + method);
                } else if (method.equals("getOwnDonations")) {
                    String username = (String) ((MapMessage) message).getObject("name");
                    createSendingConn();
                    MessageProducer replyProducer = sendingSession.createProducer(message.getJMSReplyTo());
                    ObjectMessage replyMessage = sendingSession.createObjectMessage();
                    ArrayList<Donation> donations = (ArrayList<Donation>) manager.getOwnDonations(username);
                    donations.forEach(i -> {
                        try {
                            replyMessage.setObject(i);
                            // optionally tell sender which message this reply is for 
                            replyMessage.setJMSCorrelationID(message.getJMSMessageID());
                            replyProducer.send(replyMessage);
                        } catch (JMSException ex) {
                            Logger.getLogger(DonationMessage.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

                    replyProducer.close(); // close if no longer required
                    logger.info("Received text message from queue: method_" + method);
                } else if (method.equals("deleteDonation")) {
                    String id = (String) ((MapMessage) message).getObject("id");
                    manager.deleteDonation(Integer.parseInt(id));
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
            logger.warning("Dependency injection of jms/DonationConnectionFactory failed");
        } else {
            try {
                // obtain a connection to the JMS provider
                sendingConn = connectionFactory.createConnection();
                // obtain an untransacted context for producing messages
                sendingSession = sendingConn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                logger.info("Dependency injection of jms/DonationConnectionFactory was successful");
            } catch (JMSException e) {
                logger.warning("Error while creating sessions: " + e);
            }
        }
    }
}

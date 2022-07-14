/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import beans.Donation;
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
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author prunellaparisa & abbylaloli
 */
@Named
@Path("/donations")
public class DonationResources {

    @Resource(mappedName = "jms/DonationConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/DonationMessageQueue")
    private Queue queue;
    private Connection sendingConn;
    private Session sendingSession;
    private Logger logger;

    /**
     * Creates a new instance of DonationResources
     */
    public DonationResources() {
    }

    @PostConstruct
    public void setupJMSSessions() {
        logger = Logger.getLogger(getClass().getName());
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
        if (queue == null) {
            logger.warning("Dependency injection of jms/DonationMessageQueue failed");
        } else {
            logger.info("Dependency injection of jms/DonationMessageQueue was successful");
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllDonations() {
        // send messages to the queue
        try {
            Queue tempQueue = sendingSession.createTemporaryQueue();
            // obtain a producer of messages to send to the queue
            MessageProducer producer = sendingSession.createProducer(queue);
            // create and send a string message
            MapMessage message = sendingSession.createMapMessage();
            message.setObject("method", "getAllDonations");
            message.setJMSReplyTo(tempQueue);
            logger.info("Sending message: " + message.getObject("method"));
            producer.send(message);

            // trying to receive message from temporary queue
            MessageConsumer consumer = sendingSession.createConsumer(tempQueue);
            sendingConn.start();
            Message nextMessage = null;
            ArrayList<Donation> donations = new ArrayList<>();
            do {
                nextMessage = consumer.receive(1000);
                if (nextMessage != null && nextMessage instanceof ObjectMessage) {
                    Donation d = (Donation) ((ObjectMessage) nextMessage).getObject();
                    donations.add(d);
                    logger.info("Received donation object from queue: " + d.toString());
                }
            } while (nextMessage != null);

            String json = "[";
            for (int i = 0; i < donations.size(); ++i) {
                if (i > 0) {
                    json += ",";
                }
                Donation d = (Donation) donations.get(i);
                json += "{\"" + "id" + "\":\"" + d.getId() + "\",";
                json += "\"" + "username" + "\":\"" + d.getUsername() + "\",";
                json += "\"" + "food" + "\":\"" + d.getFood() + "\",";
                json += "\"" + "latitude" + "\":\"" + d.getLatitude() + "\",";
                json += "\"" + "longitude" + "\":\"" + d.getLongitude() + "\"}";
            }
            json += "]";
            return json;
        } catch (JMSException e) {
            logger.warning("Error while sending messages: " + e);
        }
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{username}")
    public String getOwnDonations(@PathParam("username") String username) {
        // send messages to the queue
        try {
            Queue tempQueue = sendingSession.createTemporaryQueue();
            // obtain a producer of messages to send to the queue
            MessageProducer producer = sendingSession.createProducer(queue);
            // create and send a string message
            MapMessage message = sendingSession.createMapMessage();
            message.setObject("method", "getOwnDonations");
            message.setObject("name", username);
            message.setJMSReplyTo(tempQueue);
            logger.info("Sending message: " + message.getObject("method"));
            producer.send(message);

            // trying to receive message from temporary queue
            MessageConsumer consumer = sendingSession.createConsumer(tempQueue);
            sendingConn.start();
            Message nextMessage = null;
            ArrayList<Donation> donations = new ArrayList<>();
            do {
                nextMessage = consumer.receive(1000);
                if (nextMessage != null && nextMessage instanceof ObjectMessage) {
                    Donation d = (Donation) ((ObjectMessage) nextMessage).getObject();
                    donations.add(d);
                    logger.info("Received donation object from queue: " + d.toString());
                }
            } while (nextMessage != null);

            String json = "[";
            for (int i = 0; i < donations.size(); ++i) {
                if (i > 0) {
                    json += ",";
                }
                Donation d = (Donation) donations.get(i);
                json += "{\"" + "id" + "\":\"" + d.getId() + "\",";
                json += "\"" + "username" + "\":\"" + d.getUsername() + "\",";
                json += "\"" + "food" + "\":\"" + d.getFood() + "\",";
                json += "\"" + "latitude" + "\":\"" + d.getLatitude() + "\",";
                json += "\"" + "longitude" + "\":\"" + d.getLongitude() + "\"}";
            }
            json += "]";
            return json;
        } catch (JMSException e) {
            logger.warning("Error while sending messages: " + e);
        }
        return null;
    }

    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("{username}/{food}/{latitude}/{longitude}")
    public void addDonation(@PathParam("username") String username, @PathParam("food") String food,
            @PathParam("latitude") String latitude, @PathParam("longitude") String longitude) {
        // send messages to the queue
        try {
            // obtain a producer of messages to send to the queue
            MessageProducer producer = sendingSession.createProducer(queue);
            // create and send a string message
            MapMessage message = sendingSession.createMapMessage();
            message.setObject("method", "addDonation");
            message.setObject("name", username);
            message.setObject("food", food);
            message.setObject("latitude", latitude);
            message.setObject("longitude", longitude);
            logger.info("Sending message: " + message.getObject("method"));
            producer.send(message);
        } catch (JMSException e) {
            logger.warning("Error while sending messages: " + e);
        }
    }

    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("{id}")
    public void deleteDonation(@PathParam("id") String id) {
        // send messages to the queue
        try {
            // obtain a producer of messages to send to the queue
            MessageProducer producer = sendingSession.createProducer(queue);
            // create and send a string message
            MapMessage message = sendingSession.createMapMessage();
            message.setObject("method", "deleteDonation");
            message.setObject("id", id);
            logger.info("Sending message: " + message.getObject("method"));
            producer.send(message);
        } catch (JMSException e) {
            logger.warning("Error while sending messages: " + e);
        }
    }
}

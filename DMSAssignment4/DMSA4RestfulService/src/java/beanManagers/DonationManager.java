/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package beanManagers;

import beans.Donation;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author prunellaparisa & abbylaloli
 */
@Stateless
public class DonationManager {

    @PersistenceContext(unitName = "DMSA4RestfulServicePU-1")
    private EntityManager entityManager;

    public DonationManager() {
    }

    public void addDonation(String name, String food, String latitude, String longitude) {
        Donation donation = new Donation(name, food, latitude, longitude);
        entityManager.persist(donation);
    }

    public void deleteDonation(Integer id) {
        String jpqlCommand = "SELECT d FROM Donation d WHERE d.id LIKE :id";
        Query query = entityManager.createQuery(jpqlCommand);
        String id_string = id.toString();
        query.setParameter("id", id_string);
        Donation d = (Donation) query.getSingleResult();
        entityManager.remove(d);
    }

    public List<Donation> getAllDonations() {
        Vector<Donation> d = (Vector<Donation>) entityManager.createQuery("SELECT d from Donation d").getResultList();
        ArrayList<Donation> arr = new ArrayList<>();
        for (int i = 0; i < d.size(); ++i) {
            arr.add(d.get(i));
        }
        return arr;
    }

    public List<Donation> getOwnDonations(String username) {
        String jpqlCommand = "SELECT d from Donation d WHERE d.username LIKE :username";
        Query query = entityManager.createQuery(jpqlCommand);
        query.setParameter("username", username);
        Vector<Donation> d = (Vector<Donation>) query.getResultList();
        ArrayList<Donation> arr = new ArrayList<>();
        for (int i = 0; i < d.size(); ++i) {
            arr.add(d.get(i));
        }
        return arr;
    }
}

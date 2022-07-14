/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beanManagers;

import beans.Account;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 *
 * @author prunellaparisa & abbylaloli
 */
@Stateless
public class AccountManager {

    @PersistenceContext(unitName = "DMSA4RestfulServicePU-2")
    private EntityManager entityManager;

    public AccountManager() {
    }

    public String addAccount(String username, String email, String password) {
        Account account = new Account(username, email, password);
        String message = "";
        Account a = null;
        try {
            String jpqlCommand = "SELECT a FROM Account a WHERE a.username LIKE :username";
            Query query = entityManager.createQuery(jpqlCommand);
            query.setParameter("username", username);
            a = (Account) query.getSingleResult();
            if (password.contentEquals(a.getPassword())) {
                message = "account already exists";
            } else {
                message = "username already exists";
            }
        } catch (NoResultException e) {
            message = "account creation successful";
            entityManager.persist(account);
        }
        return message;
    }

    public String getOneAccount(String username, String password) {
        String message = "";
        Account a = null;
        try {
            String jpqlCommand = "SELECT a FROM Account a WHERE a.username LIKE :username";
            Query query = entityManager.createQuery(jpqlCommand);
            query.setParameter("username", username);
            a = (Account) query.getSingleResult();
            if (password.contentEquals(a.getPassword())) {
                message = "valid account";
            } else {
                message = "wrong password";
            }
        } catch (NoResultException e) {
            message = "account non-existent";
        }
        return message;
    }
}

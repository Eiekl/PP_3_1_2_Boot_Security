package ru.kata.spring.boot_security.demo.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    RolesDao rolesDao;
    @PersistenceContext
    private final EntityManager entityManager;

    public UserDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void saveUser(User user) {
        if (getUserByName(user.getName()) != null) {
            return;
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        entityManager.persist(user);
    }

    @Override
    public void removeUser(int id) {
        entityManager.remove(getUserById(id));
    }

    @Override
    public List<User> getAllUsers() {
        return entityManager.createQuery("select u from User u", User.class).getResultList();
    }

    @Override
    public User getUserById(int id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void updateUser(int id, User updatedUser) {
        User user = getUserById(id);
        user.setUsername(updatedUser.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(updatedUser.getPassword()));
        user.setEmail(updatedUser.getEmail());
        user.setName(updatedUser.getName());
        user.setSurName(updatedUser.getSurName());
        user.setRoles(updatedUser.getRoles());

        entityManager.merge(user);
    }

    @Override
    public User getUserByName(String name) {
        return entityManager.createQuery("select u from User u where u.username = :name"
                , User.class).setParameter("name", name).getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public List<Role> getRoles() {
        return rolesDao.findAll();
    }
}

package com.niit.DaoImpl;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.niit.Dao.UserDao;
import com.niit.model.User;

@Repository
@Transactional
public class UserDaoImpl implements UserDao{
@Autowired
private SessionFactory sessionFactory;
public void registerUser(User user) {
     Session session=sessionFactory.getCurrentSession();
     session.save(user);
	
}
	


	public boolean isEmailValid(String email) {
		Session session=sessionFactory.getCurrentSession();
		Query query=session.createQuery("from User where email='"+email+"'");
		User user=(User)query.uniqueResult();
		if(user==null)//email is valid,unique
			return true;
		else
			return false;
	}

	
	public boolean isUsernameValid(String username) {
		Session session=sessionFactory.getCurrentSession();
		User user=(User)session.get(User.class, username);
		if(user==null)
			return true;
		else
			return false;
	}



	@Override
	public User validateUsername(String username) {
		Session session=sessionFactory.getCurrentSession();
		User user=(User)session.get(User.class, username);
		return user;
	}


	@Override
	public User validateEmail(String email) {
		Session session=sessionFactory.getCurrentSession();
		Query query=session.createQuery("from User where email=?");
		query.setString(0,email);
		User user=(User)query.uniqueResult();
		return user;
	}



	@Override
	public User login(User user) {
		Session session=sessionFactory.getCurrentSession();
		Query query=session.createQuery("from User where username=? and password=?");
		query.setString(0, user.getUsername());
		query.setString(1, user.getPassword());
		return (User)query.uniqueResult();

	}


	@Override
	public void update(User user) {
		Session session=sessionFactory.getCurrentSession();
		session.update(user);//update the values [online status]		
	}



	@Override
	public User getUserByUsername(String username) {
		Session session=sessionFactory.getCurrentSession();
		User user=(User)session.get(User.class, username);
		return user;

	}

	

}

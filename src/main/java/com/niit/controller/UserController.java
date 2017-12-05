package com.niit.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.niit.Dao.UserDao;
import com.niit.model.ErrorClazz;
import com.niit.model.User;

@Controller
public class UserController {


@Autowired
private UserDao userDao;
//? - Any Type
@RequestMapping(value="/registeruser",method=RequestMethod.POST)
public ResponseEntity<?> registerUser(@RequestBody User user){
	try{
	User duplicateUser	=userDao.validateUsername(user.getUsername());
	if(duplicateUser!=null){
		//response.data is error
		//response.status is 406 NOT_ACCEPTABLE
		ErrorClazz error=new ErrorClazz(2,"Username already exists.. please enter different username");
		return new ResponseEntity<ErrorClazz>(error,HttpStatus.NOT_ACCEPTABLE);
	}
	User duplicateEmail=userDao.validateEmail(user.getEmail());
	if(duplicateEmail!=null){
		//response.data is error
		//response.status is 406 NOT_ACCEPTABLE
		ErrorClazz error=new ErrorClazz(3,"Email address already exists.. please enter different email address");
		return new ResponseEntity<ErrorClazz>(error,HttpStatus.NOT_ACCEPTABLE);
	}
	userDao.registerUser(user);
	//response.data is user
	//response.status is 200 OK
	return new ResponseEntity<User>(user,HttpStatus.OK);
	}catch(Exception e){
		//response.data is error
		//response.status is 406 NOT_ACCEPTABLE
		ErrorClazz error=new ErrorClazz(1,"Unable to register user details " + e.getMessage());
		return new ResponseEntity<ErrorClazz>(error,HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

@RequestMapping(value="/login",method=RequestMethod.POST)
//HttpRequest Body :
//{"username":"adam","password":"123","firstname":"","lastname:"","email":"","online":false}
public ResponseEntity<?> login(@RequestBody User user,HttpSession session){
    User validuser=userDao.login(user);
    if(validuser==null){//invalid credentials
        ErrorClazz error=new ErrorClazz(4,"Invalid username/password.. please enter valid username/pwd");
        return new ResponseEntity<ErrorClazz>(error,HttpStatus.UNAUTHORIZED);
    }
    validuser.setOnline(true);
    userDao.update(validuser);//update only the online status from 0 to 1
   session.setAttribute("username", validuser.getUsername());
    //HttpResponse Body:
    //{"username":"adam","password":"123","firstname":"Adam","lastname:"Eve","email":"a.e@abc.com","online":true}
    return new ResponseEntity<User>(validuser,HttpStatus.OK);
}


@RequestMapping(value="/logout",method=RequestMethod.GET)
public ResponseEntity<?> logout(HttpSession session){
	if(session.getAttribute("username")==null){
		ErrorClazz error=new ErrorClazz(5,"UnAuthorized User");
		return new ResponseEntity<ErrorClazz>(error,HttpStatus.UNAUTHORIZED);
	}
	String username=(String)session.getAttribute("username");
	User user=userDao.getUserByUsername(username);
	user.setOnline(false);
	userDao.update(user);
	session.removeAttribute("username");
	session.invalidate();
	return new ResponseEntity<Void>(HttpStatus.OK);
}
}
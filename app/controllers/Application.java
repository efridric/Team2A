package controllers;


import java.util.Arrays;
import java.util.List;

import models.User;
import play.*;
import play.data.*;
import static play.data.Form.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	public static class Login {

        public String email;
        public String password;
        
        public String validate() {
        	if(User.authenticate(email, password) == null){
        		return "Invalid username or password";
        	}
        	return null;
        }

	} 
	
	public static class SignUp {
	    
	    public String email;
	    public String firstName;
	    public String lastName;
	    public String password;
	}
	
    public static Result login() {
        return ok(
        	login.render(
        	    form(Login.class),
        	    form(SignUp.class)
        	)
        );
    }
          
    public static Result authenticate() {
    	Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
    	if(loginForm.hasErrors()){
    		return badRequest(login.render(loginForm, form(SignUp.class)));
    	}
    	else{
    		session().clear();
    		session("email", loginForm.get().email);
    	}
    	return ok();
    }
    
    public static Result signUp(){
        Form<SignUp> signUpForm = Form.form(SignUp.class).bindFromRequest();
        if(signUpForm.hasErrors()){
            return badRequest(login.render(form(Login.class), signUpForm));
        }
        else{
            session().clear();
            session("email", signUpForm.get().email);
        }
        return ok();
    }
  

}

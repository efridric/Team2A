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

	public static class SignIn {

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
	
    public static Result home() {
		return ok(
        	home.render(
        	    form(SignIn.class),
        	    form(SignUp.class)
        	)
        );
    }
          
    public static Result signInOrSignUp() {
    	if(request().body().asFormUrlEncoded().get("action").equals("signIn")){
	    	Form<SignIn> signInForm = Form.form(SignIn.class).bindFromRequest();
	    	if(signInForm.hasErrors()){
	    		return badRequest(home.render(signInForm, form(SignUp.class)));
	    	}
	    	else{
	    		session().clear();
	    		session("email", signInForm.get().email);
	    	}
	    	return ok();
    	}
    	else{
    		Form<SignUp> signUpForm = Form.form(SignUp.class).bindFromRequest();
    		if(signUpForm.hasErrors()){
    			return badRequest(home.render(form(SignIn.class), signUpForm));
    		}
    		else{
    			session().clear();
                session("email", signUpForm.get().email);
    		}
    		return ok();
    	}
    }
}

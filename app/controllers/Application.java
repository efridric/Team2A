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
	    public String passwordVerify;
	    
	    public String validate(){
	    	if(!password.equals(passwordVerify)){
	    		return "Passwords do not match";
	    	}
	    	return null;
	    }
	}
	
    public static Result index() {
		return ok(
        	index.render(
        	    form(SignIn.class),
        	    form(SignUp.class)
        	)
        );
    }
          
    public static Result signInOrSignUp() {
    	System.out.println(request().body().asFormUrlEncoded().get("action").toString());
    	if((request().body().asFormUrlEncoded().get("action"))[0].equals("signIn")){
	    	Form<SignIn> signInForm = Form.form(SignIn.class).bindFromRequest();
	    	if(signInForm.hasErrors()){
	    		return badRequest(index.render(signInForm, form(SignUp.class)));
	    	}
	    	else{
	    		session().clear();
	    		session("email", signInForm.get().email);
	    	}
	    	return redirect(routes.Dashboard.home());
    	}
    	else{
    		Form<SignUp> signUpForm = Form.form(SignUp.class).bindFromRequest();
    		if(signUpForm.hasErrors()){
    			return badRequest(index.render(form(SignIn.class), signUpForm));
    		}
    		else{
    			User user = new User(
    							signUpForm.get().email,
    							signUpForm.get().firstName,
    							signUpForm.get().lastName,
    							signUpForm.get().password    							
    						);
    			user.save();
    			session().clear();
                session("email", signUpForm.get().email);
    		}
    		return redirect(routes.Dashboard.home());
    	}
    }
    
    public static Result signOut() {
    	session().clear();
    	return ok(
            	index.render(
            	    form(SignIn.class),
            	    form(SignUp.class)
            	)
        );
    }
    
    public static Result editAccount(){
        String[] action = request().body().asFormUrlEncoded().get("action");
        if(action != null && action[0].equals("save")){
            
        }
        return ok(
                editAccount.render(
                   User.find.where().eq("email", session("email")).findUnique(), 
                   form(SignUp.class)
                )
        );
    }
}

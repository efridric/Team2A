package models;

import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;

@Entity 
public class User extends Model {
	
	@Id //email is the id
	public String email;
	public String firstName;
	public String lastName;
	public String password;
	
	public User(String email, String firstName, String lastName, String password){
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
	}
	
	public static Finder<String, User> find = new Finder<String,User>(
			String.class, User.class
	);
	
	public static User authenticate(String email, String password){
		return find.where().eq("email", email)
				.eq("password", password).findUnique();
	}
}


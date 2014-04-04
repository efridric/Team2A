package models;

import javax.persistence.*;
import javax.validation.Constraint;

import org.apache.commons.codec.binary.Base64;
import org.mindrot.jbcrypt.BCrypt;

import play.data.validation.Constraints.MaxLength;
import play.db.ebean.*;

import com.avaje.ebean.*;

@Entity 
public class User extends Model {
	
	@Id
	public Long id;
	public String email;
	public String firstName;
	public String lastName;
	@javax.persistence.Column(length=60)
	public String password;
	public String moodleLogin;
	public String moodlePassword;
	
	public User(String email, String firstName, String lastName, String password){
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = encryptPassword(password);
		System.out.println(this.password);
	}
	
	public static Finder<Long, User> find = new Finder<Long,User>(
			Long.class, User.class
	);
	
	public static User authenticate(String email, String password){
		User user = User.find.where().eq("email", email).findUnique();
		if(BCrypt.checkpw(password, user.password)){
			return user;
		}
		return null;
	}
	
	public static String encryptPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}
	
	public static String encodeMoodle(String password) {
		byte[] bytesEncoded = Base64.encodeBase64(password.getBytes());
		return new String(bytesEncoded);
	}
	
	public static String decodeMoodle(User user) {
		byte[] valueDecoded= Base64.decodeBase64(user.moodlePassword);
		return new String(valueDecoded);
	}
	
}


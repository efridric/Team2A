package models;

import java.util.List;

import models.*;

import org.junit.*;

import com.avaje.ebean.Ebean;

import static org.junit.Assert.*;
import play.libs.Yaml;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ModelsTest extends WithApplication {
	@Before
	public void setUp(){
		start(fakeApplication(inMemoryDatabase()));
		//Ebean.save((List) Yaml.load("test-data.yml"));
	}


/******************************** User Model Tests **********************************/
	
	@Test
	public void findUser(){
		assertNotNull(User.find.where().eq("email", "test@test.com").findUnique());
	}
	
	@Test
	public void createAndUpdate(){
		new User("tester@test.com", "Mr.", "Tester", "test").save();
		User test = User.find.where().eq("email", "tester@test.com").findUnique();
		assertNotNull(test);
		test.firstName = "first";
		test.lastName = "last";
		test.save();
		test.email = "update@test.com";
		test.save();
		assertEquals("update@test.com", test.email);
		assertEquals("first", test.firstName);
		assertEquals("last", test.lastName);
	}
	
/******************************** Task Model Tests **********************************/
	
}

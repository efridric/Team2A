package models;

import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class ModelsTest extends WithApplication {
	@Before
	public void setUp(){
		start(fakeApplication(inMemoryDatabase()));
	}
	
	@Test
	public void createAndRetriveUser() {
		new User("bob@test.com", "Bob", "Tester", "Testpass").save();
		User bob = User.find.where().eq("email", "bob@test.com").findUnique();
		assertNotNull(bob);
		assertEquals("Bob", bob.firstName);
	}
	
	@Test
	public void tryAuthenticateUser() {
		new User("bob@test.com", "Bob", "Tester", "Testpass").save();
		assertNotNull(User.authenticate("bob@test.com", "Testpass"));
		assertNull(User.authenticate("bob@test.com", "badpassword"));
        assertNull(User.authenticate("tom@gmail.com", "Testpass"));
		
	}
	
	@Test
	public void createAndUpdate(){
		new User("test@test.com", "Mr.", "Tester", "test").save();
		User test = User.find.where().eq("email", "test@test.com").findUnique();
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
}

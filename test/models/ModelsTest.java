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
		Ebean.save((List) Yaml.load("test-data.yml"));
	}

/******************************** General Tests *************************************/
	@Test
	public void generalTest(){
		//Count number of users
		assertEquals(1, User.find.findRowCount());
		assertEquals(1, Task.find.findRowCount());
	}

/******************************** User Model Tests **********************************/
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
		new User("tester@test.com", "Mr.", "Tester", "test").save();
		User test = User.find.where().eq("email", "tester@test.com").findUnique();
		assertNotNull(test);
		test.email = "update@test.com";
		test.save();
		assertEquals("update@test.com", test.email);
	}
	
/******************************** Task Model Tests **********************************/
	
	@Test
	public void listTasks(){
		List<Task> testTasks = Task.listTasks("test@test.com");
		assertEquals("test task 1", testTasks.get(0).title);
	}
}

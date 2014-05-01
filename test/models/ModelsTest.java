package models;

import java.util.List;

import models.*;

import org.junit.*;

import com.avaje.ebean.Ebean;
import org.apache.commons.codec.binary.Base64;
import org.mindrot.jbcrypt.BCrypt;

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
	public void createFindAndUpdateUser(){
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
	
	@Test
	public void auhenticateUser(){
		new User("update@test.com", "Mr.", "Tester", "test").save();
		User test1 = User.authenticate("update@test.com" , "test");
		assertNotNull(test1);
	}
	
	@Test
	public void encodeDecodeMoodle(){
		new User("student@uncc.edu", "fName", "lName", "password").save();
		User student = User.find.where().eq("email", "student@uncc.edu").findUnique();
		assertNotNull(student);
		student.moodlePassword = User.encodeMoodle("password");	
		assertEquals(User.decodeMoodle(student), "password");
		
	}
	
	@Test
	public void deleteUser(){
		new User("student@uncc.edu", "fName", "lName", "password").save();
		User student = User.find.where().eq("email", "student@uncc.edu").findUnique();
		student.delete();
		assertNull(User.find.where().eq("email", "student@uncc.edu").findUnique());

		
	}

	
	
/******************************** Task Model Tests **********************************/
		
	@Test
	public void createUpdateAndDeleteTask(){
	    new User("test@test.com", "test", "tester", "password").save();
		User u1 = User.find.where().eq("email", "test@test.com").findUnique();
		Task t = new Task();
		t.title = "title";
        Task.create(t, u1.id); //Creates the task
		List<Task> tasks = Task.listTasks(u1.id); //Finds the task
		assertEquals(tasks.get(0).title, "title"); //Asserts the task was found
		tasks.get(0).title = "new title"; //updates the title
		tasks.get(0).save(); //saves the new title
		tasks = Task.listTasks(u1.id);
		assertEquals(tasks.get(0).title, "new title"); //Checks that the title is updated
		tasks.get(0).delete(); //deletes the task
		assertEquals(Task.listTasks(u1.id).size(), 0); //Checks that the task is deleted
	}
}
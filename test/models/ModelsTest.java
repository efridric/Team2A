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
	public void findUser(){
		assertNotNull(User.find.where().eq("email", "test@test.com").findUnique());
	}
	
	@Test
	public void createAndUpdateUser(){
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
		new User("update@test.com", "Mr.", "Tester", "test");
		User test1 = User.authenticate("update@test.com" , "test");
		assertNotNull(test1);
	}
	
	@Test
	public void encodeDecodeMoodle(){
		new User("student@uncc.edu", "fName", "lName", "password").save();
		User student = User.find.where().eq("email", "student@uncc.edu").findUnique();
		assertNotNull(student);
		student.moodlePassword = student.encodeMoodle("password");	
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
	public void findAndUpdateTask(){
		User u1 = User.find.where().eq("email", "test@test.com").findUnique();
		List<Task> tasks = Task.listTasks(u1.id);
		assertEquals(tasks.get(0).title, "test task 1");
		assertEquals(tasks.get(1).ownerId, u1.id);
		tasks.get(0).title = "updated";
		assertEquals(tasks.get(0).title, "updated");
		assertEquals(tasks.get(1).ownerId, u1.id);
		
	}
	
	@Test
	public void createAndDeleteTask(){
		User u1 = User.find.where().eq("email", "test@test.com").findUnique();
		u1.id = 44L;
		Task t = new Task();
		Task t1 = new Task();
		t.title = "title";
		t1.title = "title2";
        Task.create(t, u1.id);
		List<Task> tasks = Task.listTasks(u1.id);
		assertEquals(tasks.get(0).title, "title");
		assertEquals(tasks.get(0).ownerId, u1.id);
		tasks.get(0).delete();
		assertNotEquals(tasks.get(0).title, "title");

		
	}
	
	
	

/******************************** Commitment Model Tests **********************************/

//	@Test
//	public void findCommitment(){
//		User u1 = User.find.where().eq("email", "test@test.com").findUnique();
//		List<Commitment> commitments = Commitment.listCommitments(u1.id);
//		assertEquals(commitments.get(0).title, "test");
//		assertEquals(commitments.get(0).owner, u1);
//	}
}
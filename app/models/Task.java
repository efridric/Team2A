package models;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import javax.persistence.*;

import play.db.ebean.*;

import com.avaje.ebean.*;

@Entity 
public class Task extends Model {
	
	@Id
	public Long id;
	public String title;
	public boolean isComplete = false;
	public Date dueDate;
	@ManyToOne
	public Long ownerId;
	public String source;
	public Time effort;
	public int prority;
	
	public static Finder<Long,Task> find = new Finder<Long, Task>(Long.class, Task.class);
	
	public static List<Task> listTasks(Long id){
		return find.where()
				.eq("ownerId", id)
				.findList();
	}
	
	public static Task create(Task task, Long id){
		task.ownerId = User.find.ref(id).id;
		task.save();
		return task;
	}
}

package models;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import play.db.ebean.*;

import com.avaje.ebean.*;

@Entity 
public class Task extends Model {
	
	@Id
	public Long id;
	public String title;
	public String description;
	public String category;
	public int isComplete = 0;
	public Long end = null;
	public Long start = null;
	@ManyToOne
	public Long ownerId;
	public String source;
	public Time effort;
	public int priority;
	public String url;
	
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

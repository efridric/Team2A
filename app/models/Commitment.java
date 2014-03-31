package models;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import javax.persistence.*;

import play.db.ebean.*;

import com.avaje.ebean.*;

@Entity 
public class Commitment extends Model {
	
	@Id
	public Long id;
	public String title;
	public Date date;
	public boolean repeating = false;
	@ManyToOne
	public User owner;
	public String source;
	public Time duration;
	
	public static Finder<Long,Commitment> find = new Finder<Long, Commitment>(Long.class, Commitment.class);
	
	public static List<Commitment> listCommitments(Long id){
		return find.fetch("owner").where()
				.eq("owner.id", id)
				.findList();
	}
	
	public static Commitment create(Commitment commitment, Long id){
		commitment.owner = User.find.ref(id);
		commitment.save();
		return commitment;
	}
}

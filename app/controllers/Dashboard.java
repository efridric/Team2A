package controllers;

import java.util.List;

import models.Task;
import models.User;
import play.*;
import play.data.*;
import play.db.ebean.Model.Finder;
import static play.data.Form.*;
import play.mvc.*;
import views.html.*;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Dashboard extends Controller {
		
	public static Result home(){
		String email = session("email");
		User user = User.find.where().eq("email", email).findUnique();
		return ok(
			dashboard.render(
					user,
					Task.listTasks(user.id)
			)
		);
	}
	
	public static Result getEvents(){
		String email = session("email");
		User user = User.find.where().eq("email", email).findUnique();
		List<Task> tasks = Task.listTasks(user.id);
		ObjectNode result = Json.newObject();
		result.put("success", 1);
		result.put("result", Json.toJson(tasks));
		return ok(result);
	}
}

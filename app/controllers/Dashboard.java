package controllers;

import java.util.List;

import models.Task;
import models.User;
import play.*;
import play.data.*;
import static play.data.Form.*;
import play.mvc.*;
import views.html.*;

public class Dashboard extends Controller {
	public static Result home(){
		String email = session("email");
		return ok(
			dashboard.render(
					User.find.where().eq("email", email).findUnique(),
					Task.listTasks(email)
			)
		);
	}
}

package controllers;

import models.User;
import play.*;
import play.data.*;
import static play.data.Form.*;
import play.mvc.*;
import views.html.*;

public class Tasks extends Controller {
	public static Result home(){
		return ok(
			afterLogin.render(
					User.find.where().eq("email", session("email")).findUnique()
			)
		);
	}
}
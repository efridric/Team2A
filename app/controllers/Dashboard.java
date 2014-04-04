package controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import org.apache.commons.io.IOUtils;

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
	
    public static Result getMoodleTasks(){
    	URL url = null;
    	InputStream moodleTasks = null;
    	Calendar calendar = null;
        try {
            url = new URL("https://moodle2.uncc.edu/calendar/export_execute.php?userid=25082&authtoken=771b8877ddd978a54fabf8a919323d6a03345e2b&preset_what=all&preset_time=weeknow");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    	try {
    	    moodleTasks = IOUtils.toBufferedInputStream(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    	CalendarBuilder builder = new CalendarBuilder();	
    	try {
            calendar = builder.build(moodleTasks);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }
    	
    	for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
    	    Component component = (Component) i.next();
    	    System.out.println("Component [" + component.getName() + "]");

    	    for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
    	        Property property = (Property) j.next();
    	        System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
    	    }
    	}
    	
    	return redirect(routes.Dashboard.home());
    }
}

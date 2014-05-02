package controllers;

import helpers.MoodleScraper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.persistence.ManyToOne;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.*;
import org.apache.http.message.BasicNameValuePair;

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

import controllers.Application.SignUp;


public class Dashboard extends Controller {
	
	public static class AddCredentials {

        public String username;
        public String password;
        public String vpassword;
        
        public String validate() {
        	if(!password.equals(vpassword)){
	    		return "Passwords do not match";
	    	}
        	return null;
        }
	}
	
	public static class TaskForm {
	    public Long id;
	    public String title;
	    public String description;
	    public String category;
	    public String isComplete= "0";
	    public String end = null;
	    public String start = null;
	    public Long ownerId;
	    public String source;
	    public String effort;
	    public String priority;
	}

	    
	public static Result home() throws Exception{
		String email = session("email");
		User user = User.find.where().eq("email", email).findUnique();
		Timestamp updateTime = new Timestamp(new java.util.Date().getTime() - 10*60*1000);
		if(updateTime.after(user.lastUpdate))
		    getMoodleTasks();
		return ok(
			dashboard.render(
					user,
					Task.listTasks(user.id)
			)
		);
	}
	
    public static Result addTask(){
        return ok(
                taskView.render(
                   User.find.where().eq("email", session("email")).findUnique(), 
                   new Task(),
                   form(TaskForm.class)
                )
        );
    }
    
    public static Result updateTask(){
    	User user = User.find.where().eq("email", session("email")).findUnique();
    	if((request().body().asFormUrlEncoded().get("action"))[0].equals("delete")){
    		return redirect(routes.Dashboard.home());
    	}
    	else{
	    	Form<TaskForm> updateTaskForm = Form.form(TaskForm.class).bindFromRequest();
	    	Task task = new Task();
	    	task.title = updateTaskForm.get().title;
	    	task.description = updateTaskForm.get().description;
	    	task.category = updateTaskForm.get().category;
	    	task.isComplete = Integer.parseInt(updateTaskForm.get().isComplete);
	    	task.start = stringToTimestamp(updateTaskForm.get().start);
	    	task.end = stringToTimestamp(updateTaskForm.get().end);
	    	task.ownerId = user.id;
	    	task.source = updateTaskForm.get().source;
	    	task.effort = null;
	    	task.priority = Integer.parseInt(updateTaskForm.get().priority);
	    	
			if(updateTaskForm.hasErrors()){
				return badRequest(taskView.render(
						 				user,
						 				task,
						 				updateTaskForm
								  ));
			}
			else{
				Task.create(task, user.id);
				return redirect(routes.Dashboard.home());			
			}
    	}
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
	
	public static Result addCredentials(){
		String email = session("email");
		return ok(
				addCredentials.render(
						User.find.where().eq("email", email).findUnique(),
						form(AddCredentials.class)
				)
		);
	}
	
    public static Result saveCredentials(){
    	User user = User.find.where().eq("email", session("email")).findUnique();
    	Form<AddCredentials> addCredentialsForm = Form.form(AddCredentials.class).bindFromRequest();
		if(addCredentialsForm.hasErrors()){
			return badRequest(addCredentials.render(
									user,
									addCredentialsForm
							  ));
		}
		else{
			user.moodleLogin = addCredentialsForm.get().username;
			user.moodlePassword = User.encodeMoodle(addCredentialsForm.get().password);
	    	user.save();
	    	return redirect(routes.Dashboard.getMoodleTasks());

    	}
    }
    	
    public static Result getMoodleTasks() throws Exception{
        
    	User user = User.find.where().eq("email", session("email")).findUnique();
    	
    	if(user.moodleLogin == null){
    		return redirect(routes.Dashboard.addCredentials());
    	}

    	
        String moodleUrl = "https://moodle2.uncc.edu/login/index.php";
        String calendarExport = "https://moodle2.uncc.edu/calendar/export.php";
        String host= "moodle2.uncc.edu";
     
        MoodleScraper http = new MoodleScraper();
     
        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());
     
        //Send a "GET" request, so that you can extract the form's data.
        String page = http.GetPageContent(moodleUrl);
        String postParams = http.getFormParams(page, user.moodleLogin, User.decodeMoodle(user));
     
        //Construct above post's content and then send a POST request for
        // authentication
        http.sendPost(moodleUrl, postParams);
     
        //success then go to calendar export page
        String result = http.GetPageContent(calendarExport);
        
        //get userid and authtoken from moodle
        Hashtable<String,String> moodleParams = http.getCalUrlParams(result);
        
        URL url = null;
    	InputStream moodleTasks = null;
    	Calendar calendar = null;
    	
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", moodleParams.get("userid")));
        params.add(new BasicNameValuePair("authtoken", moodleParams.get("authtoken")));
        params.add(new BasicNameValuePair("preset_what", "all"));
        params.add(new BasicNameValuePair("preset_time", "recentupcoming"));
        @SuppressWarnings("deprecation")
		URI uri = URIUtils.createURI("https", host, -1, "/calendar/export_execute.php", URLEncodedUtils.format(params, "UTF-8"), null);
        url = uri.toURL();
                
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
    	
    	for (@SuppressWarnings("rawtypes")
		Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
    	    Timestamp t = null;
    	    Component component = (Component) i.next();
    	    System.out.println("Component [" + component.getName() + "]");
    	    Task task = new Task();
    	    for (@SuppressWarnings("rawtypes")
			Iterator j = component.getProperties().iterator(); j.hasNext();) {
    	        Property property = (Property) j.next();
    	        String p = property.getName().toLowerCase();
    	        String v = property.getValue();
    	        
    	        if(v.length() > 254)
    	           v = v.substring(0, 254);
    	        if(p.equals("summary"))
    	        	task.title = v;
    	        if(p.equals("description"))
    	        	task.description = v;
        	    if(p.equals("dtstart")){
        	    	String d = v;
        	    	String year = d.substring(0, 4);
        	    	String month = d.substring(4, 6);
        	    	String day = d.substring(6,8);
        	    	String hour = d.substring(9,11);
        	    	String minute = d.substring(11, 13);
        	    	String second = d.substring(13,15);
        	    	System.out.println(year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second+".0");
        	    	task.end = Timestamp.valueOf(year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second+".0");
        	    	task.start = new Timestamp(task.end.getTime() - 60 * 60 * 1000);
        	    }
        	    if(p.equals("last-modified")){
        	        String d = v;
                    String year = d.substring(0, 4);
                    String month = d.substring(4, 6);
                    String day = d.substring(6,8);
                    String hour = d.substring(9,11);
                    String minute = d.substring(11, 13);
                    String second = d.substring(13,15);
                    t = Timestamp.valueOf(year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second+".0");
        	    }
        	    if(p.equals("categories"))
        	    	task.category = property.getValue();


    	        
    	        System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
    	    }
    	    
    	    if(t == null || user.lastUpdate == null)
    	        Task.create(task, user.id);
    	    else if(t.after(user.lastUpdate))
    	        Task.create(task, user.id);
    	}
    	
    	
    	user.lastUpdate = new Timestamp(new java.util.Date().getTime());
    	user.save();
    	return redirect(routes.Dashboard.home());
    }
    
    public static Timestamp stringToTimestamp(String d){
        if(d.length() == 18){
            d = d.substring(0, 11) + "0" + d.substring(11,18);
        }
        System.out.println(d);
        String year = d.substring(6, 10);
        String month = d.substring(0, 2);
        String day = d.substring(3, 5);
        String hour = d.substring(11,13);
        if(hour.equals("12"))
            hour = "00";
        if(d.substring(17,19).equals("PM"))
            hour = Integer.toString((Integer.parseInt(hour) + 12)); 
        String minute = d.substring(14,16);
        String second = "00";
        System.out.println(year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second+".0");
        return Timestamp.valueOf(year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second+".0");
    }
}

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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

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


public class Dashboard extends Controller {
	
    List<String> cookies;
    static HttpsURLConnection conn;
    
    final String USER_AGENT = "Mozilla/5.0";
    
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
	
    public static Result getMoodleTasks() throws Exception{
        
        String moodleUrl = "https://moodle2.uncc.edu/login/index.php";
        String calendarExport = "https://moodle2.uncc.edu/calendar/export.php";
        String host= "moodle2.uncc.edu";
     
        MoodleScraper http = new MoodleScraper();
     
        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());
     
        // 1. Send a "GET" request, so that you can extract the form's data.
        String page = http.GetPageContent(moodleUrl);
        String postParams = http.getFormParams(page, "", "");
     
        // 2. Construct above post's content and then send a POST request for
        // authentication
        http.sendPost(moodleUrl, postParams);
     
        // 3. success then go to calendar export page
        String result = http.GetPageContent(calendarExport);
        
        // 4. get userid and authtoken from moodle
        Hashtable<String,String> moodleParams = http.getCalUrlParams(result);
        
        URL url = null;
    	InputStream moodleTasks = null;
    	Calendar calendar = null;
    	
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userid", moodleParams.get("userid")));
        params.add(new BasicNameValuePair("authtoken", moodleParams.get("authtoken")));
        params.add(new BasicNameValuePair("preset_what", "all"));
        params.add(new BasicNameValuePair("preset_time", "recentupcoming"));
        URI uri = URIUtils.createURI("https", host, -1, "/calendar/export_execute.php", URLEncodedUtils.format(params, "UTF-8"), null);
        
        url = uri.toURL();
        
        
		//url = new URL("https://moodle2.uncc.edu/calendar/export_execute.php?userid=25082&authtoken=771b8877ddd978a54fabf8a919323d6a03345e2b&preset_what=all&preset_time=weeknow");
        
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

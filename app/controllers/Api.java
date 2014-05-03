package controllers;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import models.Task;
import models.User;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class Api extends Controller{
	
	public static Result getTasks(String email, String password) throws JAXBException{
		//read email and password from request params
		//serve all tasks for that user back in xml format
		User user = User.authenticate(email, password);
		if(user == null){
			return ok("Invalid Username or Password");
		}
		
		List<Task> tasks = Task.listTasks(user.id);
		
		String xml = "<tasks>";
		
		for(Task task: tasks){
			String end = "";
			String start = "";
			String effort = "";
			if(task.end != null)
				end = task.end.toString();
			if(task.start != null)
				start = task.start.toString();
			if(task.effort != null)
				effort = task.effort.toString();
			
			xml += "<task>";
			xml += "<id>"+task.id+"</id>";
			xml += "<title>"+task.title+"</title>";
			xml += "<description>"+task.description+"</description>";
			xml += "<category>"+task.category+"</category>";
			xml += "<isComplete>"+task.isComplete+"</isComplete>";
			xml += "<end>"+end+"</end>";
			xml += "<start>"+start+"</start>";
			xml += "<ownerId>"+task.ownerId+"</ownerId>";
			xml += "<source>"+task.source+"</source>";
			xml += "<effort>"+effort+"</effort>";
			xml += "<priority>"+task.priority+"</priority>";
			xml += "</task>";
		}

		
		xml += "</tasks>";
		
		return ok(xml);
	}
}

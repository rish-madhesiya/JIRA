import java.io.IOException;

import jxl.read.biff.BiffException;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;


public class FunctReportJsonParser 
{
	public static int parseJson(String s) throws JSONException{
	      
	      Object obj=JSONValue.parse("[0,"+s+"]");
	      JSONArray array=(JSONArray)obj;
	                    
	      JSONObject json=(JSONObject)array.get(1);
	      //System.out.println(obj2.get("issues")); 
	      int count=Integer.parseInt(""+json.get("total"));
	      return count;
	}
	
	public static int returnJSON(String url) throws JSONException
	{
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("rishab.madhesiya", "jira11958"));
		//String url="https://infoedge.atlassian.net/rest/api/2/search?jql=project%3D%22Naukri%20India%20Resdex%22%20and%20Sprint%3D1179%20and%20type%3DStory%20and%20(assignee%3Drishab.madhesiya%20or%20assignee%3Dprerna.sharma%20or%20assignee%3Dhimanshu.pushkar)";              
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		if (response.getStatus() != 200) {
			String str="Failed";
			JSONObject json=new JSONObject();
			System.out.println(str);
			return -1;
		}
		else
		{
		    String str=response.getEntity(String.class);
		    //System.out.println(str);
		    int count= parseJson(str);
		    return count;
		}	
	}
	
}

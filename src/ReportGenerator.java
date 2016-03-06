

//import net.sf.json.JSONObject;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import sun.rmi.transport.LiveRef;

import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Issue;
import com.naukri.datatable.Xls_Reader;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import jxl.read.biff.BiffException;

public class ReportGenerator 
{
static Xls_Reader datatable;
static Xls_Reader1 datatable1;
public static float buildQuality;
public static float buildQualityWithoutLiveIssues;
public static float TotalLiveImpact=0;
public static float TotalFunc=0,TotalUI=0,TotalInteg=0,TotalInsufficient=0,TotalIncompl=0,TotalImplicit=0,TotalValid=0;
static PrintStream fw;
public static void parseJson(String s,float TotalLiveImpact) throws JSONException, BiffException, IOException
{
	      int j=0;
	      String s1;
	      float relStoryPoints=0;
	      float totalStoryPoints=0;
	      Object obj=JSONValue.parse("[0,"+s+"]");
	      JSONArray array=(JSONArray)obj;
	                    
	      JSONObject json=(JSONObject)array.get(1);
	      ////(obj2.get("issues")); 
	      int count=Integer.parseInt(""+json.get("total"));
	      float[] story=new float[count];
	      s=""+json.get("issues");
	      
	      obj=JSONValue.parse(s);
	      array=(JSONArray)obj;
	      for(int i=0;i<count;i++)
	      {
	      json=(JSONObject)array.get(i);
	      float[] data=BuildQuality.calculateBuild((""+json.get("key")));
	      float total_bq=data[0];
	      TotalFunc=TotalFunc+data[1];
	      TotalInteg=TotalInteg+data[2];
	      TotalUI=TotalUI+data[3];
	      TotalIncompl=TotalIncompl+data[4];
	      TotalInsufficient=TotalInsufficient+data[5];
	      TotalImplicit=TotalImplicit+data[6];
	      TotalValid=TotalValid+data[7];
	      s=""+json.get("fields");
	      String sub=s.substring(s.indexOf("customfield_10004"), s.indexOf("customfield_10400"));
	      sub=sub.replaceAll("[a-z]+[/_]+[0-9]+", "");
	      sub=sub.replaceAll("[/:+\"+/,+.0]", "");
	      
	      int storyPoints;
	      if(sub.equals("null"))
	    	  continue;
	      if(sub.equals(""))
	    	  continue;
	      else
	    	  storyPoints=Integer.parseInt(sub);
	      story[j++]=100-((total_bq/storyPoints)*100);
	      if(story[j-1]<0)
	    	  story[j-1]=0;
	      relStoryPoints=relStoryPoints+(story[j-1]*storyPoints);
	      
	      totalStoryPoints=totalStoryPoints+storyPoints;
	      if(i==(count-1))
	          break;
	      continue;
	      
	      }
	      fw.println("---------------------------------Bug Counts---------------------------------");
			
			fw.println("\nTotalUI:"+TotalUI);
			fw.println("\nTotalIntegrationBugs:"+TotalInteg);
			fw.println("\nTotalImplicitBugs:"+TotalImplicit);
			fw.println("\nTotalInsufficientBugs:"+TotalInsufficient);
			fw.println("\nTotalIncompleteBugs:"+TotalIncompl);
			fw.println("\nTotalValidationsBugs:"+TotalValid);
	fw.println("TotalFuncBugs:"+TotalFunc);
	//("Relative Story Points:"+relStoryPoints);
	//("Total Story Points:"+totalStoryPoints);
	buildQualityWithoutLiveIssues=relStoryPoints/totalStoryPoints;
	buildQuality=(relStoryPoints/totalStoryPoints)-TotalLiveImpact;
}
	
public static int returnCount(String str) throws BiffException, JSONException, IOException
{
	Object obj=JSONValue.parse("[0,"+str+"]");
    JSONArray array=(JSONArray)obj;
                
    JSONObject json=(JSONObject)array.get(1);
    ////(obj2.get("issues")); 
    int count=Integer.parseInt(""+json.get("total"));
    
    
 return count;
}
	public static String returnJSON(String url) throws JSONException, BiffException, IOException
	{
		Client client = Client.create();
		client.addFilter(new HTTPBasicAuthFilter("rishab.madhesiya", "jira11958"));              
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		if (response.getStatus() != 200) {
		   // return "Error: " + response.getStatus();
			String str="Failed";
			JSONObject json=new JSONObject();
		}
		else
		{
		    String str=response.getEntity(String.class);
		    return str;
		   
		   
		
		}
		return "";	
	}
	
	
	public static void main(String args[]) throws JSONException, BiffException, IOException, ParseException
	{
		
		for(int m=13;m<=13;m++)
		{	
			Vector dataHolder = Xls_Reader1.ReadCSV("Assignee.xls");
			String SheetName=Xls_Reader1.CellData(dataHolder,m,3);
			//String SheetName="Resdex";
			
			String ProjectId=Xls_Reader1.CellData(dataHolder, m, 0);
			fw=new PrintStream(new File(ProjectId+".txt"));
			String Assignee=Xls_Reader1.CellData(dataHolder,1,1);
			String sprint=Xls_Reader1.CellData(dataHolder,m,2)+"-104";
			
		String url="https://infoedge.atlassian.net/rest/api/2/search?jql=project%3D%22"+ProjectId+"%22%20and%20Sprint%3D"+sprint+"%20and%20type%20in%20(Improvement%2CStory)%20and%20assignee%20in%20%20"+Assignee+"%20and%20status%20in%20(Verified%2CClosed)%20and%20(labels%20!%3D%20Automation%20OR%20labels%20is%20EMPTY)";
		System.out.println(url);
		VelocityGenerator.getMetheVelocity(url,ProjectId);	
		String URL_GH=getSprint(returnJSON(url));
		String StateOfSprint=getStateOfSprint(returnJSON(url));
		////(StateOfSprint);
		if(StateOfSprint.equals("CLOSED"))
		{
		////(url);
		/*****For sprint start and end dates*******/
		
		String []dates=getStartAndEndDate(returnJSON(URL_GH));
		/*****************************************************/
		String json=returnJSON(url);
		String fileName = "BuildQualityParam.xls";
        Vector dataHolder1 = Xls_Reader1.ReadCSV(fileName);
        
		int i=returnCount(getClientIssue(ProjectId, 0,dates));//client
		int j=returnCount(getClientIssue(ProjectId, 1,dates));//data
		int k=returnCount(getClientIssue(ProjectId, 2,dates));//live
		int issuesExceptClient=k-(i+j);
		TotalLiveImpact=(float) ((TotalLiveImpact+(j*0.5))+((i+issuesExceptClient)*2));
		fw.println("Total Client Issues (Code fix):"+i);
		fw.println("Total Client Issues (Data fix):"+j);
		fw.println("Total Live issues(others):"+issuesExceptClient);
		
		
		parseJson(json,TotalLiveImpact);
		final String IterationReport = "IterationReport.xls";
		/* Gets Sprint ID */
		
		String sprintIdJIRA=getSprint(returnJSON(url));
		////(sprintIdJIRA);
		
		String totalstories_URL="https://infoedge.atlassian.net/rest/api/2/search?jql=project="+ProjectId+"%20and%20(type=story%20or%20type=Improvement)%20and%20(assignee%20in%20"+ Assignee+")%20and%20(status=closed%20or%20status=verified)%20and%20sprint="+sprint+"";              
		String automatedstories_URL="https://infoedge.atlassian.net/rest/api/2/search?jql=project="+ProjectId+"%20and%20(type=story%20or%20type=Improvement)%20and%20(assignee%20in%20"+ Assignee+")%20and%20(status=closed%20or%20status=verified)%20and%20sprint="+sprint+"%20and%20automated=yes";              
		String notautomatablestories_URL ="https://infoedge.atlassian.net/rest/api/2/search?jql=project="+ProjectId+"%20and%20(type=story%20or%20type=Improvement)%20and%20(assignee%20in%20"+ Assignee + ")%20and%20(status=closed%20or%20status=verified)%20and%20sprint="+sprint+"%20and%20automated=%27Not%20Automatable%27";               
		int totalstories = FunctReportJsonParser.returnJSON(totalstories_URL);
		int storiesautomated = FunctReportJsonParser.returnJSON(automatedstories_URL);
		int storiesnotautomatable = FunctReportJsonParser.returnJSON(notautomatablestories_URL);
		////(totalstories_URL);
		fw.println("---------------------------------Functional Tests---------------------------------");
		fw.println("totalStories:"+totalstories+"\n");
		////(automatedstories_URL);
		////(notautomatablestories_URL);
		int automatablestories = (totalstories-storiesnotautomatable);
		fw.println("automatables:"+automatablestories+"\n");
		int notAutomatedstories = (totalstories-(storiesautomated + storiesnotautomatable));
		fw.println("notAutomatedStories:"+notAutomatedstories+"\n");
		fw.println("---------------------------------Build Quality---------------------------------");
		//("BuildQuality:"+buildQuality);
		fw.println("BuildQuality:"+buildQuality);
		fw.println("Build Quality (old): " +buildQualityWithoutLiveIssues);
		fw.close();
		WriteToExcel(SheetName,sprint,issuesExceptClient,i,j,totalstories,automatablestories,storiesautomated,notAutomatedstories,storiesnotautomatable);
		
		}
		else
		{
		//(ProjectId+"not closed");	
		}
		}	
	
	//Executes the macro
		/*Runtime rt = Runtime.getRuntime();
        try {
           rt.exec(new String[]{"cmd.exe","/c","cscript test.vbs"});

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
		//("Report Generated Successfully! Please check \"Iteration Report.xls\" in your workspace");
	}

	


	public static String getClientIssue(String ProjectId,int i,String []dates) throws BiffException, JSONException, IOException
	{
		
		
        
		//query=project=NIR and labels in (clientIssue) and createdDate>="2016/01/12" and createdDate<="2016/01/25" and labels not in (datafix) and resolution not in("Won't Fix",Invalid)
		String URLForClient="https://infoedge.atlassian.net/rest/api/2/search?jql=project%3D"+ProjectId+"%20and%20labels%20in%20(clientIssue)%20and%20createdDate%3E%3D%22"+dates[0]+"%22%20and%20createdDate%3C%3D%22"+dates[1]+"%22%20and%20labels%20not%20in%20(datafix)%20and%20(resolution%20not%20in%20(%22Won%27t%20Fix%22%2CDuplicate%2CInvalid)%20OR%20resolution%20%3D%20Unresolved)%20";
		////(URLForClient);
		String URLForClientDataFix="https://infoedge.atlassian.net/rest/api/2/search?jql=project%3D"+ProjectId+"%20and%20labels%20in%20(datafix)%20and%20labels%3Ddatafix%20and%20createdDate%3E%3D%22"+dates[0]+"%22%20and%20createdDate%3C%3D%22"+dates[1]+"%22%20and%20(resolution%20not%20in%20(%22Won%27t%20Fix%22%2CDuplicate%2CInvalid)%20OR%20resolution%20%3D%20Unresolved)%20";
		////(URLForClientDataFix);
		String URLForCompleteLive="https://infoedge.atlassian.net/rest/api/2/search?jql=project%3D%22"+ProjectId+"%22%20and%20labels%3Dlive%20and%20createdDate%3E%3D%22"+dates[0]+"%22%20and%20createdDate%3C%3D%22"+dates[1]+"%22%20and%20(resolution%20not%20in%20(%22Won%27t%20Fix%22%2CDuplicate%2CInvalid)%20OR%20resolution%20%3D%20Unresolved)%20";
		//("URL for The live issues:"+URLForCompleteLive);
		String[] CompleteURL={URLForClient,URLForClientDataFix,URLForCompleteLive};
		String json=returnJSON(CompleteURL[i]);
		return json;
	}


public static void WriteToExcel(String SheetName,String sprint, int issuesExceptClient,int i,int j,int totalstories,int automatablestories,int storiesautomated,int notAutomatedstories,int storiesnotautomatable) throws IOException
{
	
	

	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 56, 1,""+ issuesExceptClient);
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 56, 2,""+ i);//codefix
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 56, 3,""+ j);//datafix
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 37, 1,""+ totalstories);
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 37, 2,""+ automatablestories);
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 37, 3,""+ storiesautomated);
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 37, 4,""+ notAutomatedstories);
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 37, 5,""+ storiesnotautomatable);
	Xls_Reader1.SetCellDataString("IterationReport.xls", SheetName, 7, 0, sprint);
	Xls_Reader1.SetCellDataString("IterationReport.xls", SheetName, 56, 0, sprint);
	Xls_Reader1.SetCellDataString("IterationReport.xls", SheetName, 37, 0, sprint);
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 7, 1,""+ buildQuality);
	 
	//datatable.setCellData(SheetName, 1, 7,""+buildQuality);
	//Xls_Reader1.DeleteRow("IterationReport.xls", SheetName);
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 7, 2,""+ TotalFunc);
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 7, 3,""+ TotalInteg);
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 7, 4,""+ TotalUI);
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 7, 5,""+ TotalIncompl);
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 7, 6,""+ TotalInsufficient);
	Xls_Reader1.SetCellData("IterationReport.xls", SheetName, 7, 7,""+ TotalImplicit);
	
    
}

/******************Gets the velocity URL********************************/
public static String[] getVelocityReportURL(String jsonText)
{
	   String s="[0,"+jsonText+"]";
	      Object obj=JSONValue.parse(s);
	      
	      JSONArray array=(JSONArray)obj;
	      
	      //(array);            
	      JSONObject json=(JSONObject)array.get(1);
	      //(json);
	      s=""+json.get("issues");
	      obj=JSONValue.parse(s);
	      array=(JSONArray)obj;
	      json=(JSONObject)array.get(0);
	      s=""+json.get("fields");
	      String sub=s.substring(s.indexOf("customfield_10007"), s.indexOf("customfield_10008"));
	      ////(sub);
	      
	      String rapidView=sub.substring(sub.indexOf("rapidViewId"), sub.indexOf(",",sub.indexOf("rapidViewId")));
	      rapidView=rapidView.replaceAll("rapidViewId=", "");
	      sub=sub.substring(sub.indexOf("id"), sub.indexOf(","));
	      sub=sub.replaceAll("id=", "");
	      String VelReport="https://infoedge.atlassian.net/rest/greenhopper/latest/rapid/charts/velocity/?rapidViewId="+rapidView;
	      String []Report={VelReport,sub};
	      return Report;
}


/******Gets the Greenhopper URL ******/ 
public static String getSprint(String jsonText)
{
    String s="[0,"+jsonText+"]";
      Object obj=JSONValue.parse(s);
      
      JSONArray array=(JSONArray)obj;
                    
      JSONObject json=(JSONObject)array.get(1);
   
      s=""+json.get("issues");
      obj=JSONValue.parse(s);
      array=(JSONArray)obj;
      json=(JSONObject)array.get(0);
      JSONObject s2=(JSONObject)json.get("fields");
      //String sub=s.substring(s.indexOf("customfield_10007"), s.indexOf("customfield_10008"));
      ////(sub);
      JSONArray a=((JSONArray)s2.get("customfield_10007"));
      int i=a.size();
      String sub=""+a.get(i-1);
      String rapidView=sub.substring(sub.indexOf("rapidViewId"), sub.indexOf(",",sub.indexOf("rapidViewId")));
      rapidView=rapidView.replaceAll("rapidViewId=", "");
      sub=sub.substring(sub.indexOf("id"), sub.indexOf(","));
      sub=sub.replaceAll("id=", "");
      String URLGreenhopper="https://infoedge.atlassian.net/rest/greenhopper/latest/rapid/charts/sprintreport/?rapidViewId="+rapidView+"&sprintId="+sub;
      return URLGreenhopper;
}




public static String getStateOfSprint(String jsonText)
{
	String s="[0,"+jsonText+"]";
    Object obj=JSONValue.parse(s);
    JSONArray array=(JSONArray)obj;
                  
    JSONObject json=(JSONObject)array.get(1);
    s=""+json.get("issues");
    obj=JSONValue.parse(s);
    ////(obj);
    array=(JSONArray)obj;
    json=(JSONObject)array.get(0);
    s=""+json.get("fields");
    String sub=s.substring(s.indexOf("customfield_10007"), s.indexOf("customfield_10008"));
    String StateOfSprint=sub.substring(sub.indexOf("state="), sub.indexOf(",",sub.indexOf("state=")));
    StateOfSprint=StateOfSprint.substring(StateOfSprint.indexOf("=")+1);
    ////(StateOfSprint);
    return StateOfSprint;
}
/*********Returns the start and end date of the sprint**************/
public static String[] getStartAndEndDate(String jsonData)
{
	
	String s="[0,"+jsonData+"]";
    Object obj=JSONValue.parse(s);
    JSONArray array=(JSONArray)obj;
    JSONObject json=(JSONObject)array.get(1);
   
    ////(json);
    JSONObject info=(JSONObject) json.get("sprint");
    String endDate=""+info.get("endDate");
    endDate=endDate.replaceAll("/", "-");
	java.util.Date date = new Date(endDate);
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	String formattedEndDate = formatter.format(date);
	formattedEndDate=formattedEndDate.replaceAll("-", "%2F");
	//("end date of this iteration:"+formattedEndDate);	
	String startDate=""+info.get("startDate");
    startDate=startDate.replaceAll("/", "-");
	java.util.Date date2 = new Date(startDate);
	String formattedStartDate = formatter.format(date2);
	formattedStartDate=formattedStartDate.replaceAll("-", "%2F");
	//("start date of this iteration:"+formattedStartDate);	
	String []dates={formattedStartDate,formattedEndDate};
	return dates;
}
}
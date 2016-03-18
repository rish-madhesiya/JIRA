//import java.sql.Date;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import jxl.read.biff.BiffException;

import org.apache.commons.collections4.ListUtils;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.naukri.datatable.Xls_Reader;

public class VelocityGenerator 
{

static Xls_Reader datatable;
	static PrintStream fw;
	
	public static void getMetheVelocity(String url,String ProjectId) throws BiffException, JSONException, IOException, ParseException
{
	
		datatable=new Xls_Reader("Assignee.xls");
	fw=new PrintStream(new File(ProjectId+"Velocity"+".txt"));
	int i=0; 
	String jsonText=ReportGenerator.returnJSON(url);
	//(url);
	String GH_URL=ReportGenerator.getSprint();
	
	System.out.println(GH_URL);
	String jsonData=ReportGenerator.returnJSON(GH_URL);
	String s="[0,"+jsonData+"]";
    Object obj=JSONValue.parse(s);
    JSONArray array=(JSONArray)obj;
    JSONObject json=(JSONObject)array.get(1);
    JSONObject json2=(JSONObject) json.get("contents");
    JSONObject json3=(JSONObject) json2.get("issueKeysAddedDuringSprint");
   // //(json3.keySet());
    Set<String> StoriesAddedAfterStart=json3.keySet();
    
    JSONArray NewVelocity=(JSONArray) json2.get("completedIssues");
    int length=NewVelocity.size();
    
    
    String []Stories=StoriesAddedAfterStart.toArray(new String[StoriesAddedAfterStart.size()]);
    
   
   //*********************For getting the commitment for the iteration**********************************//*
   float totalCommitment=0;
   String []data=ReportGenerator.getVelocityReportURL(); //data[] has URL + the sprintID
   //("----url----");
   //(data[0]);
   String jsonFromVel=ReportGenerator.returnJSON(data[0]);
   String strWithVel="[0,"+jsonFromVel+"]";
   Object objWithVel=JSONValue.parse(strWithVel);
   JSONArray arrayWithVel=(JSONArray)objWithVel;
   JSONObject jsonWithVel=(JSONObject)arrayWithVel.get(1);
   //("yeahhhhh!!!!!!!");
   //(jsonWithVel);
   JSONObject jsonWithVel2=(JSONObject)jsonWithVel.get("velocityStatEntries");
   //("------Check Here-------");
   //(jsonWithVel2);
   JSONObject jsonWithVel3=(JSONObject) jsonWithVel2.get(data[1]);
   JSONObject jsonWithVel4=(JSONObject) jsonWithVel3.get("estimated");
   String TotalCommitment= (String)jsonWithVel4.get("text");
   
   totalCommitment=Float.parseFloat(TotalCommitment);
   
   //("The Final Commitment according to jira: "+TotalCommitment);
    //("The Final Commitment according to jira: "+TotalCommitment);
    /*****************For completed Issues*********************/
    JSONObject json5=(JSONObject) json.get("contents");
    
    String str2=""+ json5.get("completedIssues");
    
    obj=JSONValue.parse(str2);
    JSONArray array2=(JSONArray)(json5.get("completedIssues"));
    String []completedIssues=new String[array2.size()];
    for(int j=0;j<(array2.size());j++)
    {
    json=(JSONObject)array2.get(j);
    
    completedIssues[j]=(""+json.get("key"));
    }
   
    /***********For punted Issues***********************/
    JSONArray array3=(JSONArray)(json5.get("puntedIssues"));
    String []puntedIssues=new String[array3.size()];
    for(int j=0;j<(array3.size());j++)
    {
    json=(JSONObject)array3.get(j);
    
    puntedIssues[j]=(""+json.get("key"));
    //(puntedIssues[j]);
    }
    List puntedIssuesList=Arrays.asList(puntedIssues);
    
    /**********************For not completed Issues**************************/
   
    array3=(JSONArray)(json5.get("issuesNotCompletedInCurrentSprint"));
    String []NotcompletedIssues=new String[array3.size()];
    for(int j=0;j<(array3.size());j++)
    {
    json=(JSONObject)array3.get(j);
    
    NotcompletedIssues[j]=(""+json.get("key"));
    }
    /************************************************/
    
    /****************For getting the completed Total Sum**************************/
    System.out.println(json5);
    json5=(JSONObject)json5.get("completedIssuesEstimateSum");
    String totalCompleted=""+json5.get("value");
    float TotalEstimateCompleted=Float.parseFloat(totalCompleted);
    
    
    
    
    List NotCompletedTotal=Arrays.asList(NotcompletedIssues);
    List Unplanned=Arrays.asList(Stories);
    List puntedIssuesPlanned=ListUtils.subtract(puntedIssuesList,Unplanned);
    List NotCompletedPlanned=ListUtils.subtract(NotCompletedTotal,Unplanned);
    //("unplanned"+Unplanned);
    List total=ListUtils.sum(Arrays.asList(completedIssues),Arrays.asList(NotcompletedIssues));
    total=ListUtils.sum(total,puntedIssuesList);
    List diff = ListUtils.subtract(total, Unplanned);
    List CompletedPlanned=ListUtils.subtract(diff, NotCompletedPlanned);
    CompletedPlanned=ListUtils.subtract(CompletedPlanned, puntedIssuesList);
    float totalPointsCompleted=0;
    float totalLive=0;
    float totalUnplannedCompleted=0;
    /*//("The length is: "+length );
    for(int x=0;x<length;x++)
    {	
    
    	Iterator it=CompletedPlanned.iterator();
    	JSONObject ForVel=(JSONObject) NewVelocity.get(x);
    	
    	while(it.hasNext())
    	{
    	String FinalObj=""+ForVel.get("key");
    	FinalObj.replaceAll("\"", "");
    	String id=""+it.next();
    	
    	String estimate=""+((JSONObject)(((JSONObject) ForVel.get("estimateStatistic")).get("statFieldValue"))).get("value");
    	//comparison for getting planned completed
    	if(FinalObj.contains(id))
    	{
    		
    		
    		if(!estimate.equals("null"))
    			{
    			totalPointsCompleted=totalPointsCompleted+Float.parseFloat(estimate);
    			}
String urlForlabelsearch="https://infoedge.atlassian.net/rest/api/latest/issue/"+FinalObj;
    		
    		*//**********For getting the completed planned live***************//*
    		String jsonForlabel=ReportGenerator.returnJSON(urlForlabelsearch);
    		String sForlabel="[0,"+jsonForlabel+"]";
    	    Object obj1=JSONValue.parse(sForlabel);
    	    JSONArray arrayForLabel=(JSONArray)obj1;
    	    JSONObject jsonforlabel=(JSONObject)arrayForLabel.get(1);
    	    JSONArray Newarray=(JSONArray)((JSONObject)jsonforlabel.get("fields")).get("labels");
    		int len=Newarray.size();
    		for(int n=0;n<len;n++)
    		{
    			String j=(String)Newarray.get(n);
    			j=j.replaceAll("\"", "");
    			if(j.contains("madelive"))
    			{
    				if(!estimate.equals("null"))
        			{
        			totalLive=totalLive+Float.parseFloat(estimate);
        			}	
    			}
    		}
    	    *//****************************************************************//*
    		
    	}
    	else  //gets the unplanned completed
    	{
    		
    		if(!estimate.equals("null"))
			{
    			//("ye dekho"+totalUnplannedCompleted);
    			totalUnplannedCompleted=totalUnplannedCompleted+Float.parseFloat(estimate);
    			continue;
			}
    	}
    	}//end of while
    }//end of for
   
    //("The live points:"+totalLive);
    //("The Total planned +completed is (actual):"+totalPointsCompleted);
    //("The total Unplanned Completed is:"+totalUnplannedCompleted);
*/    String PlannedCompleted=CompletedPlanned.toString();
    String PlannedStories=diff.toString();
    String UnplannedStories=Unplanned.toString();
    PlannedCompleted=PlannedCompleted.replace("[", "");
    PlannedCompleted=PlannedCompleted.replace("]", "");
    PlannedCompleted=PlannedCompleted.replaceAll(",", "%2C");
    PlannedCompleted=PlannedCompleted.replaceAll(" ", "");
    
    PlannedStories=PlannedStories.replace("[", "");
    PlannedStories=PlannedStories.replace("]", "");
    PlannedStories=PlannedStories.replaceAll(",", "%2C");
    PlannedStories=PlannedStories.replaceAll(" ", "");
    
    UnplannedStories=UnplannedStories.replace("[", "");
    UnplannedStories=UnplannedStories.replace("]", "");
    UnplannedStories=UnplannedStories.replaceAll(",", "%2C");
    UnplannedStories=UnplannedStories.replaceAll(" ", "");
    
    
    /*******************For getting the completed planned total***************************/
    int totalStoryPlannedCompleted=0;
    String URLForPlanned="https://infoedge.atlassian.net/rest/api/2/search/?jql=id%20in%20("+PlannedCompleted+")%20AND%20status%20in(Verified%2CClosed)";
    System.out.println("    yeeeeeee           "+PlannedCompleted);
    String jsonForPlanned=ReportGenerator.returnJSON(URLForPlanned);
	String ForPlanned="[0,"+jsonForPlanned+"]";
    Object PlannedStories1=JSONValue.parse(ForPlanned);
    JSONArray arrayForPlanned=(JSONArray)PlannedStories1;
    JSONObject jsonPlanned=(JSONObject)arrayForPlanned.get(1);
    s=""+jsonPlanned.get("issues");
    Object PlannedStories_obj=JSONValue.parse(s);
    int count=Integer.parseInt(""+jsonPlanned.get("total"));
    arrayForPlanned=(JSONArray)PlannedStories_obj;
    
    for(int m=0;m<count;m++)
    {
	json=(JSONObject)arrayForPlanned.get(m);
    s=""+json.get("fields");
    JSONObject s1=(JSONObject)json.get("fields");
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
    String j=""+s1.get("labels");
    j=j.replaceAll("\"", "");
	
    if(j.contains("madelive"))
	{
    	System.out.println("in if");
    	totalLive=totalLive+storyPoints;
			
	}
    
    totalStoryPlannedCompleted=totalStoryPlannedCompleted+storyPoints;
  
}

Xls_Reader1.SetCellData("IterationReport.xls", ProjectId, 78, 5, ""+totalStoryPlannedCompleted);

fw.println("final live:"+totalLive);
fw.println("Total Story Planned and completed:"+totalStoryPlannedCompleted);

float unplannedComp=TotalEstimateCompleted-totalStoryPlannedCompleted;
if(unplannedComp<0)
	unplannedComp=0;

fw.println("The unplanned completed is :"+unplannedComp);


/******************For getting the total planned story points***********************/
    int totalStoryPlanned=0;
    String URLForOnlyPlanned="https://infoedge.atlassian.net/rest/api/2/search/?jql=id%20in%20("+PlannedStories+")";
    System.out.println("Planned Stories"+PlannedStories);
    String jsonForOnlyPlanned=ReportGenerator.returnJSON(URLForOnlyPlanned);
	String ForOnlyPlanned="[0,"+jsonForOnlyPlanned+"]";
    Object PlannedStories2=JSONValue.parse(ForOnlyPlanned);
    JSONArray arrayForOnlyPlanned=(JSONArray)PlannedStories2;
    JSONObject jsonOnlyPlanned=(JSONObject)arrayForOnlyPlanned.get(1);
    s=""+jsonOnlyPlanned.get("issues");
    PlannedStories_obj=JSONValue.parse(s);
    count=Integer.parseInt(""+jsonOnlyPlanned.get("total"));
    arrayForOnlyPlanned=(JSONArray)PlannedStories_obj;
    System.out.println(arrayForOnlyPlanned);
    for(int m=0;m<count;m++)
    {
	json=(JSONObject)arrayForOnlyPlanned.get(m);
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
  	  {
    	storyPoints=Integer.parseInt(sub);
  	    totalStoryPlanned=totalStoryPlanned+storyPoints;
  	    String str=""+json.get("key");
  	    System.out.println(m+":"+str+"  " +storyPoints);
  	  }
  
}

 fw.println("The total Story points planned:"+totalStoryPlanned);
    Xls_Reader1.SetCellData("IterationReport.xls", ProjectId, 77, 5, ""+totalStoryPlanned);
    /************************For getting completed unplanned*****************************/
    
    int totalStoryUnplannedCompleted=0;
    String URLForUnplanned="https://infoedge.atlassian.net/rest/api/2/search/?jql=id%20in%20("+UnplannedStories+")%20AND%20status%20in(Verified%2CClosed)";
    
    String jsonForUnplanned=ReportGenerator.returnJSON(URLForUnplanned);
	String ForUnplanned="[0,"+jsonForUnplanned+"]";
    PlannedStories1=JSONValue.parse(ForUnplanned);
    JSONArray arrayForUnplanned=(JSONArray)PlannedStories1;
    JSONObject jsonUnplanned=(JSONObject)arrayForUnplanned.get(1);
    s=""+jsonUnplanned.get("issues");
    PlannedStories_obj=JSONValue.parse(s);
    count=Integer.parseInt(""+jsonUnplanned.get("total"));
    arrayForUnplanned=(JSONArray)PlannedStories_obj;
    
    for(int m=0;m<count;m++)
    {
	json=(JSONObject)arrayForUnplanned.get(m);
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
    totalStoryUnplannedCompleted=totalStoryUnplannedCompleted+storyPoints;
  
}
    
  
    
    
   
    Xls_Reader1.SetCellData("IterationReport.xls", ProjectId, 79, 5, ""+totalStoryUnplannedCompleted);
 
        fw.close();
}
}

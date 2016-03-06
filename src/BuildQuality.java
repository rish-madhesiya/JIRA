import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Vector;

import org.json.JSONException;

import jxl.*;
import jxl.read.biff.BiffException;

import com.naukri.datatable.*;

public class BuildQuality 
{
static Xls_Reader datatable;
public int Totalfunc=0,TotalInteg=0,TotalUI=0,TotalIncomplete=0,TotalInsufficient=0,TotalImplicit=0;	

public static float[] calculateBuild(String StoryId) throws BiffException, IOException, JSONException
	{
		
		
		System.out.println(StoryId);
		int func=0,Integ=0,UI=0,Incomplete=0,Insufficient=0,Implicit=0,Validations=0;
		Xls_Reader1 datatable1=new Xls_Reader1();
	    String fileName = "BuildQualityParam.xls";
        Vector dataHolder = Xls_Reader1.ReadCSV(fileName);
		int total=Xls_Reader1.countRows(dataHolder);
		String Severity,Reason,weightage,labels;
		ReportGenerator getCount=new ReportGenerator();
		float weights=0,weights2=0;
		float Quality=0;
        for(int i=1;i<(total-8);i++)
        {
        Severity=Xls_Reader1.CellData(dataHolder,i,0);
        
        Reason=Xls_Reader1.CellData(dataHolder,i,1);
        
        Reason=Reason.replaceAll(" ", "%20");
        
        
        weights=Float.parseFloat(Xls_Reader1.CellData(dataHolder,i,2));
        
			String URLForBQ="https://infoedge.atlassian.net/rest/api/2/search/?jql=issue%20in%20linkedIssues(%22"+StoryId+"%22)%20and%20type%3DBug%20AND%20Severity%3D"+Severity+"%20AND%20Reason%3D%27"+Reason+"%27%20and%20(resolution%20not%20in%20(%22Won%27t%20Fix%22%2CDuplicate%2CInvalid)%20OR%20resolution%20%3D%20Unresolved)%20and%20(labels%20not%20in%20(%22delayedfind%2Flive%22)or%20labels%3Dnull)";
			
			//System.out.println(URLForBQ);
			String json=ReportGenerator.returnJSON(URLForBQ);
			
						
			
			
			int count=ReportGenerator.returnCount(json);
			
			
			if(Reason.contains("Functional"))
			{
				func=count+func;
				if(count>0)
					System.out.println("Functional"+StoryId);
			}
			if(Reason.trim().equals("Integration/Environment/Configuration"))
			{
				Integ=Integ+count;
				if(count>0)
					System.out.println("Integ"+StoryId);
			}
			if(Reason.trim().equals("UI"))
			{	
				UI=count+UI;
				if(count>0)
					System.out.println("UI"+StoryId);
				//if(count>0)
					//System.out.println(StoryId);
				//System.out.println(StoryId);
			}
			if(Reason.trim().equals("Incomplete%20requirements"))
			{	
				Incomplete=count+Incomplete;
				if(count>0)
					System.out.println("Incomplete"+StoryId);
			}
			if(Reason.trim().equals("Insufficient%20Impact%20Analysis"))
			{	
				Insufficient=Insufficient+count;
				if(count>0)
					System.out.println("Insuff"+StoryId);
			}
			if(Reason.trim().equals("Implicit%20Requirements"))
			{
				
				Implicit=Implicit+count;
				if(count>0)
					System.out.println("Implicit"+StoryId);
				//if(count>0)
					//System.out.println(StoryId+":"+Severity);
			}
			if(Reason.trim().equals("Validations"))
				{
				Validations=Validations+count;
				}
			
			Quality=Quality+count*weights;
        }
        for(int m=33;m<total;m++)
		{
        	Reason=Xls_Reader1.CellData(dataHolder,m,1);
            Reason=Reason.replaceAll(" ", "%20");
        	labels=Xls_Reader1.CellData(dataHolder,m,0);
			labels=labels.replaceAll("/", "%2F");
			String URLForStagingBugs="https://infoedge.atlassian.net/rest/api/2/search/?jql=issue%20in%20linkedIssues(%22"+StoryId+"%22)%20and%20type%3DBug%20AND%20labels%3D%22"+labels+"%22%20AND%20Reason%3D%27"+Reason+"%27%20and%20(resolution%20not%20in%20(%22Won%27t%20Fix%22%2CDuplicate%2CInvalid)%20OR%20resolution%20%3D%20Unresolved)%20";
			System.out.println(URLForStagingBugs);
			String json2=ReportGenerator.returnJSON(URLForStagingBugs);
			System.out.println(json2);
			int countStaging=ReportGenerator.returnCount(json2);
			weights2=Float.parseFloat(Xls_Reader1.CellData(dataHolder,m,2));
			Quality=Quality+countStaging*weights2;
		}
		

        float[] data={Quality,func,Integ,UI,Incomplete,Insufficient,Implicit,Validations};
        
        return data;
	}
}

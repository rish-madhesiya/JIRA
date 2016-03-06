
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.naukri.datatable.Xls_Reader;



public class LeaderboardGenerator 
{
static Xls_Reader datatable=new Xls_Reader("Leaderboard.xls");
static HashMap<String, Float> hashMapOverAll = new HashMap<String, Float>();
static HashMap<String, Float> hashMapCurrent = new HashMap<String, Float>();
public static void main(String args[]) throws FileNotFoundException, IOException
{
	ShiftExcelColumns();
	writeAverageToExcel();
	PrintStream fw=new PrintStream(new File("Rankings.txt"));	
	int length=datatable.getRowCount("Sheet1");
    for(int i=2;i<=length;i++)
    {
	System.out.println(datatable.getCellData("Sheet1", "cumulative", 3));
	float cumulative=Float.parseFloat(datatable.getCellData("Sheet1", "cumulative", i));
	float total=Float.parseFloat(datatable.getCellData("Sheet1", "total", i));
	
	String TeamName=datatable.getCellData("Sheet1", "Teamname", i);
	hashMapOverAll.put(TeamName,cumulative );
	hashMapCurrent.put(TeamName, total);
    }
    Map<String, Float> treeMapoverAll = new TreeMap<String,Float>();
    treeMapoverAll.putAll(hashMapOverAll);
    System.out.println(treeMapoverAll);
    
    List<Map.Entry<String, Float>> entries = new ArrayList<Map.Entry<String, Float>>(hashMapOverAll.entrySet());
    Collections.sort(entries, new Comparator<Map.Entry<String, Float>>() {
    	  public int compare(Map.Entry<String, Float> e1, Map.Entry<String, Float> e2) {
    	    return (int)(e2.getValue() - e1.getValue()); // reverse order sort
    	  }
    	});


    Map<String, Float> m = new LinkedHashMap<String, Float>();
    for(Map.Entry<String, Float> e : entries)
      m.put(e.getKey(), e.getValue());
    fw.println("-------------Rankings-------------------");
    int i=1;
    for (Map.Entry me : m.entrySet()) 
    {
      System.out.println("key is " + me.getKey());
     
      fw.println("Rank "+i+" : "+ me.getKey());
      System.out.println(" value is " + me.getValue());
    i++;
    }
}
   
//This method shifts excel columns
public static void ShiftExcelColumns() throws FileNotFoundException, IOException
    {
    File excelFile = new File("Leaderboard.xls");
    POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(excelFile));
    HSSFWorkbook wb = new HSSFWorkbook(fs);
    HSSFSheet sheet = wb.getSheetAt(0);
    HSSFRow row;
    HSSFCell cell,cellNew;
    int colToRemove = 2;
    for(int i=1;i<=6;i++)
    {
    System.out.println("Current value of i:"+i);
    	Iterator rowIter = sheet.iterator();
    try
    {
    while (rowIter.hasNext()) 
    {
    row = (HSSFRow)rowIter.next();
    cell = row.getCell(i);
    row.removeCell(cell); 
    }
    }
    catch(IllegalArgumentException e)
    {}
    
    Iterator rowIter2 = sheet.iterator();
    try
    {
    while (rowIter2.hasNext()) 
    {
    
    row = (HSSFRow)rowIter2.next();
    cellNew=row.getCell(i+1);
    short x=(short)i;
    row.moveCell(cellNew, x); 
    }
    System.out.println("not in while");
    }
    catch(IllegalArgumentException e)
    {}
    }
    FileOutputStream fileOut = new FileOutputStream(excelFile);
    wb.write(fileOut);
    }

public static void writeAverageToExcel()
{
	float avg=0;
	float sum;
	for(int i=2;i<=datatable.getRowCount("Sheet1");i++)
	{
		sum=0;
		for(int j=1;j<=6;j++)
		{
			sum=sum+Float.parseFloat(datatable.getCellData("Sheet1", j, i));
		}
		avg=sum/6;
		datatable.setCellData("Sheet1",9 , i, ""+avg);
	}

}

}
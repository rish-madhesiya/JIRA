import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Xls_Reader1 {

    public static void main(String[] args) {

        String fileName = "BuildQualityParam.xls";
        Vector dataHolder = ReadCSV(fileName);
        String cell=CellData(dataHolder,0,1);
        System.out.println(cell);
    }

    public static Vector ReadCSV(String fileName) {
        Vector cellVectorHolder = new Vector();

        try {
            FileInputStream myInput = new FileInputStream(fileName);

            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            
            Iterator rowIter = mySheet.rowIterator();

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                Vector cellStoreVector = new Vector();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    cellStoreVector.addElement(myCell);
                }
                cellVectorHolder.addElement(cellStoreVector);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cellVectorHolder;
    }
    public static void InsertRow(String path)
    {
    	
    	Vector cellVectorHolder = new Vector();

        try {
            FileInputStream myInput = new FileInputStream(path);

            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            mySheet.createRow(29);
            
        }
catch(Exception e){}
    }
    public static int countRows(Vector dataHolder){
    	int count=0;
    	count=dataHolder.size();
    	return count;
    }

    public static String CellData(Vector dataHolder,int i,int j) {

        /*for (int i = 0; i < dataHolder.size(); i++) {
            Vector cellStoreVector = (Vector) dataHolder.elementAt(i);
            for (int j = 0; j < cellStoreVector.size(); j++) {
                HSSFCell myCell = (HSSFCell) cellStoreVector.elementAt(j);
                String stringCellValue = myCell.toString();
                System.out.print(stringCellValue + "\t");
            }
            }*/
        
            String cell=((HSSFCell)((Vector) dataHolder.elementAt(i)).elementAt(j)).toString();
            return cell;
    }
   

    
    
    
    public static void DeleteRow(String path,String sheetname)
{
	try
	{
	FileInputStream myInput = new FileInputStream(path);
	//POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
	XSSFWorkbook myWorkBook = new XSSFWorkbook(myInput);
	XSSFSheet mySheet = myWorkBook.getSheetAt(1);
    mySheet.shiftRows(3, 3, -1);
	/*Iterator rowIter = mySheet.rowIterator();
    HSSFRow row=mySheet.getRow(2);
    mySheet.removeRow(row);*/
}
catch(Exception e)
{
	System.out.println("Inside Catch");
}

}
public static boolean SetCellData(String path,String sheetName,int rowNum,int colNum,String data)  
{
	try{
			FileInputStream fis = new FileInputStream(path);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);

			if(rowNum<=0)
				return false;

			int index = workbook.getSheetIndex(sheetName);
			//int colNum=-1;
			if(index==-1)
				return false;


			HSSFSheet sheet = workbook.getSheetAt(index);


			//row=sheet.getRow(0);
			//for(int i=0;i<row.getLastCellNum();i++){

			//if(row.getCell(i).getStringCellValue().trim().equals(colName))
			//colNum=i;
			//}

			if(colNum==-1)
				return false;

			sheet.autoSizeColumn(colNum);
			HSSFRow row = sheet.getRow(rowNum-1);
			if (row == null)
				row = sheet.createRow(rowNum-1);

			HSSFCell cell = row.getCell(colNum);
			if (cell == null)
				cell = row.createCell(colNum);

			HSSFCellStyle style = (HSSFCellStyle) workbook.createCellStyle();
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
            cell.setCellStyle(style);
            cell.setCellValue(Float.parseFloat(data));

			//cell.setCellValue(Float.parseFloat(data));

			FileOutputStream fileOut = new FileOutputStream(path);

			workbook.write(fileOut);

			fileOut.close();

		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
public static boolean SetCellDataString(String path,String sheetName,int rowNum,int colNum,String data)  
{
	try{
			FileInputStream fis = new FileInputStream(path);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);

			if(rowNum<=0)
				return false;

			int index = workbook.getSheetIndex(sheetName);
			//int colNum=-1;
			if(index==-1)
				return false;


			HSSFSheet sheet = workbook.getSheetAt(index);


			//row=sheet.getRow(0);
			//for(int i=0;i<row.getLastCellNum();i++){

			//if(row.getCell(i).getStringCellValue().trim().equals(colName))
			//colNum=i;
			//}

			if(colNum==-1)
				return false;

			sheet.autoSizeColumn(colNum);
			HSSFRow row = sheet.getRow(rowNum-1);
			if (row == null)
				row = sheet.createRow(rowNum-1);

			HSSFCell cell = row.getCell(colNum);
			if (cell == null)
				cell = row.createCell(colNum);

			HSSFCellStyle style = (HSSFCellStyle) workbook.createCellStyle();
            //style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
            //cell.setCellStyle(style);
            cell.setCellValue(data);

			//cell.setCellValue(Float.parseFloat(data));

			FileOutputStream fileOut = new FileOutputStream(path);

			workbook.write(fileOut);

			fileOut.close();

		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	


}

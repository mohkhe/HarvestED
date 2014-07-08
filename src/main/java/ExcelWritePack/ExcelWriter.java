package ExcelWritePack;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelWriter {

	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times;
	public WritableSheet excelSheet;
	public int column;
	public int row;
	WritableWorkbook workbook;

	/*
	 * public void setOutputFile(String inputFile) { this.inputFile = inputFile;
	 * }
	 */

	public void setColumn(int newColumn) {
		this.column = newColumn;
	}

	public void setRow(int newRow) {
		this.row = newRow;
	}

	public void setting(String inputFile, String sheetName) {
		File file = new File(inputFile);
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		try {
			if(file.exists()){
				Workbook tempWorkbook = Workbook.getWorkbook(file);
				if(tempWorkbook==null){
					workbook = Workbook.createWorkbook(file, wbSettings);
				}else{
					workbook = Workbook.createWorkbook(file, tempWorkbook);
				}
			}else{
				workbook = Workbook.createWorkbook(file, wbSettings);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			//e.printStackTrace();
			System.out.println("Biff Exception in Excel file. Created a new File");
			try {//if error in openning//nesfe baste bashim
				workbook = Workbook.createWorkbook(file, wbSettings);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		workbook.createSheet(sheetName, workbook.getNumberOfSheets());
		excelSheet = workbook.getSheet(workbook.getNumberOfSheets()-1);
		createLabel(excelSheet);// create timesFont
		// createLabel(excelSheet);
		// createContent(excelSheet);
		// workbook.write();
		//	addCaption(excelSheet, 0, 0, "Header 1");
	}
	
	public void writeSheetInFile(){
		try {
			workbook.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void addCaption(WritableSheet sheet, int column, int row, String s) {
		Label label;
		label = new Label(column, row, s, timesBoldUnderline);
		try {
			sheet.addCell(label);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	private void createLabel(WritableSheet sheet) {
		// Lets create a times font
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		// Define the cell format
		times = new WritableCellFormat(times10pt);
		// Lets automatically wrap the cells
		try {
			times.setWrap(true);
			// Create create a bold font with unterlines
			WritableFont times10ptBoldUnderline = new WritableFont(
					WritableFont.TIMES, 10, WritableFont.BOLD, false,
					UnderlineStyle.SINGLE);
			timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
			// Lets automatically wrap the cells
			timesBoldUnderline.setWrap(true);

			CellView cv = new CellView();
			cv.setFormat(times);
			cv.setFormat(timesBoldUnderline);
			cv.setAutosize(true);

		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public void addNumber(WritableSheet sheet, int column, int row, double value) {
		Number number;
		number = new Number(column, row, value, times);
		
		try {
			sheet.addCell(number);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	public void addLabel(WritableSheet sheet, int column, int row, String value) {
		Label label;
		label = new Label(column, row, value, times);
		
		try {
			sheet.addCell(label);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	public void closeExcel() {
		try {
			workbook.close();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

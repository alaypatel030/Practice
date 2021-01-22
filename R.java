package framework;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ExcelSheetToJsonConverter {

	public static void main(String[] args) {

		String dataSheetPath = System.getProperty("user.dir") + "\\src\\dataEngine\\demo.xls";

		JsonObject testData = ExcelSheetToJsonConverter.getExcelDataAsJsonObject(dataSheetPath);
		System.out.println(testData.toString());

	}

	public static JsonObject getExcelDataAsJsonObject(String dataSheetPath) {

		JsonObject sheetsJsonObject = new JsonObject();
		HSSFWorkbook workbook = null;
		FormulaEvaluator formulaEval = new HSSFFormulaEvaluator(workbook);

		try {

			FileInputStream fis = new FileInputStream(new File(dataSheetPath));
			workbook = new HSSFWorkbook(fis);

			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

				JsonArray sheetArray = new JsonArray();
				ArrayList<String> columnNames = new ArrayList<String>();
				HSSFSheet sheet = workbook.getSheetAt(i);
				Iterator<Row> sheetIterator = sheet.iterator();

				while (sheetIterator.hasNext()) {

					Row currentRow = sheetIterator.next();
					JsonObject jsonObject = new JsonObject();

					if (currentRow.getRowNum() != 0) {

						for (int j = 0; j < columnNames.size(); j++) {

							DataFormatter dF = new DataFormatter();

							if (currentRow.getCell(j) == null
									|| currentRow.getCell(j).getCellType() == CellType.BLANK) {
								jsonObject.addProperty(columnNames.get(j), "");
							} else {
								if (formulaEval.evaluate(currentRow.getCell(j)).getCellType() == CellType.ERROR) {
									System.out.println("Error in formula withing this cell! Error code : "
											+ currentRow.getCell(j).getErrorCellValue());
								}
							}
							jsonObject.addProperty(columnNames.get(j),
									dF.formatCellValue(currentRow.getCell(j), formulaEval));

						}

						sheetArray.add(jsonObject);

					} else {
						// store column names
						for (int k = 0; k < currentRow.getPhysicalNumberOfCells(); k++) {
							columnNames.add(currentRow.getCell(k).getStringCellValue());
						}
					}

				}

				sheetsJsonObject.add(workbook.getSheetName(i), sheetArray);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sheetsJsonObject;

	}

}

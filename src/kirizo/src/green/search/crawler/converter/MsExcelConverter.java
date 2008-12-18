package green.search.crawler.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class MsExcelConverter extends AbstractDocumentConverter implements
		DocumentConverter {

	public MsExcelConverter(File file) {
		super(file);
	}

	public String convert() {

		// 文字列に変換するためのバッファを用意
		StringBuffer stbf = new StringBuffer();
		try {
			// Excelファイルの読み込み
			FileInputStream fis = new FileInputStream(super.file);
			POIFSFileSystem fs = new POIFSFileSystem(fis);
			// ワークブック・オブジェクトの取得
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			// 総ワークシート数の取得
			int sheets = wb.getNumberOfSheets();
			// ワークシートごとに、データを取得
			for (int sheetIdx = 0; sheetIdx < sheets; sheetIdx++) {
				// ワークシートを表すオブジェクトの取得
				HSSFSheet sheet = wb.getSheetAt(sheetIdx);
				stbf.append(wb.getSheetName(sheetIdx) + CR);
				// ワークシート内にある行の最初と最後の行インデックスを取得
				int firstRow = sheet.getFirstRowNum();
				int lastRow = sheet.getLastRowNum();
				// 行ごとに、データを取得
				for (int rowIdx = firstRow; rowIdx <= lastRow; rowIdx++) {
					// 行を表すオブジェクトの取得
					HSSFRow row = sheet.getRow(rowIdx);
					// 行にデータが存在しない場合
					if (row == null)
						continue;

					// 行内の最初と最後のセルのインデックスを取得
					short firstCell = row.getFirstCellNum();
					short lastCell = row.getLastCellNum();

					HSSFCell cell = null;

					// セルごとに、データを取得
					for (short cellIdx = firstCell; cellIdx <= lastCell; cellIdx++) {

						// セルを表すオブジェクトの入手
						cell = row.getCell(cellIdx);

						// セルが空である場合
						if (cell == null)
							continue;
						if (cellIdx != firstCell)
							stbf.append(" ");
						// セルにあるデータの種類を取得
						int type = cell.getCellType();

						// データの種類ごとに、データを取得
						switch (type) {
						case HSSFCell.CELL_TYPE_BOOLEAN:
							boolean bdata = cell.getBooleanCellValue();
							stbf.append(String.valueOf(bdata));
							break;
						case HSSFCell.CELL_TYPE_NUMERIC:
							double ddata = cell.getNumericCellValue();
							stbf.append(String.valueOf(ddata));
							break;
						case HSSFCell.CELL_TYPE_STRING:
							stbf.append(cell.getRichStringCellValue());
							break;
						case HSSFCell.CELL_TYPE_BLANK:
						case HSSFCell.CELL_TYPE_ERROR:
						case HSSFCell.CELL_TYPE_FORMULA:
						default:
							continue;
						}
					}
					stbf.append(CR);
				}
			}
			fis.close();
		} catch (RuntimeException e) {
			e.printStackTrace(System.err);
			System.err.println("#####################" + super.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String restr = stbf.toString().trim();
		// 改行コードをとる
		restr = restr.replaceAll(CR + CR, "");
		return restr.replaceAll(CR, " ");
	}
}

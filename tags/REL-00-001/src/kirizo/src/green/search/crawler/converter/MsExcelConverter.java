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

		// ������ɕϊ����邽�߂̃o�b�t�@��p��
		StringBuffer stbf = new StringBuffer();
		try {
			// Excel�t�@�C���̓ǂݍ���
			FileInputStream fis = new FileInputStream(super.file);
			POIFSFileSystem fs = new POIFSFileSystem(fis);
			// ���[�N�u�b�N�E�I�u�W�F�N�g�̎擾
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			// �����[�N�V�[�g���̎擾
			int sheets = wb.getNumberOfSheets();
			// ���[�N�V�[�g���ƂɁA�f�[�^���擾
			for (int sheetIdx = 0; sheetIdx < sheets; sheetIdx++) {
				// ���[�N�V�[�g��\���I�u�W�F�N�g�̎擾
				HSSFSheet sheet = wb.getSheetAt(sheetIdx);
				stbf.append(wb.getSheetName(sheetIdx) + CR);
				// ���[�N�V�[�g���ɂ���s�̍ŏ��ƍŌ�̍s�C���f�b�N�X���擾
				int firstRow = sheet.getFirstRowNum();
				int lastRow = sheet.getLastRowNum();
				// �s���ƂɁA�f�[�^���擾
				for (int rowIdx = firstRow; rowIdx <= lastRow; rowIdx++) {
					// �s��\���I�u�W�F�N�g�̎擾
					HSSFRow row = sheet.getRow(rowIdx);
					// �s�Ƀf�[�^�����݂��Ȃ��ꍇ
					if (row == null)
						continue;

					// �s���̍ŏ��ƍŌ�̃Z���̃C���f�b�N�X���擾
					short firstCell = row.getFirstCellNum();
					short lastCell = row.getLastCellNum();

					HSSFCell cell = null;

					// �Z�����ƂɁA�f�[�^���擾
					for (short cellIdx = firstCell; cellIdx <= lastCell; cellIdx++) {

						// �Z����\���I�u�W�F�N�g�̓���
						cell = row.getCell(cellIdx);

						// �Z������ł���ꍇ
						if (cell == null)
							continue;
						if (cellIdx != firstCell)
							stbf.append(" ");
						// �Z���ɂ���f�[�^�̎�ނ��擾
						int type = cell.getCellType();

						// �f�[�^�̎�ނ��ƂɁA�f�[�^���擾
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
		// ���s�R�[�h���Ƃ�
		restr = restr.replaceAll(CR + CR, "");
		return restr.replaceAll(CR, " ");
	}
}

package green.search.crawler.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;

public class MsWordConverter extends AbstractDocumentConverter implements
		DocumentConverter {

	public MsWordConverter(File file) {
		super(file);
	}

	public String convert() {

		// 文字列に変換するためのバッファを用意
		StringBuffer stbf = new StringBuffer();
		try {
			FileInputStream fis = new FileInputStream(super.file);
			HWPFDocument doc = new HWPFDocument(fis);
			Range r = doc.getRange();
			for (int x = 0; x < r.numSections(); x++) {
				Section sec = r.getSection(x);
				for (int y = 0; y < sec.numParagraphs(); y++) {
					Paragraph para = sec.getParagraph(y);
					for (int z = 0; z < para.numCharacterRuns(); z++) {
						CharacterRun run = para.getCharacterRun(z);
						String line = run.text();
						stbf.append(line);
						stbf.append(CR);
					}
				}
			}
			fis.close();
		} catch (RuntimeException e) {
			e.printStackTrace(System.err);
			System.err.println("#####################" + super.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String restr = stbf.toString();
		return restr.replaceAll(CR, " ");
	}
}

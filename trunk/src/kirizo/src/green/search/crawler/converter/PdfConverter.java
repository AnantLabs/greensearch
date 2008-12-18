package green.search.crawler.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.encryption.AccessPermission;
import org.pdfbox.util.PDFTextStripper;

public class PdfConverter extends AbstractDocumentConverter implements
		DocumentConverter {

	public PdfConverter(File file) {
		super(file);
	}

	public String convert() {

		// 文字列に変換するためのバッファを用意
		StringBuffer stbf = new StringBuffer("");
		FileInputStream fis = null;
		PDDocument pdfDoc = null;

		try {

			fis = new FileInputStream(super.file);
			pdfDoc = PDDocument.load(fis);
			if (pdfDoc.isEncrypted()) {
				AccessPermission ap = pdfDoc.getCurrentAccessPermission();
				System.out.println("AccessPermission is " + ap);
				if (!ap.canExtractContent())
					return null;
				return null;
			}

			PDFTextStripper textStripper = new PDFTextStripper();
			String textInPDF = textStripper.getText(pdfDoc);

			stbf.append(textInPDF);

		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (pdfDoc != null)
					pdfDoc.close();
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String restr = stbf.toString();
		return restr.replaceAll(CR, " ");
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String paht = "Z:\\public_html\\techs\\トッパーズユーザマニュアル.pdf";
		PdfConverter cnv = new PdfConverter(new File(paht));
		System.out.println(cnv.convert());
	}
}

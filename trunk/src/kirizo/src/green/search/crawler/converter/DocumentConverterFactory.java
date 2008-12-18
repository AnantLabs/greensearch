package green.search.crawler.converter;

import green.search.crawler.fs.FsCrawlerFilter;

import java.io.File;

/**
 * �h�L�������g�ϊ��@�t�@�N�g���N���X�B
 * 
 * @author haruyosi
 */
public class DocumentConverterFactory {

	/**
	 * �t�@�C���^�C�v���ƂɃR���o�[�^�[�C���X�^���X��Ԃ��B
	 * 
	 * @param file
	 * @return
	 */
	public static DocumentConverter getInstance(File file) {

		DocumentConverter cv = null;
		if (FsCrawlerFilter.isMsExcelFile(file)) {
			cv = new MsExcelConverter(file);
		} else if (FsCrawlerFilter.isMsWordFile(file)) {
			cv = new MsWordConverter(file);
		} else if (FsCrawlerFilter.isMsPowerPoint(file)) {
			cv = new MsPowerPointConverter(file);
		} else if (FsCrawlerFilter.isPdf(file)) {
			cv = new PdfConverter(file);
		} else if (FsCrawlerFilter.isText(file)) {
			cv = new SimpleTextConverter(file);
		} else if (FsCrawlerFilter.isHtml(file)) {
			cv = new HtmlConverter(file);
		}
		return cv;
	}
}
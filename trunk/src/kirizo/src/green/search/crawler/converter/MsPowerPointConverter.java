package green.search.crawler.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextBox;
import org.apache.poi.hslf.usermodel.SlideShow;

public class MsPowerPointConverter extends AbstractDocumentConverter implements
		DocumentConverter {

	public MsPowerPointConverter(File file) {
		super(file);
	}

	public String convert() {
		// 文字列に変換するためのバッファを用意
		StringBuffer stbf = new StringBuffer("");
		try {
			FileInputStream fis = new FileInputStream(super.file);
			HSLFSlideShow hslfs = new HSLFSlideShow(fis);
			SlideShow slideShow = new SlideShow(hslfs);
			Slide[] slides = slideShow.getSlides();
			for (int i = 0; i < slides.length; i++) {
				Shape[] shapes = slides[i].getShapes();
				for (int j = 0; j < shapes.length; j++) {
					if (shapes[j] instanceof TextBox) {
						TextBox shape = (TextBox) shapes[j];
						String text = shape.getText();
						if (text != null) {
							stbf.append(text);
							stbf.append(CR);
						}
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

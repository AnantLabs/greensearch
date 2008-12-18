package green.search.crawler.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class SimpleTextConverter extends AbstractDocumentConverter implements
		DocumentConverter {

	private String[] han = new String[] { "��", "��", "��", "��", "��", "��",
			"��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��",
			"��", "��", "��", "��", "��", "��", "��", "��", "��", "�", "�",
			"�", "�", "�", "�", "�", "�", "�", "�", "�", "�", "�", "�", "�",
			"�", "�", "�", "�", "�", "�", "�", "�", "�", "�", "�", "�", "�",
			"�", "�", "�", "�", "�", "�", "�", "�", "�", "�", "�", "�", "�",
			"�", "�", "�", "�", "�", "�", "�", "�", "�", "�", "�", "�", "�",
			"�", "�", "�", "�", "�", "�", "�", "�", "�" };
	private String[] zenn = new String[] { "�K", "�M", "�O", "�Q", "�S", "�U", "�W",
			"�Y", "�[", "�]", "�_", "�a", "�d", "�f", "�h", "�o", "�p", "�r", "�s", "�u",
			"�v", "�x", "�y", "�{", "�|", "��", "�@", "�A", "�B", "�C", "�D", "�E", "�F",
			"�G", "�H", "�I", "�J", "�L", "�N", "�P", "�R", "�T", "�V", "�X", "�Z", "�\",
			"�^", "�`", "�b", "�c", "�e", "�g", "�i", "�j", "�k", "�l", "�m", "�n", "�q",
			"�t", "�w", "�z", "�}", "�~", "��", "��", "��", "��", "��", "��", "��", "��",
			"��", "��", "��", "��", "��", "��", "��", "��", "��", "�B", "�u", "�v", "�A",
			"�E", "�[", "�J", "�K" };

	private String hanToZen(String str) {

		for (int i = 0; i < han.length; i++) {
			while (str.indexOf(han[i]) != -1) {
				str = str.replaceAll(han[i], zenn[i]);
			}
		}
		return str;
	}

	/** MB */
	private int default_max_size = 32;

	public SimpleTextConverter(File file) {
		super(file);
	}

	public String convert() {

		// �t�@�C���T�C�Y������l�ɒB���Ă�����ǂݍ��݂��Ȃ��B
		if (file.length() / 1024 / 1024 >= default_max_size)
			return null;

		// ������ɕϊ����邽�߂̃o�b�t�@��p��
		StringBuffer stbf = new StringBuffer();
		try {
			FileInputStream fin = new FileInputStream(super.file);
			// InputStreamReader isr = new InputStreamReader(fin, "JISAutoDetect");
			InputStreamReader isr = new InputStreamReader(fin, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String s;
			while ((s = br.readLine()) != null) {
				stbf.append(s);
				stbf.append(CR);
			}
			br.close();
			isr.close();
			fin.close();
		} catch (RuntimeException e) {
			e.printStackTrace(System.err);
			System.err.println("#####################" + super.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hanToZen(stbf.toString());
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String paht = "C:\\Documents and Settings\\haruyosi\\�f�X�N�g�b�v\\�X�֔ԍ�A.txt";
		SimpleTextConverter cnv = new SimpleTextConverter(new File(paht));
		System.out.println(cnv.convert());
	}
}

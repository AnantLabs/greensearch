package green.search.crawler.converter;

/**
 * ���͂��C���f�b�N�X�\�ȃe�L�X�g�`���ɕϊ�����N���X�B
 * 
 * @author haruyosi
 */
public interface DocumentConverter {

	/**
	 * �Ώۃh�L�������g�f�[�^���e�L�X�g�`���ɕϊ�����B ���ׂẴh�L�������g�����S�ɃC���f�b�N�X�������ꍇ�ȊO�́A
	 * ���̃��\�b�h�̎����ŗ�O���X���[����ׂ��ł͂Ȃ��B
	 * 
	 * @return
	 */
	public String convert();
}

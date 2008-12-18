package org.apache.solr.core;

import java.io.IOException;

import green.search.lsi.LsaFacade;
import green.search.lsi.matrix.SparseMatrix;
import green.search.query.SVDWeight;

public class TestCommitListener extends AbstractSolrEventListener {

	public TestCommitListener(SolrCore core) {
		super(core);
	}

	public void postCommit() {
		System.out.println("### TestCommitListener is called");

		SparseMatrix sm = null;
		try {
			LsaFacade lsafacade = new LsaFacade(super.core);
			sm = lsafacade.createCrsMatrix("CRS");
			traceMatrix(sm, false);
			lsafacade.executeLatentSemanticAnalysis(sm);
			lsafacade.saveIndex();
			SVDWeight.loadMatrix();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				sm.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sm.relese();
		}

	}

	private void traceMatrix(SparseMatrix A, boolean CCR) throws IOException {
		System.out.println("## cols = " + A.getCols());
		System.out.println("## rows = " + A.getRows());
		System.out.println("## vals = " + A.getVals());
		// for (int i = 0; i < (CCR ? A.getCols() : A.getRows()) + 1; i++) {
		// System.out.print("\t" + A.getPointer(i));
		// }
		// System.out.println();
		// for (int i = 0; i < A.getVals(); i++) {
		// System.out.print("\t" + A.getIndex(i));
		// }
		// System.out.println();
		// for (int i = 0; i < A.getVals(); i++) {
		// System.out.print("\t" + A.getValue(i));
		// }
		// System.out.println();
	}

}

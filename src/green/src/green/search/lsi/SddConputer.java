package green.search.lsi;

import java.io.IOException;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;

import green.search.lsi.matrix.BigDiagMatrix;
import green.search.lsi.matrix.CRSMatrix;
import green.search.lsi.matrix.SparseMatrix;
import green.search.lsi.matrix.TwoBitMatrix;

public class SddConputer {

	private static final int IDXSHIFT = Integer.SIZE / 8 + 1;
	// private static final int IDXSHIFT = 5;

	private static final int IDXMASK = Integer.SIZE - 1;

	private static final int ONE = 1;

	private static final int MAXMASK = (ONE << IDXMASK);

	private void bzero(int[] a, int size) {
		// a = new long[size];
		for (int i = 0; i < size; i++) {
			a[i] = 0;
		}
	}

	private double[] bzero(double[] a, int size) {
		// a = new Double[size];
		for (int i = 0; i < size; i++) {
			a[i] = 0.0;
		}
		return a;
	}

	private void bone(int[] a, int size) {
		// a = new long[size];
		for (int i = 0; i < size; i++) {
			a[i] = 0xff;
			// a[i] = 0xff;
			// a[i] = 0xffffffff;
			// a[i] = -1;
		}
	}

	private void bone(double[] a, int size) {
		// a = new double[size];
		for (int i = 0; i < size; i++) {
			// a[i] = 0xff;
			a[i] = 0xffffffff;
		}
	}

	/**
	 * Add x as the next column in A. On error, prints message.
	 */
	private void expand_smatrix(Smatrix A, Svector x) {

		if (x.m != A.m) {
			System.out.println("Error: Size mismatch.");
			return;
		}

		if (A.k == A.kmax) {
			System.out.println("Error: Smatrix is full.");
			return;
		}

		A.col[A.k] = x;
		A.k++;
		return;

	} /* expand_smatrix */

	/**
	 * Add d to the next open spot in D. On error, prints message.
	 * 
	 * @param D
	 * @param d
	 */
	private void expand_dmatrix(Dmatrix D, double d) {

		if (D == null) {
			System.out.println("Cannot expand NULL Dmatrix.");
			return;
		}

		if (D.k == D.kmax) {
			System.out.println("Error: Dmatrix is full.");
			return;
		}

		D.d[D.k] = d;
		D.k++;

		return;

	} /* expand_dmatrix */

	/**
	 * Adds d and the svectors x and y to the next spot in A, and updates the
	 * number of terms in A. On error, prints warning.
	 * 
	 * @param A
	 * @param d
	 * @param x
	 * @param y
	 */
	private void expand_sdd(Sdd A, double d, Svector x, Svector y) {

		if (A.k == A.kmax) {
			System.out.println("Error: SDD is full.");
			return;
		}

		expand_dmatrix(A.D, d);
		expand_smatrix(A.X, x);
		expand_smatrix(A.Y, y);
		A.k++;
		return;

	} /* expand_sdd */

	/**
	 * Solve the SDD subproblem.
	 * 
	 * Returns the maximum value of
	 * 
	 * <pre>
	 * 		 max (x' * s) / |x|_2&circ;2
	 * </pre>
	 * 
	 * in 'val' and the number of nonzeros in x in 'idx'. Here x is constrained
	 * to be an (A->m)-long svector and s equals (A - B)y. A is a sparse matrix,
	 * B is an SDD of the same size, and y is an (A->n)-long svector. If tflag
	 * is TRUE, we use the transpose of A and B, x should be (A->n)-long, and y
	 * should be (A->m)-long. Only x is modified. On error, returns f with 0,0.
	 * 
	 * @param A
	 * @param B
	 * @param y
	 * @param x
	 * @param tflag
	 * @param s
	 * @return
	 * @throws IOException
	 */
	private SddDouble_plus subproblem(SparseMatrix A, Sdd B, Svector y,
			Svector x, int tflag, double[] s) throws IOException {

		SddDouble_plus f = new SddDouble_plus(); /* final answer */
		int i, j, m; /* counters */
		int[] valptr, sgnptr; /* tmp pointer */
		int mask; /* tmp mask */
		int onesmask; /* word of all ones */
		SddDouble_plus[] sorts = null; /* workspace */
		int sflag; /* compute s? */

		f.val = 0;
		f.idx = 0; /* init f for error returns */

		if ((A == null) || (y == null) || (x == null)) {
			System.out.println("Error in subproblem input arguments.");
			return (f);
		}

		// bone(&onesmask, sizeof(ulong));
		onesmask = 0xffffffff;
		// onesmask = 0xff;
		/* word of all ones */

		/* allocate memory for the s and sorts */

		m = (A.getCols() > A.getRows()) ? A.getCols() : A.getRows();

		if (s == null) { /* s is not given */
			sflag = 1;
			s = new double[m];
			// if ((s = (sdddouble*) calloc(m, sizeof(sdddouble))) == NULL) {
			// fprintf(stderr, "Error allocating work space.\n");
			// return (f);
			// }
		} else
			sflag = 0; /* don't compute s */

		sorts = new SddDouble_plus[m];
		for (int k = 0; k < sorts.length; k++) {
			sorts[k] = new SddDouble_plus();
		}
		// if ((sorts = (sdddouble_plus*) calloc(m, sizeof(sdddouble_plus))) ==
		// NULL) {
		// fprintf(stderr, "Error allocating work space.\n");
		// return (f);
		// }

		m = x.m; /* copy to a local variable */

		if (sflag != 0) {
			// System.out.print("y = ");
			// for (i = 0; i < y.val.length; i++) {
			// System.out.print(y.val[i] + " ");
			// }
			// System.out.println();
			// System.out.println("tflag = " + tflag);
			matrixxsv(s, A, y, tflag); /* s = A y */
			sddxsv(s, B, y, tflag); /* s = s - B y */
			// System.out.print("sx = ");
			// for (i = 0; i < s.length; i++) {
			// System.out.print(s[i] + " ");
			// }
			// System.out.println();
		}

		/* Initialize of s-values in x to 1 */

		bone(x.val, numwords(m));
		/* set every bit to 1 */
		bzero(x.sgn, numwords(m));
		/* set every bit to 0 */

		/*
		 * Fill in the sorts array with abs(s), and change the i-th s-value in x
		 * to -1 if s[i] is negative.
		 */

		// int pvalptr = 0;
		int psgnptr = 0;

		valptr = x.val; /* copy to local pointer */
		sgnptr = x.sgn; /* copy to a local pointer */
		mask = ONE; /* initialize */

		for (i = 0; i < m; i++) {
			// System.out.print(s[i] + ", ");

			sorts[i].idx = i; /* copy the index into array to be sorted */

			/*
			 * If s[i] is negative, set the i-th bit of x->sgn to 1 w/o
			 * affecting any other bits. Also, fill in sorts[i].val with
			 * abs(s[i]).
			 */

			if (s[i] < 0) { /* make x[i] = -1 */
				sorts[i].val = -s[i]; /* copy -s[i] into array to be sorted */
				sgnptr[psgnptr] |= mask;
				// x.sgn[psgnptr] |= mask;
				/*
				 * swaps the correct bit of x->sgn to 1
				 */
			} else
				sorts[i].val = s[i]; /* copy s[i] into array to be sorted */

			/* Update the mask and, if necessary, the pointer. */

			if (mask == MAXMASK) {
				mask = ONE;
				psgnptr++;
			} else
				mask <<= 1;
		}
		// System.out.println();

		// System.out.print("sorts =");
		// for (i = 0; i < m; i++)
		// System.out.print(" [" + sorts[i].idx + " " + sorts[i].val + "]");
		// System.out.println();

		/* sort sorts */
		Qsort q = new Qsort();
		q.sort(sorts);
		q.reverse(sorts);
		// qsortopt((char *) sorts, m);
		/* compute partial sums */

		s[0] = sorts[0].val;
		for (i = 1; i < m; i++)
			s[i] = s[i - 1] + sorts[i].val;

		/* compute function values */
		for (i = 0; i < m; i++)
			s[i] = (s[i] * s[i]) / (i + 1);

		/* find the maximum of the array s */
		f.val = s[0];
		f.idx = 0;
		for (i = 1; i < m; i++)
			if (s[i] > f.val) {
				f.val = s[i];
				f.idx = i;
			}

		/* increment f.idx so that it is the number of nonzeros in x */
		f.idx++;

		/*
		 * zero out certain elements of x with indices > f.idx in the sorted
		 * array
		 */

		for (i = f.idx; i < m; i++) {

			/* (j+1)st s-value in x should be zero */
			j = sorts[i].idx;

			/* causes bit j in x->val to be zero w/o affecting other bits */
			// System.out.print(", i = " + i);
			// System.out.print(", f.idx = " + f.idx);
			// System.out.print(", m = " + m);
			// System.out.print(", ONE = " + ONE);
			// System.out.print(", j = " + j);
			// System.out.print(", IDXMASK = " + IDXMASK);
			// System.out.print(", IDXSHIFT = " + IDXSHIFT);
			// System.out.print(", onesmask = " + onesmask);
			// System.out.print(", ((ONE << (j & IDXMASK)) ^ onesmask) = "
			// + ((ONE << (j & IDXMASK)) ^ onesmask));
			// System.out.print(", (j >> IDXSHIFT) = " + (j >> IDXSHIFT));
			// System.out
			// .print(" valptr[j >> IDXSHIFT] = " + x.val[j >> IDXSHIFT]);
			//
			// System.out.println();
			valptr[j >> IDXSHIFT] &= ((ONE << (j & IDXMASK)) ^ onesmask);
			// x.val[j >> IDXSHIFT] &= ((ONE << (j & IDXMASK)) ^ onesmask);
		}

		// System.out.print("x.val = ");
		// for (i = 0; i < x.val.length; i++) {
		// System.out.print(x.val[i] + " ");
		// }
		// System.out.println();
		// System.out.println();

		s = null;
		sorts = null;

		return (f);

	} /* subproblem */

	/**
	 * Initializes x so that every 100th s-value is 1, and all other values are
	 * set to 0. Does nothing on error.
	 */
	private void init_pones(Svector x) {

		int i; /* counter */
		int m; /* size of x */

		if (x == null) {
			System.out.println("Error trying to initialize NULL y-vector.");
			return;
		}

		m = x.m;

		bzero(x.val, numwords(m));
		/* set x to all zeros */
		bzero(x.sgn, numwords(m));
		/* set x to all zeros */

		/* set every 100th element in x to 1 */
		for (i = 0; i < m; i += 100) {
			x.val[i >> IDXSHIFT] |= ONE << (i & IDXMASK);
		}

		return;

	} /* init_pones */

	/**
	 * Initializes x so that every 100th s-value is 1, and all other values are
	 * set to 0. Does nothing on error.
	 * 
	 * @param x
	 */
	private void init_ones(Svector x) {

		int m; /* size of x */

		if (x == null) {
			System.out.println("Error trying to initialize NULL y-vector.");
			return;
		}

		m = x.m;

		bone(x.val, numwords(m));
		/* set x to all ones */
		bzero(x.sgn, numwords(m));
		/* set x to all oness */

		return;

	} /* init_pones */

	/**
	 * 
	 */
	private void init_cycle(Svector y, int idx) {
		int n; /* size of A */
		int localidx; /* initialization index */

		if (y == null) {
			System.out.println("Error trying to initialize NULL y-vector.");
			return;
		}

		n = y.m;

		/* Set y */
		bzero(y.val, numwords(n));
		bzero(y.sgn, numwords(n));

		localidx = idx % n;
		y.val[localidx >> IDXSHIFT] |= ONE << (localidx & IDXMASK);

		return;
	}

	static int[] cntlookup = null; /* static look-up table */

	/**
	 * SDD times Svector. Computes s = s - A y where s is a A->m long real
	 * vector (of type sdddouble), A is an SDD, and y is an A->n long svector. A
	 * is transposed if tflag=1, and then we assume s is A->n long and y is A->m
	 * long. The vector s is modified. All the other vectors are unchanged. On
	 * error, prints message.
	 */
	private void sddxsv(double[] s, Sdd A, Svector y, int tflag) {

		int i, j, k; /* counters */
		int kmax; /* number of terms in SDD A */
		int nwrds; /* number of words in y */
		int cnt; /* counter */
		double tmp; /* tmp value */
		Smatrix X, Y; /* local pointers */
		double[] D; /* local pointer */
		int val, sgn; /* used in inner product calculation */
		int mask; /* used for last partial word */
		int[] xvalptr, xsgnptr; /* pointers to walk through arrays */
		int[] yvalptr, ysgnptr; /* pointers to walk through arrays */
		double[] tmpvec = null; /* temporary workspace holds A y */

		if (A.k == 0)
			return; /* A is empty, do nothing */

		/* Allocate space for tmpvec. */

		tmpvec = new double[A.kmax];
		// if ((tmpvec = (sdddouble*) calloc(A->kmax, sizeof(sdddouble))) ==
		// NULL) {
		// fprintf(stderr, "Error allocating work space for sdd times vec.\n");
		// return;
		// }

		/*
		 * Allocate space for and compute the static look-up table cntlookup.
		 * Note that since this variable is static, we only compute the look-up
		 * table the first time this function is called. We use this table for
		 * speed. For a given byte x, cntlookup[x] = number of one bits in x.
		 */

		if (cntlookup == null) {
			cntlookup = new int[256];
			// if ((cntlookup = (int*) calloc(256, sizeof(int))) == NULL) {
			// fprintf(stderr, "Error allocating space for look-up table.\n");
			// return;
			// }
			// System.out.println("### cntlookup = ");
			for (i = 0; i < cntlookup.length; i++) {
				mask = i;
				cnt = 0;
				for (j = 0; j < 8; j++) {
					if ((mask & 1) != 0)
						cnt++;
					mask >>= 1;
				}
				cntlookup[i] = cnt;
				// if (i % 16 == 0)
				// System.out.println();
				// System.out.print(cnt + "\t");
			}
			// System.out.println();
		}

		/*
		 * Assign X, Y, and D to local variables. Note that transposing the SDD
		 * A only has the effect of swapping the X and Y matrices, so that is
		 * the only thing we need to do if tflag=1.
		 */

		kmax = A.k;

		if (tflag != 0) { /* Transpose => Swap X and Y */
			X = A.Y;
			Y = A.X;
		} else {
			X = A.X;
			Y = A.Y;
		}

		D = (A.D).d;

		/* COMPUTE tmpvec = Y'y */

		if (y.m != Y.m) { /* error checking */
			System.out.println("Size mismatch.");
			return;
		}

		yvalptr = y.val;
		ysgnptr = y.sgn;

		nwrds = numwords(y.m);
		mask = (ONE << (Y.m & IDXMASK)) - 1;
		if (mask != 0)
			ysgnptr[nwrds - 1] &= mask;

		/*
		 * Loop through each column of Y, computing its inner product with y
		 * which will be added to s[k].
		 */

		for (k = 0; k < kmax; k++) {

			/*
			 * Set xvalptr and xsgnptr to the beginnings of the (k+1)st column
			 * of Y's val and sgn arrays.
			 */

			xvalptr = (Y.col[k]).val;
			xsgnptr = (Y.col[k]).sgn;

			/* Mask final partial word, if it exists */
			if (mask != 0) {
				xvalptr[nwrds - 1] &= mask;
				xsgnptr[nwrds - 1] &= mask;
			}

			cnt = 0; /*
					 * This will be the value of the innner product when we are
					 * done.
					 */

			/* Do whole words first. */

			for (i = 0; i < nwrds; i++) {

				/*
				 * Rather than going through comparing y and the (k+1)st column
				 * of Y s-value by s-value, we will handle BITS_PER_WORD
				 * s-values at once. Assuming there are 32 bits_per_word, we are
				 * trading a _MINIMUM_ of 32 shifts, 32 AND's, and 32 compares
				 * for 2 AND's, 1 XOR, 8 adds, 8 array look-ups, 4 multiplies,
				 * and 8 right shifts. Generally, the cntlookup array should
				 * easily fit into cache memory.
				 * 
				 * Here is a brief mathematical explanation of what we are
				 * doing: suppose we have two s-values a and b. If we AND the
				 * val bits, we get 1 if ab = +1 or -1, and 0 if ab = 0; call
				 * this bit c. If ab = -1, the XOR of the sgn bits is 1, and if
				 * ab = 1, the XOR of the sgn bits is 0. If ab = 0, the XOR of
				 * the sgn bits could be anything. In order to make sure it is
				 * zero if ab = 0, we AND c with the result of the XOR of the
				 * sgn bits; Call this d. Finally, a b = c - 2 d. We are doing
				 * the same thing below, but rather than dealing with one pair
				 * of s-values at a time, we are dealing with 32!
				 */

				val = xvalptr[i] & yvalptr[i];
				sgn = xsgnptr[i] ^ ysgnptr[i];
				sgn &= val;

				// System.out.println("### val = " + val);
				// System.out.println("### val2 = " + (val & -1));

				for (j = 0; j < Integer.SIZE / 8; j++) {
					// for (j = 0; j < Integer.SIZE; j++) {
					// System.out.println("### val = " + ((val & 0xff)));
					// cnt += cntlookup[(int) (val & 0xff)];
					cnt += cntlookup[(int) (val & 0xff)];
					// System.out.println("### sgn = "
					// + ((sgn & 0xff)));
					// cnt -= 2 * cntlookup[(int) (sgn & 0xff)];
					cnt -= 2 * cntlookup[(int) (sgn & 0xff)];
					// System.out.println("### cnt3 = " + cnt);
					val >>= 8;
					sgn >>= 8;
					// if (val != 0)
					// System.out.println("### val(in) = " + val);

				}
			}

			// System.out.println("### cnt = " + cnt);
			tmpvec[k] = cnt;

		} /* k-loop */

		/*------------------------------
		 Compute tmpvec = D * tmpvec
		 ------------------------------*/

		for (k = 0; k < kmax; k++) {
			// System.out.println("### tmpvec[k] = " + tmpvec[k]);
			// System.out.println("### D[k] = " + D[k]);
			tmpvec[k] *= D[k];
		}

		/*------------------------------
		 Compute s -= X * tmpvec
		 ------------------------------*/

		/* Cycle through the columns of X. */

		// System.out.println("### kmax = " + kmax);
		// System.out.print("### tmpvec = ");
		for (k = 0; k < kmax; k++) {
			int pxvalptr = 0;
			int pxsgnptr = 0;

			// System.out.print(tmpvec[k] + ", ");
			tmp = tmpvec[k]; /* Copy tmpvec[k] to a local variable */
			xvalptr = (X.col[k]).val; /* Set xvalptr to the beginning to val */
			xsgnptr = (X.col[k]).sgn; /* Set xsgnptr to the beginning to sgn */
			// val = *xvalptr; /* Dereference the xvalptr */
			// sgn = *xsgnptr; /* Dereference the xsgnptr */
			val = xvalptr[pxvalptr]; /* Dereference the xvalptr */
			sgn = xsgnptr[pxsgnptr]; /* Dereference the xsgnptr */
			mask = ONE; /* Initialize the mask */

			/* This loop will go through each s-value in X->m. */

			for (i = 0; i < X.m; i++) {

				/* Mask picks off the current s-value. */

				if ((val & mask) != 0) { /* i-th s-value is nonzero */
					if ((sgn & mask) != 0) /* X[i,k] = -1 */
						s[i] += tmp;
					else
						/* X[i,k] = 1 */
						s[i] -= tmp;
				}

				/* Update mask and, if necessary, val and sgn. */

				// if (mask == MAXMASK && pxvalptr < xvalptr.length) {
				if (mask == MAXMASK) {
					mask = ONE; /* Reinitialize the mask. */
					// Increment the pointer and dereference.
					val = xvalptr[pxvalptr++];
					// Increment the pointer and dereference.
					sgn = xsgnptr[pxsgnptr++];
				} else
					mask <<= 1; /* Update the mask. */

			} /* i-loop */

		} /* k-loop */
		// System.out.println();

		tmpvec = null;
		return;

	} /* smxsv */

	/**
	 * Sparse matrix times svector. Computes s = A * y where A is transposed if
	 * tflag is TRUE. On error, prints message.
	 * 
	 * @param s
	 * @param A
	 * @param y
	 * @param tflag
	 * @throws IOException
	 */
	private void matrixxsv(double[] s, SparseMatrix A, Svector y, int tflag)
			throws IOException {

		int i, j; /* couters */
		int m, n; /* size of A */
		// int[] ir, jc; /* tmp pointers */
		// float[] val; /* tmp pointer */
		int row; /* row number */
		int idx; /* word index of y to get svalue */
		int mask; /* mask to extract svalue */
		int[] valptr, sgnptr; /* tmp pointers */

		m = A.getCols();
		n = A.getRows();
		// ir = A->ir;
		// jc = A->jc;
		// val = A->val;
		valptr = y.val;
		sgnptr = y.sgn;

		if (tflag != 0) { /* tranpose A */

			bzero(s, n);
			/* set s to zero */

			for (j = 0; j < n; j++)

				for (i = A.getPointer(j); i < A.getPointer(j + 1); i++) {

					row = A.getIndex(i);
					idx = row >> IDXSHIFT; /* element of y to look in */
					// System.out.print("### ONE = " + ONE + ", IDXMASK = "
					// + IDXMASK + ", (row & IDXMASK) = "
					// + (row & IDXMASK));
					mask = ONE << (row & IDXMASK); /* bit in y[idx] to pick off */

					// System.out.println(" i = " + i + ", row = " + row
					// + ", idx = " + idx + ", mask = " + mask + ", s["
					// + j + "] = " + s[j] + ", valptr[idx] = "
					// + valptr[idx] + ", sgnptr[idx] = " + sgnptr[idx]);
					if ((valptr[idx] & mask) != 0) { /* nonzero */
						if ((sgnptr[idx] & mask) != 0) /* y[row] = -1 */
							s[j] -= A.getValue(i);
						else
							/* y[row] = 1 */
							s[j] += A.getValue(i);
					}

				} /* i-loop */
		} /* if - transpose */

		else { /* no transpose */

			bzero(s, m);
			/* set s to zero */

			idx = 0;
			mask = 1;
			for (j = 0; j < n; j++) {

				if ((valptr[idx] & mask) != 0) { /* nonzero */
					if ((sgnptr[idx] & mask) != 0) /* y[j] = -1 */
						for (i = A.getPointer(j); i < A.getPointer(j + 1); i++) {
							s[A.getIndex(i)] -= A.getValue(i);
						}
					else
						/* y[j] = 1 */
						for (i = A.getPointer(j); i < A.getPointer(j + 1); i++) {
							// System.out.println("### " + m + " " + (i) + " "
							// + A.getIndex(i) + " " + A.getValue(i));
							s[A.getIndex(i)] += A.getValue(i);
						}
				}

				if (mask == MAXMASK) {
					mask = 1;
					idx++;
				} else
					mask <<= 1;

			} /* j-loop */

		} /* else - no transpose */

	} /* matrixxsv */

	private double[] init_threshold(Svector y, SparseMatrix A, Sdd B,
			double rho, int[] idx) throws IOException {
		int i; /* counter */
		int m, n; /* size of A */
		int localidx; /* initialization index */
		double[] s; /* return vector */
		double sqnorms = 0; /* squared norm of s */

		int pidx = 0;

		if ((y == null) || (A == null) || (B == null)) { /* error */
			System.out.println("Error in initialization of y.");
			return null;
		}

		/* Allocate space for s and init to zero. */
		m = (A.getCols() > A.getRows()) ? A.getCols() : A.getRows();
		s = new double[m];
		// System.out.println("s の定義");
		// for (i = 0; i < s.length; i++) {
		// System.out.print(s[i] + ", ");
		// }
		// System.out.println();

		// if ((s = (sdddouble*) calloc(m, sizeof(sdddouble))) == NULL) {
		// fprintf(stderr, "Error allocating space for s.\n");
		// return null;
		// }

		/* Set sizes. */
		m = A.getCols();
		n = A.getRows();

		while (sqnorms < (rho / (double) n)) {

			// System.out.println("### sqnorms = " + sqnorms + ", rho = " + rho
			// + ", n= " + n + " {F} = " + (sqnorms < (rho / n)));
			/* Set y */
			bzero(y.val, numwords(n));
			bzero(y.sgn, numwords(n));

			// localidx = (*idx) % n;
			localidx = idx[pidx] % n;
			y.val[localidx >> IDXSHIFT] |= ONE << (localidx & IDXMASK);

			/* Update iidx */
			// (*idx) ++;
			idx[pidx]++;

			/* Compute s */
			matrixxsv(s, A, y, 0); /* s = A y */
			// System.out.print("### s0 = ");
			// for (i = 0; i < s.length; i++) {
			// System.out.print(s[i] + ", ");
			// }
			// System.out.println();
			sddxsv(s, B, y, 0); /* s = s - B y */

			/* Compute squared norm of s */
			sqnorms = 0;
			for (i = 0; i < m; i++) {
				sqnorms += s[i] * s[i];
			}

		} /* while loop */

		return (s);
	}

	/**
	 * 
	 * @param x
	 * @return
	 */
	private int svcount(Svector x) {
		/*
		 * Returns the number of non-zero values in x. On error, returns zero.
		 */

		int i, j; /* counters */
		int cnt; /* counter */
		int nwrds; /* number of whole words used in x */
		int val; /* temporary variable */
		int mask; /* mask for final word */
		int[] cntlookup = null; /* static look-up table */

		if (x == null)
			return (0); /* error */

		/*
		 * The array cntlookup is a lookup table of length 2^8 that, for each
		 * byte used as an index, returns the number of 1-bits in that byte.
		 * Allocate space for and compute the static look-up table cntlookup.
		 * Note that since this variable is static, we only compute the look-up
		 * table the first time this function is called. We use this table for
		 * speed. For a given byte x, cntlookup[x] = number of one bits in x.
		 */

		if (cntlookup == null) {
			cntlookup = new int[256];
			// if ((cntlookup = (int*) calloc(256, sizeof(int))) == NULL) {
			// fprintf(stderr, "Error allocating space for look-up table.\n");
			// return (0);
			// }
			for (i = 0; i < 256; i++) {
				mask = i;
				cnt = 0;
				for (j = 0; j < 8; j++) {
					if ((mask & 1) != 0)
						cnt++;
					mask >>= 1;
				}
				cntlookup[i] = cnt;
			}
		}

		nwrds = numwords(x.m); /* number of words in x */

		/* compute mask for final partial word with (x->m & idxmask) 1-bits */
		mask = (ONE << (x.m & IDXMASK)) - 1;
		if (mask != 0)
			x.val[nwrds - 1] &= mask;

		/*
		 * Count the number of 1 bits in the whole words of val. For efficiency,
		 * we look at the val array one byte at a time (rather than one bit at a
		 * time) using the cntlookup array. The means we trade 8 right shifts, 8
		 * AND's, and 8 adds for one right shift, one and, one add, and one
		 * array look-up. Generally, the cntlookup array should easily fit into
		 * cache memory.
		 */

		cnt = 0;
		for (i = 0; i < nwrds; i++) {
			val = x.val[i];
			for (j = 0; j < (Integer.SIZE / 8); j++) {
				// for (j = 0; j < (Integer.SIZE); j++) {
				// cnt += cntlookup[(int) (val & 0xff)];
				cnt += cntlookup[(int) (val & 0xff)];
				val >>= 8;
			}
		}

		return (cnt);

	} /* svcount */

	/**
	 * Returns the sum of all the squares of the elements of sparse matrix A. On
	 * error, returns zero.
	 * 
	 * @return
	 * @throws IOException
	 */
	private double fnormsq(SparseMatrix A) throws IOException {

		int i; /* counter */
		double f = 0; /* sum */

		if (A == null)
			return (f);

		for (i = 0; i < A.getVals(); i++)
			f += A.getValue(i) * A.getValue(i);

		return (f);

	} /* fnormsq */

	/**
	 * Returns the number of words needed to store x bits. On error, returns 0.
	 * 
	 * @param x
	 * @return
	 */
	int numwords(int x) {

		int i;
		if (x < 0)
			return (0); /* error */
		i = x >> IDXSHIFT; /* whole words */
		if ((x & IDXMASK) != 0)
			i++; /* partial word */
		return (i);

	} /* numwords */

	/**
	 * Frees the memory used by x. Does nothing if x is NULL.
	 * 
	 * @param x
	 */
	private void free_svector(Svector x) {

		if (x == null) {
			System.out.println("Warning: Trying to free null svector.");
			return;
		}
		x.val = null;
		x.sgn = null;
		x = null;
		return;

	} /* free_svector */

	/**
	 * Creates an svector that holds m svalues. If x is non-null, the first
	 * min(x->m, m) entries are preserved. On error, returns NULL.
	 * 
	 * @param x
	 * @param m
	 * @return
	 */
	private Svector create_svector(Svector x, int m) {

		int nwrds = numwords(m);

		if (x == null) { /* create new svector */

			x = new Svector();
			// if ((x = (svector*) calloc(1, sizeof(svector))) == NULL) {
			// fprintf(stderr, "Error allocating space for svector.\n");
			// return (NULL);
			// }
			x.m = m;
			x.val = new int[nwrds];
			x.sgn = new int[nwrds];
			// x.val = new long[m];
			// x.sgn = new long[m];
			// if (((x->val = (ulong*) calloc(nwrds, sizeof(ulong))) == NULL)
			// || ((x->sgn = (ulong*) calloc(nwrds, sizeof(ulong))) == NULL)) {
			// fprintf(stderr, "Error allocating space for svector
			// components.\n");
			// return (NULL);
			// }

		} /* if - create new svector */

		else { /* enlarge or shrink previous svector */

			x.m = m;
			// x.val = new long[nwrds];
			// x.sgn = new long[nwrds];
			// x.val = new long[m];
			// x.sgn = new long[m];
			// if (((x->val = (ulong*) realloc(x->val, nwrds * sizeof(ulong)))
			// == NULL)
			// || ((x->sgn = (ulong*) realloc(x->sgn, nwrds * sizeof(ulong)))
			// == NULL)) {
			// fprintf(stderr, "Error shrinking/enlarging svector.\n");
			// return (NULL);
			// }

		} /* else - enlarge or shrink previous svector */

		return (x);

	} /* create_svector */

	/**
	 * Returns a ptr to a dmatrix of maximum size kmax. If D is non-null then
	 * the first kmin elements of D are preserved. On error, returns NULL.
	 * 
	 * @param D
	 * @param kmin
	 * @param kmax
	 * @return
	 */
	private Dmatrix create_dmatrix(Dmatrix D, int kmin, int kmax) {

		if ((kmin < 0) || (kmax <= 0) || (kmin > kmax)) { /* error checking */
			System.out.println("Error in parameters for dmatrix.");
			return null;
		}

		if (D == null) { /* start from scratch */

			D = new Dmatrix();
			// if ((D = (dmatrix*) calloc(1, sizeof(dmatrix))) == NULL) {
			// fprintf(stderr, "Error allocating space for dmatrix.\n");
			// return null;
			// }

			D.k = 0;
			D.kmax = kmax;

			D.d = new double[kmax];
			// if ((D.d = (sddfloat*) calloc(kmax, sizeof(sddfloat))) == NULL) {
			// fprintf(stderr, "Error allocating space for dmatrix values,\n");
			// return null;
			// }

		} /* if - start from scratch */

		else { /* enlarging or shrinking D */

			/* Delete extra elements, if necessary. */

			if (kmin > D.k) {
				System.out.println("Error: kmin too big for dmatrix.");
				return null;
			}

			D.k = kmin;

			/* Expand or shrink maximum number of elements, if necessary. */

			if (D.kmax != kmax) {
				D.kmax = kmax;
				D.d = new double[kmax];
				// if ((D.d = (sddfloat*) realloc(D.d, kmax * sizeof(sddfloat)))
				// == NULL) {
				// fprintf(stderr, "Error in reallocating memory for
				// dmatrix.\n");
				// return null;
				// }
			}

		} /* else - enlarge/shrink */

		return (D);

	} /* create_dmatrix */

	/**
	 * Returns a ptr to an smatrix of maximum dimension m by kmax, and of
	 * current dimension m by kmin. If A is non-null, then the first min(A->m,
	 * m) s-values of the first kmin columns of A are preserved. On error,
	 * returns NULL.
	 * 
	 * @param A
	 * @param kmin
	 * @param kmax
	 * @param m
	 * @return
	 */
	private Smatrix create_smatrix(Smatrix A, int kmin, int kmax, int m) {

		int k; /* counter */

		if ((kmax <= 0) || (m <= 0) || (kmin < 0)) { /* error checking */
			System.out.println("Error in requested size of smatrix.");
			return null;
		}

		if (kmin > kmax) { /* error checking */
			System.out.println("Max k-dimension less than min dimension.");
			return null;
		}

		if (A == null) { /* starting from scratch */

			if (kmin != 0) { /* error checking */
				System.out.println("Invalid kmin dimension.");
				return null;
			}

			A = new Smatrix();
			// if ((A = (smatrix*) calloc(1, sizeof(smatrix))) == NULL) {
			// fprintf(stderr, "Error allocating memory for smatrix.\n");
			// return null;
			// }

			A.k = 0; /* current columns in A */
			A.kmax = kmax; /* max size columns in A */
			A.m = m; /* number of rows in A */

			/* allocate column pointers */
			A.col = new Svector[kmax];
			// if ((A->col = (svector**) calloc(kmax, sizeof(svector*))) ==
			// NULL) {
			// fprintf(stderr, "Error allocating space for smatrix columns.\n");
			// return null;
			// }

		} /* if - starting from scratch */

		else { /* enlarging or shrinking A */

			/* If nothing needs to be changed, just return A. */
			if ((A.k == kmin) && (A.kmax == kmax) && (A.m == m))
				return (A);

			/* If the current A has too many columns defined, delete some. */
			if (kmin < A.k) {
				for (k = kmin; k < A.k; k++)
					free_svector(A.col[k]);
				A.k = kmin;
			}

			/*
			 * If the current A does not have the right number of rows,
			 * re-create each column vector, adjusting to the new size.
			 */
			if (m != A.m) {
				for (k = 0; k < A.k; k++)
					if ((A.col[k] = create_svector(A.col[k], m)) == null) {
						System.out.println("Error recreating svectors.");
						return null;
					}
				A.m = m;
			}

			/*
			 * If A does not have the right maximum number of columns, adjust
			 * that.
			 */
			if (kmax != A.kmax) {
				A.col = new Svector[kmax];
				// if ((A.col = (svector**) realloc(A.col, kmax *
				// sizeof(svector*)))
				// == null) {
				// fprintf(stderr,
				// "Error enlarging number of columns in A matrix,\n");
				// return null;
				// }
				A.kmax = kmax;
			}

		} /* else - enlarging / shrinking A */

		return (A);

	} /* create_smatrix */

	/**
	 * Returns a pointer to an SDD for an m by n matrix. The maximum allowable
	 * number of terms is kmax. If A is non-null, then the first kmin terms are
	 * preserved. On error, returns NULL.
	 * 
	 * @param A
	 * @param m
	 * @param n
	 * @param kmin
	 * @param kmax
	 * @return
	 */
	private Sdd create_sdd(Sdd A, int m, int n, int kmin, int kmax) {
		/*  */

		/* Error checking */
		if ((m <= 0) || (n <= 0) || (kmin < 0) || (kmax <= 0) || (kmin > kmax)) {
			System.out.println("Error in size parameters for SDD.");
			return null;
		}

		if (A == null) { /* start from scratch */

			if (kmin != 0) {
				System.out
						.println("Warning: Specified nonzero existing kmax for new SDD.");
				kmin = 0;
			}

			/* Allocate space for A, and initialize pointers to NULL. */
			A = new Sdd();

		} /* if start from scratch */

		else { /* appending A */

			if (kmin > A.k) {
				System.out.println("Error in kmin parameter for SDD.");
				return null;
			}

		} /* appending A */

		/* Fill in A */
		A.m = m;
		A.n = n;
		A.k = kmin;
		A.kmax = kmax;
		if (((A.X = create_smatrix(A.X, kmin, kmax, m)) == null)
				|| ((A.Y = create_smatrix(A.Y, kmin, kmax, n)) == null)
				|| ((A.D = create_dmatrix(A.D, kmin, kmax)) == null)) {
			System.out.println("Error allocating memory for SDD components.");
			return null;
		}

		return (A);

	} /* create_sdd */

	/**
	 * Computes the SDD of A. If the sdd B is non-null, preserves the first kmin
	 * triplets, and then continues to expand. The SDD of A will be such that
	 * either the norm of the residual is less than rhomin or the number of
	 * terms is kmax. The inner iterations are controlled by alphamin (the
	 * improvement tolerance) and lmax (the maximum number of inner iterations).
	 * The initflag controls the method used to initialize y; the choices are
	 * ... (TGK - fill this in).
	 * 
	 * @param mat
	 * @param kmin
	 * @param kmax
	 * @param rhomin
	 * @param lmax
	 * @param alphamin
	 * @param initflag
	 * @return
	 * @throws IOException
	 */
	public Sdd compute_sdd(SparseMatrix A, int kmin, int kmax, double rhomin,
			int lmax, double alphamin, int initflag) throws IOException {
		/** current number of terms in of SDD */
		int k;
		/** size of matrix */
		int m, n;
		/** current d-value */
		double d;
		/** current x & y svectors */
		Svector x = null, y = null;
		/** solutions to subproblems */
		SddDouble_plus xmax = null, ymax = null;
		/** iteration count and total */
		int l, totall;
		/** square of initial residual norm */
		double rho0 = 0;
		/** square of residual norm */
		double rho = 0;
		/** improvement in change in residual */
		double alpha;
		/** change in residual, and previous */
		double beta, betabar;
		/** product A*y produced by initialization */
		double[] s = null;
		/** index used in initialization */
		int[] initidx = new int[] { 0 };

		Sdd B = null;

		if (A == null) {
			System.out.println("Error trying to compute SDD of NULL matrix.");
			return null;
		}
		if (kmin == kmax) {
			System.out.println("No expansion is necessary.");
			return null;
		}
		/* initialization of lengths */
		m = A.getCols();
		n = A.getRows();

		/* (Re)initialization of sdd decomposition (stored in B). */
		if ((B = create_sdd(B, m, n, kmin, kmax)) == null) {
			System.out.println("Error initializing SDD.");
			return null;
		}

		/* Compute initial residual if kmin is zero. */
		rho = rho0 = fnormsq(A);

		/* Compute actual rho if expanding existing decomp. */
		if (kmin != 0) {
			System.out.println("rho0 = " + rho);
			for (k = 0; k < kmin; k++) {
				rho -= (B.D.d[k] * B.D.d[k]) * svcount(B.X.col[k])
						* svcount(B.Y.col[k]);
				System.out.println("rho[" + (k + 1) + "] = " + rho);
			}
		}

		totall = 0;

		/* Outer iterations */
		for (k = kmin; k < kmax; k++) {
			System.out.println("## k = " + k);
			if (((x = create_svector(null, m)) == null)
					|| ((y = create_svector(null, n)) == null)) {
				System.out.println("Error creating svectors.");
				return (null);
			}

			switch (initflag) {
			case 1: /* Threshold */
				s = init_threshold(y, A, B, rho, initidx);
				break;
			case 2: /* Cycling */
				init_cycle(y, k);
				break;
			case 3: /* All Ones */
				init_ones(y);
				break;
			case 4: /* Periodic Ones */
				init_pones(y);
				break;
			default:
				System.out.println("Error in initflag.");
				return null;
			}

			beta = betabar = 0;

			for (l = 0; l < lmax; l++) { /* inner iteration */
				xmax = subproblem(A, B, y, x, 0, s);
				// System.out.println("## xmax.idx = " + xmax.idx);
				// System.out.println("## xmax.val = " + xmax.val);
				s = null;
				ymax = subproblem(A, B, x, y, 1, null);
				// System.out.println("## ymax.idx = " + ymax.idx);
				// System.out.println("## ymax.val = " + ymax.val);

				beta = (ymax.val / (double) xmax.idx);

				// System.out.println("## l = " + l);
				// System.out.println("## rho = " + rho);
				// System.out.println("## beta = " + beta);
				// System.out.println("## betabar = " + betabar);

				if (l > 0) {
					alpha = (beta - betabar) / betabar;
					if (alpha < alphamin) {
						l++;
						break;
					}
				}
				betabar = beta;
				// break;
			} /* l-loop */

			d = ((Math.sqrt(ymax.val * ymax.idx)) / (double) (xmax.idx * ymax.idx));
			System.out.println("## d = " + d);
			expand_sdd(B, d, x, y);
			totall += l;
			rho = rho - beta;

			// System.out.println("--- " + rho + "\t" + rhomin + "\t" + beta
			// + "\t" + l + "\t" + totall);
			// if (totall == 69) {
			// break;// System.exit(1);
			// }

			if (rho <= rhomin) {
				if (rho < 0)
					rho = 0;
				k++;
				// System.out.println("## break");
				break;
			}
			// break;

		} /* end k-loop */

		return B;
	}

	/**
	 * Write A to the file named fname in text (bflag=0) or binary (bflag=1)
	 * format. The extra stuff (matrixf, edecompf, kmin, tol) is written in the
	 * comments for a text file. On error, print message.
	 * 
	 * @param A
	 * @param bflag
	 */
	// public void write_sdd(Sdd A, int bflag) {
	//
	// if (A == null) {
	// System.out.println("Error trying to write NULL SDD to file,");
	// return;
	// }
	//
	// /* Write out components of SDD */
	// BigDiagMatrix d = write_dmatrix(A.D);
	// TwoBitMatrix Vt = write_smatrix(A.X);
	// System.out.println();
	// TwoBitMatrix Ut = write_smatrix(A.Y);
	//
	// RealMatrix zvt = d.multiply(Vt);
	//
	// // /
	//
	// for (int i = 0; i < zvt.getColumnDimension(); i++) {
	//
	// double[] dq = new double[] { 0, 0, 0, 0, 0, 1, 1, 0 };
	// // 質問ベクトル
	// RealMatrix qm = new RealMatrixImpl(dq);
	// // 上部右行列
	// RealMatrix utdq = Ut.multiply(qm);
	// // System.out.println(utdq);
	// // 上部左行列
	// RealMatrix upleft = zvt.getColumnMatrix(i);
	// // System.out.println(upleft);
	//
	// RealMatrix upper = upleft.transpose().multiply(utdq);
	// // System.out.println(upper);
	//
	// // ノルムの計算
	// double nrmdl = upleft.getNorm();
	// double nrmdr = qm.getNorm();
	//
	// // 類頻度の計算
	//
	// double sim = upper.getNorm() / (nrmdl * nrmdr);
	// System.out.println(sim);
	// // fclose(fptr);
	// }
	// return;
	//
	// } /* write_sdd */
	/**
	 * Writes the smatrix A to the file pointed to by fptr in text (flag=0) or
	 * binary (bflag=1) format.
	 * 
	 * @param A
	 * @param bflag
	 */
	public CRSMatrix write_smatrix(Smatrix A) {

		int k; /* counter */
		CRSMatrix mat = new CRSMatrix(A.m, A.k);

		/* Simply write each svector to the file in sequence. */
		for (k = 0; k < A.k; k++)
			write_svector(A.col[k], mat);
		mat.next();
		mat.freez();
		return mat;

	} /* write_smatrix */

	/**
	 * Writes x to the file pointed to by fptr in text (bflag=0) or binary
	 * (bflag=1) format. On error, does nothing.
	 * 
	 * @param x
	 * @param bflag
	 */
	private void write_svector(Svector x, CRSMatrix mat) {

		int i; /* counter */
		int pvalptr = 0, psgnptr = 0;
		int[] valptr, sgnptr; /* local pointers */
		int mask; /* bit mask */
		int val, sgn; /* current val and sgn words */

		if (x == null) {
			System.out
					.println("Warning: Trying to write NULL svector to file.");
			return;
		}

		valptr = x.val;
		sgnptr = x.sgn;
		val = valptr[pvalptr];
		sgn = sgnptr[psgnptr];
		mask = ONE;

		/* Loop through each s-value in x. */
		for (i = 0; i < x.m; i++) {

			/* Print out the appropriate value. */
			if ((val & mask) != 0) {
				if ((sgn & mask) != 0) {
					// System.out.print("-1");
					// mat.addOneInv();
					mat.addValue(i, -1);
				} else {
					// System.out.print("1");
					// mat.addOne();
					mat.addValue(i, 1);
				}
			} else {
				// System.out.print("0");
				// mat.addZero();
			}
			// System.out.print("\t");

			/* Update mask, val, and sgn. */
			if (mask == MAXMASK) {
				mask = ONE;
				val = valptr[++pvalptr];
				sgn = sgnptr[++psgnptr];

				// fprintf(fptr, "\n");
				// System.out.println();
			} else
				mask <<= 1;

		} /* i-loop */

		if (mask != (ONE)) {
			// fprintf(fptr, "\n");
			// System.out.println();
		}
		mat.next();
		return;

	} /* write_svector */

	/**
	 * Writes D to the file pointed to by fptr in text (bflag=0) or binary
	 * (bflag=1) format. On error, prints message.
	 * 
	 * @param D
	 * @param bflag
	 */
	public BigDiagMatrix write_dmatrix(Dmatrix D) {

		// System.out.println("## D = \n" + D.toString());
		BigDiagMatrix d = new BigDiagMatrix(D.d);

		return d;

	} /* write_dmatrix */

	/**
	 * The following structure is used for sorting. The function qsortopt in
	 * sdd.c is specially modified for this datatype and is used if QSORTOPT is
	 * defined.
	 * 
	 * @author haruyosi
	 */
	private class SddDouble_plus {
		double val;
		int idx;
	}

	/**
	 * An s-vector is used to store an m-long array of s-values. Each s-value is
	 * represented by a val bit and a sgn bit.
	 * 
	 * <pre>
	 *  0: val = 0, sgn = undefined
	 * 	1: val = 1, sgn = 0
	 * -1: val = 1, val = 1
	 * </pre>
	 * 
	 * These bits are packed into ulong's to form the val and sgn arrays.
	 * 
	 * @author haruyosi
	 */
	public class Svector {
		/** number of entries stored */
		int m;
		/** zero (0) or non-zero (1) */
		int[] val;
		/** plus (0) or minus (1) */
		int[] sgn;

		@Override
		public String toString() {
			StringBuffer bf = new StringBuffer();
			for (int i = 0; i < val.length; i++) {
				bf.append((sgn[i] == 1) ? "-" : " ");
				bf.append(val[i]);
				bf.append("\t");
			}
			bf.append("\n");
			return bf.toString();
		}
	}

	public class Smatrix {
		int k, kmax;
		int m;
		Svector[] col;

		@Override
		public String toString() {
			StringBuffer bf = new StringBuffer();
			for (int i = 0; i < col.length; i++) {
				bf.append(col[i] != null ? col[i].toString() : "");
			}
			bf.append("\n");
			return bf.toString();
		}
	}

	public class Dmatrix {
		int k, kmax;
		public double[] d;

		@Override
		public String toString() {
			StringBuffer bf = new StringBuffer();
			for (int i = 0; i < d.length; i++) {
				bf.append(d[i]);
				bf.append("\t");
			}
			bf.append("\n");
			return bf.toString();
		}
	}

	public class Sdd {
		int m, n, k, kmax;
		public Smatrix X, Y;
		public Dmatrix D;
	}

	/**
	 * クイックソートアルゴリズム
	 * 
	 * @author haruyosi
	 */
	public class Qsort {
		static final int STACKSIZE = Double.SIZE; // たかだか int のビット数程度
		static final int THRESHOLD = 10; // 要素数がこれ以下の部分は

		// 挿入ソートに任せる

		public void reverse(SddDouble_plus a[]) {

			SddDouble_plus t;
			for (int i = 0; i < a.length / 2; i++) {
				t = a[i];
				a[i] = a[a.length - i - 1];
				a[a.length - i - 1] = t;
			}
		}

		/** a[] を昇順に */
		public void sort(SddDouble_plus a[]) {
			int left = 0, right = a.length - 1, p = 0;
			int[] leftstack = new int[STACKSIZE];
			int[] rightstack = new int[STACKSIZE];

			InsertSort isort = new InsertSort();

			for (;;) {
				if (right - left <= THRESHOLD) {
					if (p == 0)
						break;
					p--;
					left = leftstack[p];
					right = rightstack[p];
				}
				double x = a[(left + right) / 2].val;
				int i = left, j = right;
				for (;;) {
					while (a[i].val < x)
						i++;
					while (x < a[j].val)
						j--;
					if (i >= j)
						break;
					SddDouble_plus swap = a[i];
					a[i] = a[j];
					a[j] = swap;
					i++;
					j--;
				}
				if (i - left > right - j) {
					if (i - left > THRESHOLD) {
						leftstack[p] = left;
						rightstack[p] = i - 1;
						p++;
					}
					left = j + 1;
				} else {
					if (right - j > THRESHOLD) {
						leftstack[p] = j + 1;
						rightstack[p] = right;
						p++;
					}
					right = i - 1;
				}
			}
			isort.sort(a);
		}
	}

	/**
	 * 挿入ソート
	 * 
	 */
	private class InsertSort {
		/** a[] を昇順に */
		private void sort(SddDouble_plus a[]) {
			for (int i = 1; i < a.length; i++) {
				int j;
				SddDouble_plus x = a[i];
				for (j = i - 1; j >= 0 && a[j].val > x.val; j--)
					a[j + 1] = a[j];
				a[j + 1] = x;
			}
		}
	}
}

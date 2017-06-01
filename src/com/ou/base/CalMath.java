package com.ou.base;

import com.ou.common.ComFunc;
import com.ou.common.Constant;

import android.graphics.PointF;
import android.graphics.Rect;

public class CalMath {
	public CalMath() {
		
	}

	public byte [] getCalResult(int width, int height, PointF []calP, PointF [] screenP) {
		PointF[] calps = transferCalPoint(calP);
		PointF[] screenps = transferScreenPoint(screenP, width, height);
		int [] rcv = new int[8];
		boolean r = CalEquation(calps, screenps, rcv);
		if (r == false) {
			ComFunc.log("CalEquation error");
			return null;
		}
		
		byte [] ret = ComFunc.intsToBytes(rcv);
		return ret;
	}
	public PointF[] transferCalPoint(PointF[] calP) {
		PointF[] tmp = new PointF[calP.length];
		for (int i = 0; i < calP.length; i++) {
			tmp[i] = new PointF();
			tmp[i].x = (short) calP[i].x >> 5;
			tmp[i].y = (short) calP[i].y >> 5;
		}
		return tmp;
	}

	public PointF[] transferScreenPoint(PointF[] ps, int width, int height) {
		PointF[] tmp = new PointF[ps.length];
		for (int i = 0; i < ps.length; i++) {
			tmp[i] = new PointF();
			tmp[i].x = (short) ((ps[i].x * 1024) / width);
			tmp[i].y = (short) ((ps[i].y * 1024) / height);
		}
		return tmp;
	}

	boolean CalEquation(PointF[] src_pnt, PointF[] dst_pnt, int[] X) 
	{
		boolean r = false;
		int i = 0;
		double[] A = new double[8 * 8 * 8];
		double[] B = new double[8 * 8];
		double[] C = new double[8 * 8];

		for (i = 0; i < 8; i++) {
			if (i % 2 == 0) {
				A[i * 8] = src_pnt[i / 2].x;
				A[i * 8 + 1] = src_pnt[i / 2].y;
				A[i * 8 + 2] = 1;
				A[i * 8 + 3] = 0;
				A[i * 8 + 4] = 0;
				A[i * 8 + 5] = 0;
				A[i * 8 + 6] = -src_pnt[i / 2].x * dst_pnt[i / 2].x;
				A[i * 8 + 7] = -dst_pnt[i / 2].x * src_pnt[i / 2].y;

				B[i] = dst_pnt[i / 2].x;
			} else {
				A[i * 8] = 0;
				A[i * 8 + 1] = 0;
				A[i * 8 + 2] = 0;
				A[i * 8 + 3] = src_pnt[(i - 1) / 2].x;
				A[i * 8 + 4] = src_pnt[(i - 1) / 2].y;
				;
				A[i * 8 + 5] = 1;
				A[i * 8 + 6] = -src_pnt[(i - 1) / 2].x * dst_pnt[(i - 1) / 2].y;
				A[i * 8 + 7] = -src_pnt[(i - 1) / 2].y * dst_pnt[(i - 1) / 2].y;

				B[i] = dst_pnt[(i - 1) / 2].y;
			}
		}

		r = MatrixInverse(A, 8);
		if (r == false) {
			ComFunc.log("MatrixInverse err");
			return false;
		}
		r = MatrixMulti(A, 8, 8, B, 8, 1, C);
		if (r == false) {
			ComFunc.log("MatrixMulti err");
			return false;
		}
		
		for (i = 0; i < 8; i++) {
			X[i] = (int) (C[i] * (1 << 20) + 0.5);
		}
		return true;

	}

	boolean MatrixMulti(double[] m1, int row1, int col1, double[] m2, int row2, int col2, double[] m3) {
		int i, j, k;

		if (col1 != row2)
			return false;
		for (i = 0; i < row1; ++i)
			for (j = 0; j < col2; ++j) {
				double sum = 0;
				for (k = 0; k < col1; ++k)
					sum += m1[i * col1 + k] * m2[k * col2 + j];
				m3[i * col2 + j] = sum;
			}

		return true;
	}

	double fabs(double value) {
		if (value < 0.0) {
			return -value;
		}

		return value;
	}

	boolean MatrixInverse(double[] m1, int row) {
		int i, j, k;
		double div, temp;
		// double *out;
		int[] is;
		int[] js;

		is = new int[4 * row];
		js = new int[4 * row];

		for (i = 0; i < row; ++i) {
			is[i] = i;
			js[i] = i;
		}

		// start from first column to the next
		for (k = 0; k < row; ++k) {
			div = 0;
			for (i = k; i < row; ++i) {
				for (j = k; j < row; ++j) {
					if (fabs(m1[i * row + j]) > div) {
						div = fabs(m1[i * row + j]);
						is[k] = i;
						js[k] = j;
					}
				}
			}

			if (fabs(div) < 1e-10) {
				ComFunc.log("err div:" + fabs(div));
				return false;
			}
			if (is[k] != k) {
				for (j = 0; j < row; ++j) {
					temp = m1[k * row + j];
					m1[k * row + j] = m1[is[k] * row + j];
					m1[is[k] * row + j] = temp;
				}
			}
			if (js[k] != k) {
				for (i = 0; i < row; ++i) {
					temp = m1[i * row + k];
					m1[i * row + k] = m1[i * row + js[k]];
					m1[i * row + js[k]] = temp;
				}
			}
			m1[k * row + k] = 1 / m1[k * row + k];
			for (j = 0; j < row; ++j) {
				if (j != k)
					m1[k * row + j] = m1[k * row + j] * m1[k * row + k];
			}
			for (i = 0; i < row; ++i) {
				if (i != k) {
					for (j = 0; j < row; ++j) {
						if (j != k)
							m1[i * row + j] -= m1[i * row + k] * m1[k * row + j];
					}
				}
			}
			for (i = 0; i < row; ++i) {
				if (i != k)
					m1[i * row + k] = -m1[i * row + k] * m1[k * row + k];
			}
		}

		for (k = row - 1; k >= 0; --k) {
			for (j = 0; j < row; ++j)
				if (js[k] != k) {
					temp = m1[k * row + j];
					m1[k * row + j] = m1[js[k] * row + j];
					m1[js[k] * row + j] = temp;
				}
			for (i = 0; i < row; ++i)
				if (is[k] != k) {
					temp = m1[i * row + k];
					m1[i * row + k] = m1[i * row + is[k]];
					m1[i * row + is[k]] = temp;
				}
		}

		return true;
	}
	
	public Rect[] getCalKeyPosition(int zone, ShortCutPointGroup grp) {
		float offset;
		float height;
		int sign_bit = 1;
		int j = 0;
		int cnt = grp.getCount();
		Rect[] rects = new Rect[cnt];
		for (int i = 0; i < cnt; i++) {
			rects[i] = new Rect();
		}
		
		int index = 0;
		rects[index].left = (int) grp.getFirst().x;
		rects[index].right = (int) grp.getFirst().y;

		rects[index].right = (int) grp.getSecond().x;
		rects[index].bottom = (int) grp.getSecond().y;
		index += 1;

		if (cnt < 1)
			return rects;

		if (zone == Constant.ZONE_LEFT || zone == Constant.ZONE_RIGHT) {
			height = (grp.getThird().y - grp.getFirst().y);
			offset = (grp.getThird().x - grp.getSecond().x);
		} else {
			height = (grp.getThird().x - grp.getFirst().x);
			offset = (grp.getThird().y - grp.getSecond().y);
		}
		sign_bit = (offset >= 0) ? 1 : (-1);
		for (j = 0; j < cnt - 1; j++) {
			if (zone == Constant.ZONE_LEFT || zone == Constant.ZONE_RIGHT) {
				rects[index].left = (int) (grp.getFirst().x + (offset * j + sign_bit * cnt / 2) / (cnt - 1));
				rects[index].right = (int) (grp.getFirst().y + (height * j + cnt / 2) / (cnt - 1));

				rects[index].right = (int) (grp.getSecond().x + (offset * j + sign_bit * cnt / 2) / (cnt - 1));
				rects[index].bottom = (int) (grp.getSecond().y + (height * j + cnt / 2) / (cnt - 1));
			} else {
				rects[index].left = (int) (grp.getFirst().x + (height * j + cnt / 2) / (cnt - 1));
				rects[index].right = (int) (grp.getFirst().y + (offset * j + sign_bit * cnt / 2) / (cnt - 1));

				rects[index].right = (int) (grp.getSecond().x + (height * j + cnt / 2) / (cnt - 1));
				rects[index].bottom = (int) (grp.getSecond().y + (offset * j + sign_bit * cnt / 2) / (cnt - 1));
			}
			index++;
		}
		return rects;
	}


}

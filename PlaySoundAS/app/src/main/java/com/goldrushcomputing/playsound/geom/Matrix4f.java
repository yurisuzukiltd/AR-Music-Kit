package com.goldrushcomputing.playsound.geom;

//====================================
//            [Matrix4f]
// 
// メンバの並び方は、m00, m10, ...
// と縦列単位になっている
// +------------------+
// |m00, m01, m02, m03|
// |m10, m11, m12, m13|
// |m20, m21, m22, m23|
// |m30, m31, m32, m33|
// +------------------+
//====================================
public class Matrix4f {
	public float m00; // 1行1列
	public float m10; // 2行1列
	public float m20; // 3行1列
	public float m30; // 4行1列

	public float m01;
	public float m11;
	public float m21;
	public float m31;

	public float m02;
	public float m12;
	public float m22;
	public float m32;

	public float m03;
	public float m13;
	public float m23;
	public float m33;

	public Matrix4f() {
	}

	public void set(Matrix4f m) {
		m00 = m.m00;
		m10 = m.m10;
		m20 = m.m20;
		m30 = m.m30;

		m01 = m.m01;
		m11 = m.m11;
		m21 = m.m21;
		m31 = m.m31;

		m02 = m.m02;
		m12 = m.m12;
		m22 = m.m22;
		m32 = m.m32;

		m03 = m.m03;
		m13 = m.m13;
		m23 = m.m23;
		m33 = m.m33;
	}

	public void set(float[] m) {
		m00 = m[0];
		m10 = m[1];
		m20 = m[2];
		m30 = m[3];

		m01 = m[4];
		m11 = m[5];
		m21 = m[6];
		m31 = m[7];

		m02 = m[8];
		m12 = m[9];
		m22 = m[10];
		m32 = m[11];

		m03 = m[12];
		m13 = m[13];
		m23 = m[14];
		m33 = m[15];
	}

	public void set(float m00_, float m10_, float m20_, float m30_, float m01_, float m11_, float m21_, float m31_,
	                float m02_, float m12_, float m22_, float m32_, float m03_, float m13_, float m23_, float m33_) {
		m00 = m00_;
		m10 = m10_;
		m20 = m20_;
		m30 = m30_;
		m01 = m01_;
		m11 = m11_;
		m21 = m21_;
		m31 = m31_;
		m02 = m02_;
		m12 = m12_;
		m22 = m22_;
		m32 = m32_;
		m03 = m03_;
		m13 = m13_;
		m23 = m23_;
		m33 = m33_;
	}

	public void mul(Matrix4f m) {
		set(m00 * m.m00 + m01 * m.m10 + m02 * m.m20 + m03 * m.m30, m10 * m.m00 + m11 * m.m10 + m12 * m.m20 + m13
						* m.m30, m20 * m.m00 + m21 * m.m10 + m22 * m.m20 + m23 * m.m30, m30 * m.m00 + m31 * m.m10 + m32 * m.m20
						+ m33 * m.m30,

				m00 * m.m01 + m01 * m.m11 + m02 * m.m21 + m03 * m.m31, m10 * m.m01 + m11 * m.m11 + m12 * m.m21 + m13 * m.m31,
				m20 * m.m01 + m21 * m.m11 + m22 * m.m21 + m23 * m.m31, m30 * m.m01 + m31 * m.m11 + m32 * m.m21 + m33
						* m.m31,

				m00 * m.m02 + m01 * m.m12 + m02 * m.m22 + m03 * m.m32, m10 * m.m02 + m11 * m.m12 + m12 * m.m22 + m13
						* m.m32, m20 * m.m02 + m21 * m.m12 + m22 * m.m22 + m23 * m.m32, m30 * m.m02 + m31 * m.m12 + m32
						* m.m22 + m33 * m.m32,

				m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03 * m.m33, m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13
						* m.m33, m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23 * m.m33, m30 * m.m03 + m31 * m.m13 + m32
						* m.m23 + m33 * m.m33);
	}

	public void transform(Vector4f vin, Vector4f vout) {
		vout.set(m00 * vin.x + m01 * vin.y + m02 * vin.z + m03 * 1.0f,
				m10 * vin.x + m11 * vin.y + m12 * vin.z + m13 * 1.0f,
				m20 * vin.x + m21 * vin.y + m22 * vin.z + m23 * 1.0f,
				m30 * vin.x + m31 * vin.y + m32 * vin.z + m33 * 1.0f);
	}

	public String toString() {
		String ret = m00 + ", " + m01 + ", " + m02 + ", " + m03 + "\n" + m10 + ", " + m11 + ", " + m12 + ", " + m13
				+ "\n" + m20 + ", " + m21 + ", " + m22 + ", " + m23 + "\n" + m30 + ", " + m31 + ", " + m32 + ", " + m33;
		return ret;
	}
}

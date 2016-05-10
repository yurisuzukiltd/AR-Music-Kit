/*
 *  Author(s): Kosuke Miyoshi, Narrative Nights
 */

package com.yurisuzuki.geom;

public class Vector4f {
	public float x;
	public float y;
	public float z;
	public float w;
	
	public Vector4f() {
	}
	
	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4f(Vector4f v) {
		this.x = v.x;
		this.x = v.y;
		this.x = v.z;
		this.w = v.w;
	}

	public void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public void set(Vector4f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.w = v.w;
	}
	
	public void set(float[] v) {
		this.x = v[0];
		this.y = v[1];
		this.z = v[2];
		this.w = v[3];
	}

	@Override
	public String toString() {
		return "Vector4f [x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + "]";
	}
}

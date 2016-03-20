package org.andresoviedo.app.model3D.model;

public interface Object3D {

	float[] getPosition();

	void setPosition(float[] position);

	float[] getColor();

	void setColor(float[] color);

	void draw(float[] mvpMatrix, float[] mvMatrix);

	void draw(float[] mvpMatrix, float[] mvMatrix, int drawType, int drawSize);

	void drawBoundingBox(float[] mvpMatrix, float[] mvMatrix);

	void translateX(float f);

	void translateY(float f);

	float[] getRotation();

	void setRotationZ(float rz);

	float getRotationZ();

	void setRotation(float[] rotation);

}
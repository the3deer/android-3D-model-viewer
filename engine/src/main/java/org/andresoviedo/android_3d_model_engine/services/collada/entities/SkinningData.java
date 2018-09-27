package org.andresoviedo.android_3d_model_engine.services.collada.entities;

import java.util.List;

public class SkinningData {

    public final float[] bindShapeMatrix;
	public final List<String> jointOrder;
	public final List<VertexSkinData> verticesSkinData;
	public final float[] inverseBindMatrix;

	public SkinningData(float[] bindShapeMatrix, List<String> jointOrder, List<VertexSkinData> verticesSkinData, float[] inverseBindMatrix){
	    this.bindShapeMatrix = bindShapeMatrix;
		this.jointOrder = jointOrder;
		this.verticesSkinData = verticesSkinData;
		this.inverseBindMatrix = inverseBindMatrix;
	}


}

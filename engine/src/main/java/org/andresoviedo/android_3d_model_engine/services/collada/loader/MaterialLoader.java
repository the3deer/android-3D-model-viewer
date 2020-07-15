package org.andresoviedo.android_3d_model_engine.services.collada.loader;

import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Element;
import org.andresoviedo.android_3d_model_engine.model.Material;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.MeshData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkeletonData;
import org.andresoviedo.util.xml.XmlNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaterialLoader {

    private final XmlNode materialsData;
    private final XmlNode imagesNode;
    private final XmlNode effectsData;

    public MaterialLoader(XmlNode materialsNode, XmlNode effectsNode, XmlNode imagesNode) {
        this.materialsData = materialsNode;
        this.imagesNode = imagesNode;
        this.effectsData = effectsNode;
    }

    public void loadMaterial(MeshData meshData){

        // log event
        Log.i("MaterialLoader", "Loading materials for " + meshData.getId() + " ("+ meshData.getName() +")...");

        int i=0;
        for (Element element : meshData.getElements()) {

            // if no linked material in < mesh > do nothing
            final String materialId = element.getMaterialId();
            if (materialId == null) {
                continue;
            }

            // log event
            Log.i("MaterialLoader", "Loading material '"+materialId+"' for element: "+i++);

            // parse material
            element.setMaterial(parseMaterial(materialId));

            // log event
            Log.i("MaterialLoader", "Material '"+materialId+"' for element: "+(i++)+": "+element.getMaterial());
        }
    }

    public void loadMaterialFromVisualScene(MeshData meshData, SkeletonData skeletonData){

        // if no joints do nothing
        if (skeletonData == null) return;

        // get mesh id to update
        final String geometryId = meshData.getId();
        final String geometryName = meshData.getName();

        // log event
        Log.i("MaterialLoader", "Loading materials for " + geometryId + " ("+geometryName+")...");

        int i=0;
        for (Element element : meshData.getElements()) {

            // if no linked material in < mesh > do nothing
            final String materialId = element.getMaterialId();
            if (materialId == null) {
                continue;
            }

            // log event
            Log.i("MaterialLoader", "Loading instance material '"+materialId+"' for element: "+i++);

            // parse material
            final Material material = parseInstanceMaterial(geometryId, geometryName, materialId, skeletonData);
            if (material != null) {

                // if there is instance material, overwrite
                element.setMaterial(material);

                // log event
                Log.i("MaterialLoader", "Instance material '"+materialId+"' for element: "+(i++)+": "+element.getMaterial());
            }
            else {
                // log event
                Log.i("MaterialLoader", "Instance material '" + materialId + "' for element: " + (i++) + " not found");
            }
        }
    }


    private Material parseInstanceMaterial(String geometryId, String geometryName, String material, SkeletonData skeletonData){
        if (skeletonData != null) {
            JointData jointData = skeletonData.find(geometryId);
            if (jointData == null && geometryName != null) {
                jointData = skeletonData.find(geometryName);
            }
            if (jointData != null && jointData.containsMaterial(material)) {
                return parseMaterial(jointData.getMaterial(material));
            }
        }
        return null;
    }

    private Material parseMaterial(String materialId) {
        try {

            // lookup material node
            XmlNode materialNode = materialsData.getChildWithAttribute("material", "id", materialId);
            if (materialNode == null) {
                materialNode = materialsData.getChildWithAttribute("material", "name", materialId);
            }
            if (materialNode == null) {
                return null;
            }

            // lookup instance_effect
            XmlNode instanceEffectNode = materialNode.getChild("instance_effect");
            String instanceEffectId = instanceEffectNode.getAttribute("url").substring(1);
            XmlNode effect = effectsData.getChildWithAttribute("effect", "id", instanceEffectId);
            XmlNode profile_common = effect.getChild("profile_COMMON");

            // get technique implementation
            XmlNode technique = profile_common.getChild("technique");
            XmlNode techniqueImpl = null;
            if (technique.getChild("lambert") != null) {
                techniqueImpl = technique.getChild("lambert");
            } else if (technique.getChild("phong") != null) {
                techniqueImpl = technique.getChild("phong");
            } else if (technique.getChild("blinn") != null) {
                techniqueImpl = technique.getChild("blinn");
            }

            // get ambient
			/*XmlNode colorAmbientNode = null;
			if (techniqueImpl != null){
				XmlNode ambient = techniqueImpl.getChild("ambient");
				if (ambient != null){
					colorAmbientNode = ambient.getChild("color");
				}
			}*/

            // get diffuse
            XmlNode diffuse = null;
            XmlNode transparency = null;
            if (techniqueImpl != null) {
                diffuse = techniqueImpl.getChild("diffuse");
                transparency = techniqueImpl.getChild("transparency");
            }

            // get color & texture
            XmlNode colorNode = null;
            XmlNode textureNode = null;
            if (diffuse != null) {
                colorNode = diffuse.getChild("color");
                textureNode = diffuse.getChild("texture");
            }

            // parse color
            float[] color = null;
            float alpha = -1;
            if (colorNode != null) {
                String[] colorData = colorNode.getData().trim().replace(',', '.').split("\\s+");
                color = new float[]{Float.parseFloat(colorData[0]), Float.parseFloat(colorData[1]), Float.parseFloat(colorData[2]), Float
                        .parseFloat(colorData[3])};
                alpha = Float.parseFloat(colorData[3]);
                Log.v("MaterialLoader","Color: "+Arrays.toString(color));
            }

            // parse transparency
            if (color != null && transparency != null) {
                if (transparency.getChild("float") != null) {
                    final float aFloat = Float.parseFloat(transparency.getChild("float").getData().replace(',', '.'));
                    alpha = aFloat;
                    Log.v("MaterialLoader","Transparency: "+aFloat);
                }
            }

            // get texture image
            String textureFile = null;
            if (textureNode != null) {
                String texture = textureNode.getAttribute("texture");
                XmlNode newParamNode = profile_common.getChildWithAttribute("newparam", "sid", texture);
                if (newParamNode != null) {
                    String surface = newParamNode.getChild("sampler2D").getChild("source").getData();
                    newParamNode = profile_common.getChildWithAttribute("newparam", "sid", surface);
                    String imageRef = newParamNode.getChildWithAttribute("surface", "type", "2D").getChild("init_from").getData();
                    textureFile = imagesNode.getChildWithAttribute("image", "id", imageRef).getChild("init_from")
                            .getData();
                } else {
                    // TODO: is this ok?
                    textureFile = imagesNode.getChildWithAttribute("image", "id", texture).getChild("init_from").getData();
                }
            }

            if (color == null && textureFile == null){
                Log.v("MaterialLoader", "Color nor texture found: " + materialId);
                return null;
            }

            final Material ret = new Material(materialId);
            ret.setDiffuse(color);
            ret.setAlpha(alpha);
            ret.setTextureFile(textureFile);
            return ret;

            // fallback to ambient color
			/*if (colorAmbientNode != null){
				String colorString = colorAmbientNode.getData().trim();
				String[] color = colorString.split("\\s+");
				ret[0] = new float[]{Float.valueOf(color[0]), Float.valueOf(color[1]), Float.valueOf(color[2]), Float
						.valueOf(color[3])};
				Log.d("GeometryLoader","Ambient color '"+colorString+"'");
			}*/

        } catch (Exception ex) {
            Log.e("MaterialLoader", "Error reading material '" + materialId + "'", ex);
            return null;
        }
    }

    public List<String> getImages() {
        final List<String> ret = new ArrayList<>();
        try {
            final List<XmlNode> images = imagesNode.getChildren("image");
            for (int i = 0; i< images.size(); i++){
                ret.add(images.get(i).getChild("init_from").getData());
            }
        } catch (Exception e) {
            return null;
        }
        return ret;
    }
}

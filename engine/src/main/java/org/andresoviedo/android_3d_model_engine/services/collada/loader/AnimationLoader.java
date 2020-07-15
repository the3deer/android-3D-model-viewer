package org.andresoviedo.android_3d_model_engine.services.collada.loader;

import android.opengl.Matrix;
import androidx.annotation.NonNull;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.animation.Animation;
import org.andresoviedo.android_3d_model_engine.animation.JointTransform;
import org.andresoviedo.android_3d_model_engine.animation.KeyFrame;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointTransformData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.KeyFrameData;
import org.andresoviedo.util.xml.XmlNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


public class AnimationLoader {

    // list of animations
    private final XmlNode library_animations;

    // duration of animation
    private float duration;

    // list of key frames
    private List<Float> keyTimes;
    private KeyFrameData[] keyFrames;

    public AnimationLoader(XmlNode xml) {
        this.library_animations = xml.getChild("library_animations");
    }

    /**
     * @return <code>true</code> if there is any animation declared
     */
    public boolean isAnimated() {
        return this.library_animations != null && !this.library_animations.getChildren("animation").isEmpty();
    }

    public Animation load() {

        if (!isAnimated()) return null;

        Animation ret = null;
        try {
            Log.i("AnimationLoader", "Loading animation...");

            loadTransforms();

			KeyFrame[] frames = buildKeyFrames();

			ret = new Animation(this.duration, frames);
            Log.i("AnimationLoader", "Loaded animation: " + ret);
        } catch (Exception ex) {
            Log.e("AnimationLoader", "Error loading animation", ex);
        }
        return ret;
    }

	private void loadTransforms() {
        Log.i("AnimationLoader", "Loading key times...");
        TreeSet<Float> timesSorted = getKeyTimes();
        Log.i("AnimationLoader", "Loaded key times: (" + timesSorted.size() + "): " + timesSorted);

        this.duration = timesSorted.last();
        Log.i("AnimationLoader", "Animation length: " + duration);

        Log.d("AnimationLoader", "Loading key frames...");
        this.keyFrames = new KeyFrameData[timesSorted.size()];
        int i = 0;
        for (Float time : timesSorted) {
            this.keyFrames[i++] = new KeyFrameData(time);
        }

        this.keyTimes = new ArrayList<>(timesSorted);
        List<XmlNode> animationNodes = library_animations.getChildren("animation");
        Log.i("AnimationLoader", "Loading animations... Total: " + animationNodes.size());
        for (XmlNode animationNode : animationNodes) {

            if (animationNode.getChildren("animation").isEmpty()){
                loadJointTransforms(animationNode);
                continue;
            }

            // INFO: Raptor / Iris models have nested nodes.
            for (XmlNode animationNestedNode : animationNode.getChildren("animation")) {
                loadJointTransforms(animationNestedNode);
            }
        }
    }

	/**
	 * Object and bone transformation in Blender are applied in the order of scale, rotation, translation.
	 * As a matrix this would be written like this:
	 * <p>
	 * {@code}object_matrix = translation_matrix * rotation_matrix * scale_matrix{@code}
	 * <p>
	 * Note that this order ensures there is no shearing, which happens when you do scaling after rotation.
	 * <p>
	 * If parenting is included in the matrix, multiple such object or bone matrices might be multiplied together,
	 * which means there is no longer a clear correct way to decompose the matrix into scale, rotation and translation.
	 */
	@NonNull
	private KeyFrame[] buildKeyFrames() {
		KeyFrame[] frames = new KeyFrame[this.keyFrames.length];
		for (int i = 0; i < frames.length; i++) {
			final Map<String, JointTransform> map = new HashMap<>();

			// process matrix transformation (full transformation)
			for (JointTransformData jointData : this.keyFrames[i].jointTransforms) {
				if (jointData.matrix == null) continue;

				final JointTransform current = map.get(jointData.jointId);
				final float[] matrix;
				if (current == null) {
					matrix =  new float[16];
					Matrix.setIdentityM(matrix, 0);
				} else {
					// accumulate transformations
					matrix = current.getMatrix();
				}
				float[] newMatrix = new float[16];
				Matrix.multiplyMM(newMatrix, 0, jointData.matrix, 0, matrix, 0);
				final JointTransform jointTransform = new JointTransform(newMatrix);
				map.put(jointData.jointId, jointTransform);
			}

            // process location transformation
            for (JointTransformData jointData : this.keyFrames[i].jointTransforms) {
                if (jointData.location == null) continue;

                JointTransform current = map.get(jointData.jointId);
                if (current == null) {
                    map.put(jointData.jointId, JointTransform.ofLocation(jointData.location));
                } else {
                    current.addLocation(jointData.location);
                }
            }

            // process rotation transformation
            for (JointTransformData jointData : this.keyFrames[i].jointTransforms) {
                if (jointData.rotation == null) continue;

                JointTransform current = map.get(jointData.jointId);
                if (current == null) {
                    map.put(jointData.jointId, JointTransform.ofRotation(jointData.rotation));
                } else {
                    current.addRotation(jointData.rotation);
                }
            }

            // process scale transformation
            for (JointTransformData jointData : this.keyFrames[i].jointTransforms) {
                if (jointData.scale == null) continue;

                JointTransform current = map.get(jointData.jointId);
                if (current == null) {
                    map.put(jointData.jointId, JointTransform.ofScale(jointData.scale));
                } else {
                    current.addScale(jointData.scale);
                }
            }

			frames[i] = new KeyFrame(this.keyFrames[i].time, map);

            // log event
            if (i<10) {
                Log.d("AnimationLoader", "Loaded Keyframe: " + frames[i]);
            } else if (i==11){
                Log.d("AnimationLoader", "Loaded Keyframe... (omitted)");
            }
		}
		return frames;
	}

    /**
     * Process all animations and combine all key frames in order to have the global list of times
     *
     * @return list of key times sorted ascending (0 --> infinite)
     */
    private TreeSet<Float> getKeyTimes() {
        TreeSet<Float> ret = new TreeSet<>();
        for (XmlNode animation : library_animations.getChildren("animation")) {
            if (animation.getChild("animation") != null) {
                animation = animation.getChild("animation");
            }
            XmlNode timeData = animation.getChild("source").getChild("float_array");
            String[] rawTimes = timeData.getData().trim().split("\\s+");
            for (String rawTime : rawTimes) {
                ret.add(Float.parseFloat(rawTime));

            }
        }
        return ret;
    }


    private void loadJointTransforms(XmlNode animationNode) {
        Log.v("AnimationLoader", "Loading animation... id: " + animationNode.getAttribute("id"));
        String[] target = getTarget(animationNode);
        String jointNameId = target[0];
        String transform = target[1];
        String input = getInput(animationNode);
        String output = getOutput(animationNode);
        try {
            XmlNode timeData = animationNode.getChildWithAttribute("source", "id", input);
            String[] rawTimes = timeData.getChild("float_array").getData().trim().split("\\s+");
            XmlNode transformData = animationNode.getChildWithAttribute("source", "id", output);
            String[] rawData = transformData.getChild("float_array").getData().trim().split("\\s+");
            XmlNode technique_common = transformData.getChild("technique_common");
            XmlNode accessor = technique_common.getChild("accessor");
            String stride = accessor.getAttribute("stride") != null? accessor.getAttribute("stride") : "1";
            if (stride.equals("16")) {
                processMatrixTransforms(jointNameId, rawTimes, rawData);
            } else if (transform.equals("scale.X")) {
                process_scale_X(jointNameId, rawTimes, rawData);
            } else if (transform.equals("scale.Y")) {
                process_scale_Y(jointNameId, rawTimes, rawData);
            } else if (transform.equals("scale.Z")) {
                process_scale_Z(jointNameId, rawTimes, rawData);
            } else if (transform.equals("rotationX.ANGLE") || transform.equals("rotateX.ANGLE")) {
                process_rotation_X(jointNameId, rawTimes, rawData);
            } else if (transform.equals("rotationY.ANGLE") || transform.equals("rotateY.ANGLE")) {
                process_rotation_Y(jointNameId, rawTimes, rawData);
            } else if (transform.equals("rotationZ.ANGLE") || transform.equals("rotateZ.ANGLE")) {
                process_rotation_Z(jointNameId, rawTimes, rawData);
            } else if (transform.equals("location.X") || transform.equals("translate.X")) {
                process_location_X(jointNameId, rawTimes, rawData);
            } else if (transform.equals("location.Y") || transform.equals("translate.Y")) {
                process_location_Y(jointNameId, rawTimes, rawData);
            } else if (transform.equals("location.Z") || transform.equals("translate.Z")) {
                process_location_Z(jointNameId, rawTimes, rawData);
            }
            Log.v("AnimationLoader", "Animation (key frames: " + rawTimes.length + ") " + jointNameId);
        } catch (Exception e) {
            Log.e("AnimationLoader", "Problem loading animation for joint '" + jointNameId + "' with source '" + output + "'", e);
            throw new RuntimeException(e);
        }
    }

    private String getOutput(XmlNode jointData) {
        XmlNode node = jointData.getChild("sampler").getChildWithAttribute("input", "semantic", "OUTPUT");
        return node.getAttribute("source").substring(1);
    }

    /**
     * Input are all the time key frames
     *
     * @param animationNode animation node
     * @return raw key frames
     */
    private String getInput(XmlNode animationNode) {
        XmlNode node = animationNode.getChild("sampler").getChildWithAttribute("input", "semantic", "INPUT");
        return node.getAttribute("source").substring(1);
    }

    /**
     * Target is the bone to animate
     *
     * @param animationNode animation node
     * @return bone + transformation name
     */
    private String[] getTarget(XmlNode animationNode) {
        XmlNode channelNode = animationNode.getChild("channel");
        String data = channelNode.getAttribute("target");
        return data.split("/");
    }

    private void processMatrixTransforms(String jointName, String[] rawTimes, String[] rawData) {

        float[] tempMatrix = new float[16];

        for (int i = 0; i < rawTimes.length; i++) {

            // parse matrix
            for (int j = 0; j < 16; j++) {
                tempMatrix[j] = Float.parseFloat(rawData[i * 16 + j]);
            }

            // transpose matrix
            float[] transpose = new float[16];
            Matrix.transposeM(transpose, 0, tempMatrix, 0);

            // add transform
            keyFrames[keyTimes.indexOf(Float.parseFloat(rawTimes[i]))].
                    addJointTransform(JointTransformData.ofMatrix(jointName, transpose));
        }
    }

    private void process_scale_X(String jointName, String[] rawTimes, String[] rawData) {
        for (int i = 0; i < rawTimes.length; i++)
            keyFrames[keyTimes.indexOf(Float.parseFloat(rawTimes[i]))].
					addJointTransform(JointTransformData.ofScale(jointName, new Float[]{Float.parseFloat(rawData[i]), null, null}));
    }

    private void process_scale_Y(String jointName, String[] rawTimes, String[] rawData) {
        for (int i = 0; i < rawTimes.length; i++) {
            keyFrames[keyTimes.indexOf(Float.parseFloat(rawTimes[i]))].
					addJointTransform(JointTransformData.ofScale(jointName, new Float[]{null, Float.parseFloat(rawData[i]), null}));
        }
    }

    private void process_scale_Z(String jointName, String[] rawTimes, String[] rawData) {
        for (int i = 0; i < rawTimes.length; i++) {
            keyFrames[keyTimes.indexOf(Float.parseFloat(rawTimes[i]))].
					addJointTransform(JointTransformData.ofScale(jointName, new Float[]{null, null, Float.parseFloat(rawData[i])}));
        }
    }

    private void process_rotation_X(String jointName, String[] rawTimes, String[] rawData) {
        for (int i = 0; i < rawTimes.length; i++) {
            keyFrames[keyTimes.indexOf(Float.parseFloat(rawTimes[i]))].
                    addJointTransform(JointTransformData.ofRotation(jointName, new Float[]{Float.parseFloat(rawData[i]),null,null}));
        }
    }

    private void process_rotation_Y(String jointName, String[] rawTimes, String[] rawData) {
        for (int i = 0; i < rawTimes.length; i++) {
            keyFrames[keyTimes.indexOf(Float.parseFloat(rawTimes[i]))].
                    addJointTransform(JointTransformData.ofRotation(jointName, new Float[]{null, Float.parseFloat(rawData[i]),null}));
        }
    }

    private void process_rotation_Z(String jointName, String[] rawTimes, String[] rawData) {
        for (int i = 0; i < rawTimes.length; i++) {
            keyFrames[keyTimes.indexOf(Float.parseFloat(rawTimes[i]))].
					addJointTransform(JointTransformData.ofRotation(jointName, new Float[]{null, null, Float.parseFloat(rawData[i])}));
        }
    }

    private void process_location_X(String jointName, String[] rawTimes, String[] rawData) {
        for (int i = 0; i < rawTimes.length; i++) {
            keyFrames[keyTimes.indexOf(Float.parseFloat(rawTimes[i]))].
                    addJointTransform(JointTransformData.ofLocation(jointName, new Float[]{Float.parseFloat(rawData[i]), null, null}));
        }
    }

    private void process_location_Y(String jointName, String[] rawTimes, String[] rawData) {
        for (int i = 0; i < rawTimes.length; i++) {
            keyFrames[keyTimes.indexOf(Float.parseFloat(rawTimes[i]))].
                    addJointTransform(JointTransformData.ofLocation(jointName, new Float[]{null, Float.parseFloat(rawData[i]), null}));
        }
    }

    private void process_location_Z(String jointName, String[] rawTimes, String[] rawData) {
        for (int i = 0; i < rawTimes.length; i++) {
            keyFrames[keyTimes.indexOf(Float.parseFloat(rawTimes[i]))].
                    addJointTransform(JointTransformData.ofLocation(jointName, new Float[]{null, null, Float.parseFloat(rawData[i])}));
        }
    }
}

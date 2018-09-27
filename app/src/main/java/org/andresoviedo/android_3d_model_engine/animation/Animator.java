package org.andresoviedo.android_3d_model_engine.animation;

import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.Joint;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * This class contains all the functionality to apply an animation to an
 * animated entity. An Animator instance is associated with just one
 * {@link AnimatedModel}. It also keeps track of the running time (in seconds)
 * of the current animation, along with a reference to the currently playing
 * animation for the corresponding entity.
 * 
 * An Animator instance needs to be updated every frame, in order for it to keep
 * updating the animation pose of the associated entity. The currently playing
 * animation can be changed at any time using the doAnimation() method. The
 * Animator will keep looping the current animation until a new animation is
 * chosen.
 * 
 * The Animator calculates the desired current animation pose by interpolating
 * between the previous and next keyframes of the animation (based on the
 * current animation time). The Animator then updates the transforms all of the
 * joints each frame to match the current desired animation pose.
 * 
 * @author Karl
 *
 */
public class Animator {

	private float animationTime = 0;

    private final float IDENTITY_MATRIX[] = new float[16];

    // TODO: implement slower/faster speed
    private float speed = 1f;

    private final Map<String,float[]> cache = new HashMap<>();

    public Animator() {
        Matrix.setIdentityM(IDENTITY_MATRIX,0);
	}

	/**
	 * This method should be called each frame to update the animation currently
	 * being played. This increases the animation time (and loops it back to
	 * zero if necessary), finds the pose that the entity should be in at that
	 * time of the animation, and then applies that pose to all the model's
	 * joints by setting the joint transforms.
	 */
	public void update(Object3DData obj) {
		if (!(obj instanceof AnimatedModel)) {
			return;
		}
		// if (true) return;
		AnimatedModel animatedModel = (AnimatedModel)obj;
		if (animatedModel.getAnimation() == null) return;

		// add missing key transformations
		initAnimation(animatedModel);

		// increase time to progress animation
		increaseAnimationTime((AnimatedModel)obj);

		Map<String, float[]> currentPose = calculateCurrentAnimationPose(animatedModel);

		applyPoseToJoints(currentPose, (animatedModel).getRootJoint(), IDENTITY_MATRIX, 0);
	}

	private void initAnimation(AnimatedModel animatedModel) {
		if (animatedModel.getAnimation().isInitialized()) {
			return;
		}
		KeyFrame[] keyFrames = animatedModel.getAnimation().getKeyFrames();
		Log.i("Animator", "Initializing " + animatedModel.getId() + ". " + keyFrames.length + " key frames...");
		for (int i = 0; i < keyFrames.length; i++) {
			int j = (i + 1) % keyFrames.length;
			KeyFrame keyFramePrevious = keyFrames[i];
			KeyFrame keyFrameNext = keyFrames[j];
			Map<String, JointTransform> jointTransforms = keyFramePrevious.getJointKeyFrames();
			for (Map.Entry<String, JointTransform> transform : jointTransforms.entrySet()) {
				String jointId = transform.getKey();
				if (keyFrameNext.getJointKeyFrames().containsKey(jointId)) {
					continue;
				}
				JointTransform keyFramePreviousTransform = keyFramePrevious.getJointKeyFrames().get(jointId);
				JointTransform keyFrameNextTransform;
				KeyFrame keyFrameNextNext;
				int k = (j + 1) % keyFrames.length;
				do {
					keyFrameNextNext = keyFrames[k];
					keyFrameNextTransform = keyFrameNextNext.getJointKeyFrames().get(jointId);
					k = (k + 1) % keyFrames.length;
				} while (keyFrameNextTransform == null);
				this.animationTime = keyFrameNext.getTimeStamp();
				float progression = calculateProgression(keyFramePrevious, keyFrameNextNext);
				JointTransform missingFrameTransform = JointTransform.interpolate(keyFramePreviousTransform, keyFrameNextTransform, progression);
				keyFrameNext.getJointKeyFrames().put(jointId, missingFrameTransform);
				Log.i("Animator","Added missing key transform for "+jointId);
			}
		}
		animatedModel.getAnimation().setInitialized(true);
		Log.i("Animator", "Initialized " + animatedModel.getId() + ". " + keyFrames.length + " key frames");
	}

	/**
	 * Increases the current animation time which allows the animation to
	 * progress. If the current animation has reached the end then the timer is
	 * reset, causing the animation to loop.
	 */
	private void increaseAnimationTime(AnimatedModel obj) {
		this.animationTime = SystemClock.uptimeMillis() / 1000f * speed;
		this.animationTime %= obj.getAnimation().getLength();
	}

	/**
	 * This method returns the current animation pose of the entity. It returns
	 * the desired local-space transforms for all the joints in a map, indexed
	 * by the name of the joint that they correspond to.
	 * 
	 * The pose is calculated based on the previous and next keyframes in the
	 * current animation. Each keyframe provides the desired pose at a certain
	 * time in the animation, so the animated pose for the current time can be
	 * calculated by interpolating between the previous and next keyframe.
	 * 
	 * This method first finds the preious and next keyframe, calculates how far
	 * between the two the current animation is, and then calculated the pose
	 * for the current animation time by interpolating between the transforms at
	 * those keyframes.
	 * 
	 * @return The current pose as a map of the desired local-space transforms
	 *         for all the joints. The transforms are indexed by the name ID of
	 *         the joint that they should be applied to.
	 */
	private Map<String, float[]> calculateCurrentAnimationPose(AnimatedModel obj) {
		KeyFrame[] frames = getPreviousAndNextFrames(obj);
		float progression = calculateProgression(frames[0], frames[1]);
		return interpolatePoses(frames[0], frames[1], progression);
	}

	/**
	 * This is the method where the animator calculates and sets those all-
	 * important "joint transforms" that I talked about so much in the tutorial.
	 * 
	 * This method applies the current pose to a given joint, and all of its
	 * descendants. It does this by getting the desired local-transform for the
	 * current joint, before applying it to the joint. Before applying the
	 * transformations it needs to be converted from local-space to model-space
	 * (so that they are relative to the model's origin, rather than relative to
	 * the parent joint). This can be done by multiplying the local-transform of
	 * the joint with the model-space transform of the parent joint.
	 * 
	 * The same thing is then done to all the child joints.
	 * 
	 * Finally the inverse of the joint's bind transform is multiplied with the
	 * model-space transform of the joint. This basically "subtracts" the
	 * joint's original bind (no animation applied) transform from the desired
	 * pose transform. The result of this is then the transform required to move
	 * the joint from its original model-space transform to it's desired
	 * model-space posed transform. This is the transform that needs to be
	 * loaded up to the vertex shader and used to transform the vertices into
	 * the current pose.
	 * 
	 * @param currentPose
	 *            - a map of the local-space transforms for all the joints for
	 *            the desired pose. The map is indexed by the name of the joint
	 *            which the transform corresponds to.
	 * @param joint
	 *            - the current joint which the pose should be applied to.
	 * @param parentTransform
	 *            - the desired model-space transform of the parent joint for
	 *            the pose.
	 */
	private void applyPoseToJoints(Map<String, float[]> currentPose, Joint joint, float[] parentTransform, int limit) {

	    float[] currentTransform = cache.get(joint.getName());
	    if (currentTransform == null){
	        currentTransform = new float[16];
	        cache.put(joint.getName(), currentTransform);
        }

        // TODO: implement bind pose
        if (limit <= 0){
			if (currentPose.get(joint.getName()) != null) {
				Matrix.multiplyMM(currentTransform, 0, parentTransform, 0, currentPose.get(joint.getName()), 0);
			} else {
				Matrix.multiplyMM(currentTransform, 0, parentTransform, 0, joint.getBindLocalTransform(), 0);
			}
        } else{
            Matrix.multiplyMM(currentTransform, 0, parentTransform, 0, joint.getBindLocalTransform(), 0);
        }

        // calculate animation only if its used by vertices
        //joint.calcInverseBindTransform2(parentTransform);
        if (joint.getIndex() >= 0) {
            Matrix.multiplyMM(joint.getAnimatedTransform(), 0, currentTransform, 0,
                    joint.getInverseBindTransform(), 0);
        }

		// transform children
		for (int i=0; i<joint.getChildren().size(); i++) {
            Joint childJoint = joint.getChildren().get(i);
			applyPoseToJoints(currentPose, childJoint, currentTransform, limit-1);
		}
	}

	/**
	 * Finds the previous keyframe in the animation and the next keyframe in the
	 * animation, and returns them in an array of length 2. If there is no
	 * previous frame (perhaps current animation time is 0.5 and the first
	 * keyframe is at time 1.5) then the first keyframe is used as both the
	 * previous and next keyframe. The last keyframe is used for both next and
	 * previous if there is no next keyframe.
	 * 
	 * @return The previous and next keyframes, in an array which therefore will
	 *         always have a length of 2.
	 */
	private KeyFrame[] getPreviousAndNextFrames(AnimatedModel obj) {
		KeyFrame[] allFrames = obj.getAnimation().getKeyFrames();
		KeyFrame previousFrame = allFrames[0];
		KeyFrame nextFrame = allFrames[0];
		for (int i = 1; i < allFrames.length; i++) {
			nextFrame = allFrames[i];
			if (nextFrame.getTimeStamp() > animationTime) {
				break;
			}
			previousFrame = allFrames[i];
		}
		return new KeyFrame[] { previousFrame, nextFrame };
	}

	/**
	 * Calculates how far between the previous and next keyframe the current
	 * animation time is, and returns it as a value between 0 and 1.
	 * 
	 * @param previousFrame
	 *            - the previous keyframe in the animation.
	 * @param nextFrame
	 *            - the next keyframe in the animation.
	 * @return A number between 0 and 1 indicating how far between the two
	 *         keyframes the current animation time is.
	 */
	private float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame) {
		float totalTime = nextFrame.getTimeStamp() - previousFrame.getTimeStamp();
		float currentTime = animationTime - previousFrame.getTimeStamp();
        // TODO: implement key frame display
        //return 0;
		return currentTime / totalTime;
	}

	/**
	 * Calculates all the local-space joint transforms for the desired current
	 * pose by interpolating between the transforms at the previous and next
	 * keyframes.
	 * 
	 * @param previousFrame
	 *            - the previous keyframe in the animation.
	 * @param nextFrame
	 *            - the next keyframe in the animation.
	 * @param progression
	 *            - a number between 0 and 1 indicating how far between the
	 *            previous and next keyframes the current animation time is.
	 * @return The local-space transforms for all the joints for the desired
	 *         current pose. They are returned in a map, indexed by the name of
	 *         the joint to which they should be applied.
	 */
	private Map<String, float[]> interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame, float progression) {
	    // TODO: optimize this (memory allocation)
		Map<String, float[]> currentPose = new HashMap<>();
		for (String jointName : previousFrame.getJointKeyFrames().keySet()) {
			JointTransform previousTransform = previousFrame.getJointKeyFrames().get(jointName);
			if (Math.signum(progression) == 0){
                currentPose.put(jointName, previousTransform.getLocalTransform());
            } else {
			    // memory optimization
                float[] jointPose = cache.get(jointName);
                if (jointPose == null){
                    jointPose = new float[16];
                    cache.put(jointName, jointPose);
                }
                float[] jointPoseRot = cache.get("___rotation___interpolation___");
                if (jointPoseRot == null){
                    jointPoseRot = new float[16];
                    cache.put("___rotation___interpolation___", jointPoseRot);
                }
                // calculate interpolation
                JointTransform nextTransform = nextFrame.getJointKeyFrames().get(jointName);
                JointTransform.interpolate(previousTransform, nextTransform, progression, jointPose, jointPoseRot);
                currentPose.put(jointName, jointPose);
            }
		}
		return currentPose;
	}

}


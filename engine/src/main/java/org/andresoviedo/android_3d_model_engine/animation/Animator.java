package org.andresoviedo.android_3d_model_engine.animation;

import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.util.math.Math3DUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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
 * @author Karl,andresoviedo
 *
 */
public class Animator {

	private float animationTime = 0;

	private float speed = 1f;

	private final Map<String,Object> cache = new HashMap<>();

	// cache
	private final Map<String, float[]> currentPose = new HashMap<>();;
	private KeyFrame[] previousAndNextKeyFrames = new KeyFrame[2];

	public Animator() {
	}

	/**
	 * This method should be called each frame to update the animation currently
	 * being played. This increases the animation time (and loops it back to
	 * zero if necessary), finds the pose that the entity should be in at that
	 * time of the animation, and then applies that pose to all the model's
	 * joints by setting the joint transforms.
	 */
	public void update(Object3DData obj, boolean bindPoseOnly) {
		if (!(obj instanceof AnimatedModel)) {
			return;
		}

		// if (true) return;
		AnimatedModel animatedModel = (AnimatedModel)obj;

		if (animatedModel.getAnimation() == null) return;

		// add missing key transformations
		initAnimation(animatedModel);

		// increase time to progress animation
		increaseAnimationTime((AnimatedModel) obj);

		Map<String, float[]> currentPose = calculateCurrentAnimationPose(animatedModel);

		applyPoseToJoints(animatedModel, currentPose, animatedModel.getRootJoint(), Math3DUtils.IDENTITY_MATRIX,
				Integer.MAX_VALUE);
	}

	private void initAnimation(AnimatedModel animatedModel) {
		if (animatedModel.getAnimation().isInitialized()) {
			return;
		}

		final KeyFrame[] keyFrames = animatedModel.getAnimation().getKeyFrames();
		Log.i("Animator", "Initializing " + animatedModel.getId() + ". " + keyFrames.length + " key frames...");

		// get all joint names in the different key frames
		final Set<String> allJointIds = new HashSet<>();
		for (int i = 0; i < keyFrames.length; i++) {
			allJointIds.addAll(keyFrames[i].getTransforms().keySet());
		}

		// complete keyframes with missing transforms
		final Joint rootJoint = animatedModel.getRootJoint();
		for (int i = 0; i < keyFrames.length; i++) {

			final KeyFrame keyFrameCurrent = keyFrames[i];

			final Map<String, JointTransform> jointTransforms = keyFrameCurrent.getTransforms();

			for (String jointId : allJointIds){

				// if transform is complete, do nothing
				final JointTransform currentTransform = jointTransforms.get(jointId);
				if (currentTransform != null && currentTransform.isComplete()){
					continue;
				}

				// if not complete, but first frame we just complete transforms with join data
				if (currentTransform != null && i == 0){
					currentTransform.complete(rootJoint.find(jointId));
					continue;
				}

				// if no transforms at all, but first frame we fill with empty transforms
				if (currentTransform == null && i == 0){
					jointTransforms.put(jointId, JointTransform.ofNull());
					continue;
				}

				// get previous key frame
				final KeyFrame keyFramePrevious = keyFrames[i-1];
				final JointTransform previousTransform = keyFramePrevious.getTransforms().get(jointId);

				// if on last frame, just use previous one
				if (currentTransform == null && i==keyFrames.length-1){
					jointTransforms.put(jointId, previousTransform);
					continue;
				}

				// otherwise, interpolate...
				boolean hasScaleX = currentTransform != null && currentTransform.hasScaleX();
				boolean hasScaleY = currentTransform != null && currentTransform.hasScaleY();
				boolean hasScaleZ = currentTransform != null && currentTransform.hasScaleZ();
				boolean hasRotationX = currentTransform != null && currentTransform.hasRotationX();
				boolean hasRotationY = currentTransform != null && currentTransform.hasRotationY();
				boolean hasRotationZ = currentTransform != null && currentTransform.hasRotationZ();
				boolean hasLocationX = currentTransform != null && currentTransform.hasLocationX();
				boolean hasLocationY = currentTransform != null && currentTransform.hasLocationY();
				boolean hasLocationZ = currentTransform != null && currentTransform.hasLocationZ();

				// get next available key frames
				KeyFrame keyFrameNextScaleX = null;
				KeyFrame keyFrameNextScaleY = null;
				KeyFrame keyFrameNextScaleZ = null;
				KeyFrame keyFrameNextRotationX = null;
				KeyFrame keyFrameNextRotationY = null;
				KeyFrame keyFrameNextRotationZ = null;
				KeyFrame keyFrameNextLocationX = null;
				KeyFrame keyFrameNextLocationY = null;
				KeyFrame keyFrameNextLocationZ = null;
				for (int k = i + 1; k < keyFrames.length; k++) {
					JointTransform candidate = keyFrames[k].getTransforms().get(jointId);
					if (candidate == null) continue;
					if (candidate.getScale() != null) {
						if (!hasScaleX && keyFrameNextScaleX == null && candidate.getScale()[0] != null)
							keyFrameNextScaleX = keyFrames[k];
						if (!hasScaleY && keyFrameNextScaleY == null && candidate.getScale()[1] != null)
							keyFrameNextScaleY = keyFrames[k];
						if (!hasScaleZ && keyFrameNextScaleZ == null && candidate.getScale()[2] != null)
							keyFrameNextScaleZ = keyFrames[k];
					}
					if (candidate.getRotation() != null) {
						if (!hasRotationX && keyFrameNextRotationX == null && candidate.getRotation()[0] != null)
							keyFrameNextRotationX = keyFrames[k];
						if (!hasRotationY && keyFrameNextRotationY == null && candidate.getRotation()[1] != null)
							keyFrameNextRotationY = keyFrames[k];
						if (!hasRotationZ && keyFrameNextRotationZ == null && candidate.getRotation()[2] != null)
							keyFrameNextRotationZ = keyFrames[k];
					}
					if (candidate.getLocation() != null) {
						if (!hasLocationX && keyFrameNextLocationX == null && candidate.getLocation()[0] != null)
							keyFrameNextLocationX = keyFrames[k];
						if (!hasLocationY && keyFrameNextLocationY == null && candidate.getLocation()[1] != null)
							keyFrameNextLocationY = keyFrames[k];
						if (!hasLocationZ && keyFrameNextLocationZ == null && candidate.getLocation()[2] != null)
							keyFrameNextLocationZ = keyFrames[k];
					}
					if (keyFrameNextScaleX != null && keyFrameNextScaleY != null && keyFrameNextScaleZ != null
							&& keyFrameNextRotationX != null && keyFrameNextRotationY != null && keyFrameNextRotationZ != null
							&& keyFrameNextLocationX != null && keyFrameNextLocationY != null && keyFrameNextLocationZ != null) {
						break;
					}
				}

				// if next transform is null, copy previous one
				if (keyFrameNextScaleX == null) keyFrameNextScaleX = hasScaleX ? keyFrameCurrent : keyFramePrevious;
				if (keyFrameNextScaleY == null) keyFrameNextScaleY = hasScaleY ? keyFrameCurrent : keyFramePrevious;
				if (keyFrameNextScaleZ == null) keyFrameNextScaleZ = hasScaleZ ? keyFrameCurrent : keyFramePrevious;
				if (keyFrameNextRotationX == null) keyFrameNextRotationX = hasRotationX ? keyFrameCurrent : keyFramePrevious;
				if (keyFrameNextRotationY == null) keyFrameNextRotationY = hasRotationY ? keyFrameCurrent : keyFramePrevious;
				if (keyFrameNextRotationZ == null) keyFrameNextRotationZ = hasRotationZ ? keyFrameCurrent : keyFramePrevious;
				if (keyFrameNextLocationX == null) keyFrameNextLocationX = hasLocationX ? keyFrameCurrent : keyFramePrevious;
				if (keyFrameNextLocationY == null) keyFrameNextLocationY = hasLocationY ? keyFrameCurrent : keyFramePrevious;
				if (keyFrameNextLocationZ == null) keyFrameNextLocationZ = hasLocationZ ? keyFrameCurrent : keyFramePrevious;

				// calculate progression for each individual transform
				final float elapsed = keyFrameCurrent.getTimeStamp() - keyFramePrevious.getTimeStamp();
				final float scaleProgressionX = keyFrameNextScaleX != keyFramePrevious ?
					elapsed / (keyFrameNextScaleX.getTimeStamp() - keyFramePrevious.getTimeStamp()) : 0;
				final float scaleProgressionY = keyFrameNextScaleY != keyFramePrevious ?
						elapsed / (keyFrameNextScaleY.getTimeStamp() - keyFramePrevious.getTimeStamp()) : 0;
				final float scaleProgressionZ = keyFrameNextScaleZ != keyFramePrevious ?
						elapsed / (keyFrameNextScaleZ.getTimeStamp() - keyFramePrevious.getTimeStamp()) : 0;
				final float rotationProgressionX = keyFrameNextRotationX != keyFramePrevious ?
					elapsed / (keyFrameNextRotationX.getTimeStamp() - keyFramePrevious.getTimeStamp()) : 0;
				final float rotationProgressionY = keyFrameNextRotationY != keyFramePrevious ?
						elapsed / (keyFrameNextRotationY.getTimeStamp() - keyFramePrevious.getTimeStamp()) : 0;
				final float rotationProgressionZ = keyFrameNextRotationZ != keyFramePrevious ?
						elapsed / (keyFrameNextRotationZ.getTimeStamp() - keyFramePrevious.getTimeStamp()) : 0;
				final float locationProgressionX = keyFrameNextLocationX != keyFramePrevious ?
					elapsed / (keyFrameNextLocationX.getTimeStamp() - keyFramePrevious.getTimeStamp()) : 0;
				final float locationProgressionY = keyFrameNextLocationY != keyFramePrevious ?
						elapsed / (keyFrameNextLocationY.getTimeStamp() - keyFramePrevious.getTimeStamp()) : 0;
				final float locationProgressionZ = keyFrameNextLocationZ != keyFramePrevious ?
						elapsed / (keyFrameNextLocationZ.getTimeStamp() - keyFramePrevious.getTimeStamp()) : 0;

				// interpolate
				final JointTransform missingFrameTransform = JointTransform.ofInterpolation(
						previousTransform, keyFrameNextScaleX.getTransforms().get(jointId), scaleProgressionX,
						previousTransform, keyFrameNextScaleY.getTransforms().get(jointId), scaleProgressionY,
						previousTransform, keyFrameNextScaleZ.getTransforms().get(jointId), scaleProgressionZ,
						previousTransform, keyFrameNextRotationX.getTransforms().get(jointId), rotationProgressionX,
						previousTransform, keyFrameNextRotationY.getTransforms().get(jointId), rotationProgressionY,
						previousTransform, keyFrameNextRotationZ.getTransforms().get(jointId), rotationProgressionZ,
						previousTransform, keyFrameNextLocationX.getTransforms().get(jointId), locationProgressionX,
						previousTransform, keyFrameNextLocationY.getTransforms().get(jointId), locationProgressionY,
						previousTransform, keyFrameNextLocationZ.getTransforms().get(jointId), locationProgressionZ
				);

				jointTransforms.put(jointId, missingFrameTransform);
			}

			// log event
			if (i<10) {
				Log.d("Animator", "Completed Keyframe: " + keyFrameCurrent);
			} else if (i==11){
				Log.d("Animator", "Completed Keyframe... (omitted)");
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
	 * @param animatedModel
	 *            - a map of the local-space transforms for all the joints for
	 *            the desired pose. The map is indexed by the name of the joint
	 *            which the transform corresponds to.
	 * @param joint
	 *            - the current joint which the pose should be applied to.
	 * @param parentTransform
	 *            - the desired model-space transform of the parent joint for
	 *            the pose.
	 */
	private void applyPoseToJoints(AnimatedModel animatedModel, Map<String,float[]> currentPose, Joint joint, float[]
			parentTransform, int limit) {

		float[] currentTransform = (float[])cache.get(joint.getName());
		if (currentTransform == null){
			currentTransform = new float[16];
			cache.put(joint.getName(), currentTransform);
		}

		if (currentPose.get(joint.getName()) != null){
			Matrix.multiplyMM(currentTransform, 0, parentTransform, 0, currentPose.get(joint.getName()), 0);
		}else {
			Matrix.multiplyMM(currentTransform, 0, parentTransform, 0, joint.getBindLocalTransform(), 0);
		}

		if (limit >= 0) {
			if (joint.getInverseBindTransform() == null)
				Log.e("Animator","joint with inverseBindTransform null: " +joint.getName()+", index: "+joint.getIndex());
			Matrix.multiplyMM(joint.getAnimatedTransform(), 0, currentTransform, 0, joint.getInverseBindTransform(), 0);
		} else {
			System.arraycopy(Math3DUtils.IDENTITY_MATRIX,0,joint.getAnimatedTransform(),0,16);
		}
		if (joint.getIndex() != -1) {
			// setup only if its used by vertices. if no index no place for it into animated array
			animatedModel.updateAnimatedTransform(joint);
		}

		// transform children
		for (int i=0; i<joint.getChildren().size(); i++) {
			Joint childJoint = joint.getChildren().get(i);
			applyPoseToJoints(animatedModel, currentPose, childJoint, currentTransform, limit-1);
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
		previousAndNextKeyFrames[0] =previousFrame;
		previousAndNextKeyFrames[1] = nextFrame;
		return previousAndNextKeyFrames;
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
		return currentTime / totalTime * this.speed;
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
		for (Map.Entry<String,JointTransform> entry : previousFrame.getTransforms().entrySet()) {

			final String jointName = entry.getKey();
			final JointTransform previousTransform = entry.getValue();

			// if there is no progression, we just return key transform
			if (Math.signum(progression) == 0) {
				currentPose.put(jointName, previousTransform.getMatrix());
				continue;
			}

			// TODO: initialize cache on init
			// temp cache optimization
			float[] tempMatrix1 = (float[])cache.get(jointName);
			if (tempMatrix1 == null){
				tempMatrix1 = new float[16];
				cache.put(jointName, tempMatrix1);
			}

			// next transform
			JointTransform nextTransform = nextFrame.getTransforms().get(jointName);

			// interpolate
			JointTransform.interpolate(previousTransform, nextTransform, progression, tempMatrix1);

			// update pose
			currentPose.put(jointName, tempMatrix1);
		}
		return currentPose;
	}

}


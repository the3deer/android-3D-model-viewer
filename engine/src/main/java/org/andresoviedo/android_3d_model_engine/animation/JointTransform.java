package org.andresoviedo.android_3d_model_engine.animation;

import android.opengl.Matrix;

import org.andresoviedo.android_3d_model_engine.services.collada.entities.Vector3f;
import org.andresoviedo.util.math.Quaternion;

/**
 * 
 * Represents the local bone-space transform of a joint at a certain keyframe
 * during an animation. This includes the position and rotation of the joint,
 * relative to the parent joint (for the root joint it's relative to the model's
 * origin, seeing as the root joint has no parent). The transform is stored as a
 * position vector and a quaternion (rotation) so that these values can be
 * easily interpolated, a functionality that this class also provides.
 * 
 * @author Karl
 *
 */

public class JointTransform {

	// remember, this position and rotation are relative to the parent bone!
    private final float[] matrix;
	private final Vector3f position;
	private final Quaternion rotation;

	public JointTransform(float[] matrix){
	    this.matrix = matrix;
        this.position = new Vector3f(matrix[12], matrix[13], matrix[14]);
        this.rotation = Quaternion.fromMatrix(matrix);
    }
	/**
	 * 
	 * @param position
	 *            - the position of the joint relative to the parent joint
	 *            (bone-space) at a certain keyframe. For example, if this joint
	 *            is at (5, 12, 0) in the model's coordinate system, and the
	 *            parent of this joint is at (2, 8, 0), then the position of
	 *            this joint relative to the parent is (3, 4, 0).
	 * @param rotation
	 *            - the rotation of the joint relative to the parent joint
	 *            (bone-space) at a certain keyframe.
	 */
	public JointTransform(Vector3f position, Quaternion rotation) {
	    this.matrix = null;
		this.position = position;
		this.rotation = rotation;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	/**
	 * In this method the bone-space transform matrix is constructed by
	 * translating an identity matrix using the position variable and then
	 * applying the rotation. The rotation is applied by first converting the
	 * quaternion into a rotation matrix, which is then multiplied with the
	 * transform matrix.
	 * 
	 * @return This bone-space joint transform as a matrix. The exact same
	 *         transform as represented by the position and rotation in this
	 *         instance, just in matrix form.
	 */
	public float[] getLocalTransform() {
	    if (matrix != null){
	        return matrix;
        }
		float[] matrix = new float[16];
		Matrix.setIdentityM(matrix,0);
		Matrix.translateM(matrix,0,position.x,position.y,position.z);
		Matrix.multiplyMM(matrix,0,matrix,0,rotation.toRotationMatrix(new float[16]),0);
		return matrix;
	}

	/**
	 * Interpolates between two transforms based on the progression value. The
	 * result is a new transform which is part way between the two original
	 * transforms. The translation can simply be linearly interpolated, but the
	 * rotation interpolation is slightly more complex, using a method called
	 * "SLERP" to spherically-linearly interpolate between 2 quaternions
	 * (rotations). This gives a much much better result than trying to linearly
	 * interpolate between Euler rotations.
	 * 
	 * @param frameA
	 *            - the previous transform
	 * @param frameB
	 *            - the next transform
	 * @param progression
	 *            - a number between 0 and 1 indicating how far between the two
	 *            transforms to interpolate. A progression value of 0 would
	 *            return a transform equal to "frameA", a value of 1 would
	 *            return a transform equal to "frameB". Everything else gives a
	 *            transform somewhere in-between the two.
	 * @return
	 */
	protected static JointTransform interpolate(JointTransform frameA, JointTransform frameB, float progression) {
		Vector3f pos = interpolate(frameA.position, frameB.position, progression);
		Quaternion rot = Quaternion.interpolate(frameA.rotation, frameB.rotation, progression);
		return new JointTransform(pos, rot);
	}

    protected static float[] interpolate(JointTransform frameA, JointTransform frameB, float progression, float[]
            matrix1, float[] matrix2) {
        Vector3f pos = interpolate(frameA.position, frameB.position, progression);
        Quaternion rot = Quaternion.interpolate(frameA.rotation, frameB.rotation, progression);
        Matrix.setIdentityM(matrix1,0);
        Matrix.translateM(matrix1,0,pos.x,pos.y,pos.z);
        Matrix.multiplyMM(matrix1,0,matrix1,0,rot.toRotationMatrix(matrix2),0);
        return matrix1;
    }

	/**
	 * Linearly interpolates between two translations based on a "progression"
	 * value.
	 * 
	 * @param start
	 *            - the start translation.
	 * @param end
	 *            - the end translation.
	 * @param progression
	 *            - a value between 0 and 1 indicating how far to interpolate
	 *            between the two translations.
	 * @return
	 */
	private static Vector3f interpolate(Vector3f start, Vector3f end, float progression) {
		float x = start.x + (end.x - start.x) * progression;
		float y = start.y + (end.y - start.y) * progression;
		float z = start.z + (end.z - start.z) * progression;
        // TODO: optimize this (memory allocation)
		return new Vector3f(x, y, z);
	}

}

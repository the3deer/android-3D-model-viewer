package org.andresoviedo.android_3d_model_engine.model;

import android.util.Log;

import org.andresoviedo.android_3d_model_engine.collision.Collision;
import org.andresoviedo.android_3d_model_engine.collision.CollisionDetection;
import org.andresoviedo.util.math.Math3DUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author andresoviedo
 */
public class Point {

    public static List<Ball> balls = new ArrayList<Ball>();
    static {
        balls.add(new Ball(50, 60, 30, 0));
        balls.add(new Ball(25, 30, 30, 0));
    }
    public Vec3 gravity = new Vec3(0, -0.098f, 0);

    public int idx = 0;
    public Vec3 position = new Vec3();
    public Vec3 previousPosition = new Vec3();
    public Vec3 velocity = new Vec3();

    public float restitution = 0.01f;
    public float friction = 1f;

    public boolean pinned = false;

    public Vec3 positionReset = new Vec3();
    public Vec3 previousPositionReset = new Vec3();

    public Point(float x, float y, float z) {
        this(x, y, z, x, y, z);
    }

    public Point(float x, float y, float z, float prevX, float prevY, float prevZ) {
        position.set(x, y, z);
        previousPosition.set(prevX, prevY, prevZ);

        positionReset.set(x, y, z);
        previousPositionReset.set(prevX, prevY, prevZ);
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public void reset() {
        position.set(positionReset);
        previousPosition.set(previousPositionReset);
        velocity.set(0, 0, 0);
    }

    public void update() {
        if (pinned) {
            return;
        }
        //Log.v("Point","updating point...");
        velocity.set(position);
        velocity.sub(previousPosition);

        previousPosition.set(position);

        position.add(velocity);
        position.add(gravity);
    }

    public void updateCollision() {
        if (pinned) {
            return;
        }

        velocity.set(position);
        velocity.sub(previousPosition);
        velocity.scale(friction);

        // collision with balls
        for (Ball ball : balls) {
            Vec3 normalPenetration = ball.getCollisionNormalPenetration(this);
            if (normalPenetration != null) {
                position.add(normalPenetration);
                // TODO: how to calculate friction ?
            }
        }
        
        // collision with ground
        if (position.y < 0) {
            position.y = 0;
            previousPosition.y = position.y + (int) (velocity.y * restitution);
            previousPosition.x += (position.x - previousPosition.x) * friction;
            previousPosition.z += (position.z - previousPosition.z) * friction;
        }
        
    }

    float[] prev = new float[3];
    float[] ray = new float[3];

    public void updateCollision(Object3DData obj, BoundingBox bbox) {
        if (pinned) {
            Log.v("Point","Collision! Point: "+this.idx+", Position: "+this.position+" PINNED");
            return;
        }

        //velocity.set(position);
        //velocity.sub(previousPosition);


        if (bbox.insideBounds(position.x, position.y, position.z)) {
            //Log.v("Point", "Inside bounds. point: "+position+", box: "+ bbox);
            prev[0] = previousPosition.x;
            prev[1] = previousPosition.y;
            prev[2] = previousPosition.z;
            ray[0] = velocity.x;
            ray[1] = velocity.y;
            ray[2] = velocity.z;
            if (Math3DUtils.length(ray) == 0) {
                Log.e("Point", "Ray zero bounds");
                return;
            }
            Math3DUtils.normalize(ray);
            //Collision collision = CollisionDetection.getTriangleIntersection(obj, prev, ray);
            final Collision collision = CollisionDetection.getTriangleIntersection2(obj, prev, ray);
            if(collision != null){

                Vec3 diff = new Vec3();
                diff.set(position);
                diff.sub(previousPosition);
                Log.v("Point","Collision! Point: "+this.idx+", Position: "+this.position+", distance: "+collision.getDistance()+" < "+diff.length());


                //position.set(collision.getPoint()[0], collision.getPoint()[1], collision.getPoint()[2]);
                //position.add(new Vec3(0,-velocity.length(),0));
                //position.set(collision.getTriangle().centroid[0], collision.getTriangle().centroid[1], collision.getTriangle().centroid[2]);
                //setPinned(true);
                //return;
            }


            if (collision != null && collision.getDistance() <= velocity.length()) {

                Log.v("Point","Collision at: "+collision.getDistance());
                final float[] normal = collision.getTriangle().normal;
                Vec3 colNormal = new Vec3(normal[0], normal[1], normal[2]);
                //colNormal.scale(0.5f);
                position.set(collision.getPoint()[0], collision.getPoint()[1], collision.getPoint()[2]);
                position.add(colNormal);
                setPinned(true);


                // float distance = collision.getDistance() - position.length();

                // colNormal.scale(1f);
                // position.add(colNormal)

                // new velocity
                // velocity.add(colNormal);

                // TODO: how to calculate friction ?
            }
        } else {
            Log.v("Point","Collision! Point: "+this.idx+", Position: "+this.position+", OUT OF BOUNDS");
        }

        //velocity.scale(friction);



        if (position.y < 0) {
            position.y = 0;
            previousPosition.y = position.y + (int) (velocity.y * restitution);
            previousPosition.x += (position.x - previousPosition.x) * friction;
            previousPosition.z += (position.z - previousPosition.z) * friction;
        }

    }

    public float[] getPosition() {
        return new float[]{position.x,position.y,position.z};
    }

    public int getVertexIndex() {
        return idx;
    }
}

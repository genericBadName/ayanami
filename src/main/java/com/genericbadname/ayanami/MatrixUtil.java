package com.genericbadname.ayanami;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class MatrixUtil {
    public static Matrix4d translation(Vector3d t) {
        return new Matrix4d(
                1, 0, 0, t.x,
                0, 1, 0, t.y,
                0, 0, 1, t.z,
                0, 0, 0, 1
        );
    }

    public static Matrix4d scale(Vector3d s) {
        return new Matrix4d(
                s.x, 0, 0, 0,
                0, s.y, 0, 0,
                0, 0, s.z, 0,
                0, 0, 0, 1
        );
    }

    public static Matrix4d rotation(Quaterniond r) {
        return r.get(new Matrix4d());
    }

    public static Matrix4d identity() {
        return new Matrix4d(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );
    }
}

package com.genericbadname.ayanami.client.processing.processed;

import org.joml.Matrix4d;

public record JointNode(
        Integer[] children,
        Matrix4d inverseBindMatrix,
        Matrix4d globalJointTransform
) {

}

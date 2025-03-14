package com.genericbadname.ayanami.client.renderer;

import com.genericbadname.ayanami.client.processing.processed.animation.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector4d;

public class AnimationStateManager {
    private final int totalNodes;
    private ProcessedAnimation currentAnimation;
    private boolean playingAnimation = false;
    private double currentTime = 0;

    // frame arrays follow a 2-frame pattern: current, next
    // head arrays keep track of where that channel should read from the source list. node -> head
    // output arrays are the output values for each node. node -> value

    private Vector3Frame[] translationFrames;
    private QuaternionFrame[] rotationFrames;
    private Vector3Frame[] scaleFrames;
    private Vector4Frame[] weightFrames;

    private int[] translationHeads;
    private int[] rotationHeads;
    private int[] scaleHeads;
    private int[] weightHeads;

    private Vector3d[] outputTranslations;
    private Quaterniond[] outputRotations;
    private Vector3d[] outputScales;
    private Vector4d[] outputWeights;

    public AnimationStateManager(int totalNodes) {
        this.totalNodes = totalNodes;
    }

    public void tick(float delta) {
        if (currentAnimation == null || !playingAnimation) return;

        updateTimers();
        currentTime += delta;

        if (currentTime >= currentAnimation.totalTime()) {
            reset();
        }
    }

    private void updateTimers() {
        for (Int2ObjectMap.Entry<NodeChannel> entry : currentAnimation.nodeChannels().int2ObjectEntrySet()) {
            int node = entry.getIntKey();
            NodeChannel nc = entry.getValue();
            int currentFrameIndex = (node * 2);
            int nextFrameIndex = (node * 2) + 1;

            updateVec3Channel(node, currentFrameIndex, nextFrameIndex, nc.getTranslations(), translationFrames, translationHeads, outputTranslations);
            updateQuatChannel(node, currentFrameIndex, nextFrameIndex, nc.getRotations(), rotationFrames, rotationHeads, outputRotations);
            updateVec3Channel(node, currentFrameIndex, nextFrameIndex, nc.getScales(), scaleFrames, scaleHeads, outputScales);
            updateVec4Channel(node, currentFrameIndex, nextFrameIndex, nc.getWeights(), weightFrames, weightHeads, outputWeights);
        }
    }

    private void updateVec3Channel(int node, int startFrame, int endFrame, ObjectList<Vector3Frame> frameSrc, Vector3Frame[] frameBuffer, int[] heads, Vector3d[] outputValues) {
        int head = heads[node];

        // initialize buffer
        if (head == 0 && frameSrc.size() >= 2) {
            frameBuffer[startFrame] = next(node, frameSrc, heads);
            frameBuffer[endFrame] = next(node, frameSrc, heads);
        }

        // interpolate and write to output
        Vector3Frame start = frameBuffer[startFrame];
        Vector3Frame end = frameBuffer[endFrame];

        if (start != null && end != null) outputValues[node] = interpolateVec3(start, end);

        // transfer frames
        if (hasNext(node, frameSrc, heads)) {
            frameBuffer[startFrame] = frameBuffer[endFrame];
            frameBuffer[endFrame] = next(node, frameSrc, heads);
        }
    }

    private void updateQuatChannel(int node, int startFrame, int endFrame, ObjectList<QuaternionFrame> frameSrc, QuaternionFrame[] frameBuffer, int[] heads, Quaterniond[] outputValues) {
        int head = heads[node];

        if (head == 0 && frameSrc.size() >= 2) {
            frameBuffer[startFrame] = next(node, frameSrc, heads);
            frameBuffer[endFrame] = next(node, frameSrc, heads);
        }

        QuaternionFrame start = frameBuffer[startFrame];
        QuaternionFrame end = frameBuffer[endFrame];

        if (start != null && end != null) outputValues[node] = interpolateQuat(start, end);

        if (hasNext(node, frameSrc, heads)) {
            frameBuffer[startFrame] = frameBuffer[endFrame];
            frameBuffer[endFrame] = next(node, frameSrc, heads);
        }
    }

    private void updateVec4Channel(int node, int startFrame, int endFrame, ObjectList<Vector4Frame> frameSrc, Vector4Frame[] frameBuffer, int[] heads, Vector4d[] outputValues) {
        int head = heads[node];

        if (head == 0 && frameSrc.size() >= 2) {
            frameBuffer[startFrame] = next(node, frameSrc, heads);
            frameBuffer[endFrame] = next(node, frameSrc, heads);
        }

        Vector4Frame start = frameBuffer[startFrame];
        Vector4Frame end = frameBuffer[endFrame];

        if (start != null && end != null) outputValues[node] = interpolateVec4(start, end);

        if (hasNext(node, frameSrc, heads)) {
            frameBuffer[startFrame] = frameBuffer[endFrame];
            frameBuffer[endFrame] = next(node, frameSrc, heads);
        }
    }

    private static <T> T next(int node, ObjectList<T> frameSrc, int[] heads) {
        T result = frameSrc.get(heads[node]);
        heads[node]++;

        return result;
    }

    private static <T> boolean hasNext(int node, ObjectList<T> frameSrc, int[] heads) {
        return frameSrc.size() > heads[node] + 1;
    }

    public void setupForAnimation(ProcessedAnimation currentAnimation) {
        this.currentAnimation = currentAnimation;

        translationFrames = new Vector3Frame[totalNodes * 2];
        rotationFrames = new QuaternionFrame[totalNodes * 2];
        scaleFrames = new Vector3Frame[totalNodes * 2];
        weightFrames = new Vector4Frame[totalNodes * 2];
        
        translationHeads = new int[totalNodes];
        rotationHeads = new int[totalNodes];
        scaleHeads = new int[totalNodes];
        weightHeads = new int[totalNodes];

        outputTranslations = new Vector3d[totalNodes];
        outputRotations = new Quaterniond[totalNodes];
        outputScales = new Vector3d[totalNodes];
        outputWeights = new Vector4d[totalNodes];

        this.playingAnimation = true;
    }

    public boolean isPlayingAnimation() {
        return playingAnimation;
    }

    private void reset() {
        currentTime = 0;
        currentAnimation = null;
        translationFrames = null;
        rotationFrames = null;
        scaleFrames = null;
        weightFrames = null;
    }

    private Vector3d interpolateVec3(Vector3Frame start, Vector3Frame end) {
        double tD = end.timestamp() - start.timestamp(); // duration
        double t = (currentTime - start.timestamp()) / tD; // normalized interpolation factor
        Vector3d vS = start.vector();
        Vector3d vE = end.vector();

        return switch(start.interpolation()) {
            case LINEAR -> new Vector3d(((1 - t) * vS.x) + (t * vE.x), ((1 - t) * vS.y) + (t * vE.y), ((1 - t) * vS.z) + (t * vE.z));
            case STEP -> vS;
            case CUBICSPLINE -> vS; // i am too lazy to implement this.
        };
    }

    private Quaterniond interpolateQuat(QuaternionFrame start, QuaternionFrame end) {
        double tD = end.timestamp() - start.timestamp(); // duration
        double t = (currentTime - start.timestamp()) / tD; // normalized interpolation factor
        Quaterniond qS = start.quaternion();
        Quaterniond qE = end.quaternion();

        double a = Math.acos(Math.abs(qS.dot(qE)));
        double s = qS.dot(qE) / Math.abs(qS.dot(qE));
        double c1 = Math.sin(a * (1 - t)) / Math.sin(a);
        double c2 = Math.sin(a * t) / Math.sin(a);

        return switch(start.interpolation()) {
            case LINEAR -> new Quaterniond(
                    (c1 * qS.x) + (s * c2 * qE.x),
                    (c1 * qS.y) + (s * c2 * qE.y),
                    (c1 * qS.z) + (s * c2 * qE.z),
                    (c1 * qS.w) + (s * c2 * qE.w)
            );
            case STEP -> qS;
            case CUBICSPLINE -> qS; // i am too lazy to implement this.
        };
    }

    private Vector4d interpolateVec4(Vector4Frame start, Vector4Frame end) {
        double tD = end.timestamp() - start.timestamp(); // duration
        double t = (currentTime - start.timestamp()) / tD; // normalized interpolation factor
        Vector4d vS = start.vector();
        Vector4d vE = end.vector();

        return switch(start.interpolation()) {
            case LINEAR -> new Vector4d(((1 - t) * vS.x) + (t * vE.x), ((1 - t) * vS.y) + (t * vE.y), ((1 - t) * vS.z) + (t * vE.z), ((1 - t) * vS.w) + (t * vE.w));
            case STEP -> vS;
            case CUBICSPLINE -> vS; // i am too lazy to implement this.
        };
    }

    public Vector3d getCurrentTranslation(int node) {
        return outputTranslations[node];
    }

    public Quaterniond getCurrentRotation(int node) {
        return outputRotations[node];
    }

    public Vector3d getCurrentScale(int node) {
        return outputScales[node];
    }
}

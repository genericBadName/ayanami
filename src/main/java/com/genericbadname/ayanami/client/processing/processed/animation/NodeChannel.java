package com.genericbadname.ayanami.client.processing.processed.animation;

import com.genericbadname.ayanami.client.gltf.properties.types.Interpolation;
import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayPriorityQueue;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector4d;

import java.util.Comparator;

public class NodeChannel {
    private final PriorityQueue<Vector3Frame> translations;
    private final PriorityQueue<QuaternionFrame> rotations;
    private final PriorityQueue<Vector3Frame> scales;
    private final PriorityQueue<Vector4Frame> weights;
    private double lastTime = 0;

    public NodeChannel() {
        this.translations = new ObjectArrayPriorityQueue<>(Comparator.comparing(Vector3Frame::time));
        this.rotations = new ObjectArrayPriorityQueue<>(Comparator.comparing(QuaternionFrame::time));
        this.scales = new ObjectArrayPriorityQueue<>(Comparator.comparing(Vector3Frame::time));
        this.weights = new ObjectArrayPriorityQueue<>(Comparator.comparing(Vector4Frame::time));
    }

    public void addTranslation(double[] time, Vector4d[] translation, Interpolation interpolation) {
        for (int i=0; i<time.length; i++) {
            if (time[i] > lastTime) lastTime = time[i];
            translations.enqueue(new Vector3Frame(time[i], new Vector3d(translation[i].x, translation[i].y, translation[i].z), interpolation));
        }
    }

    public void addRotation(double[] time, Vector4d[] rotation, Interpolation interpolation) {
        for (int i=0; i<time.length; i++) {
            if (time[i] > lastTime) lastTime = time[i];
            rotations.enqueue(new QuaternionFrame(time[i], new Quaterniond(rotation[i].x, rotation[i].y, rotation[i].z, rotation[i].w), interpolation));
        }
    }

    public void addScale(double[] time, Vector4d[] scale, Interpolation interpolation) {
        for (int i=0; i<time.length; i++) {
            if (time[i] > lastTime) lastTime = time[i];
            scales.enqueue(new Vector3Frame(time[i], new Vector3d(scale[i].x, scale[i].y, scale[i].z), interpolation));
        }
    }

    public void addWeight(double[] time, Vector4d[] weight, Interpolation interpolation) {
        for (int i=0; i<time.length; i++) {
            if (time[i] > lastTime) lastTime = time[i];
            weights.enqueue(new Vector4Frame(time[i], new Vector4d(weight[i].x, weight[i].y, weight[i].z, weight[i].w), interpolation));
        }
    }

    public double getLastTime() {
        return lastTime;
    }
}

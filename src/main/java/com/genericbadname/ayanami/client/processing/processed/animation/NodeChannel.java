package com.genericbadname.ayanami.client.processing.processed.animation;

import com.genericbadname.ayanami.client.gltf.properties.types.Interpolation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector4d;

import java.util.Comparator;

public class NodeChannel {
    private final ObjectList<Vector3Frame> translations;
    private final ObjectList<QuaternionFrame> rotations;
    private final ObjectList<Vector3Frame> scales;
    private final ObjectList<Vector4Frame> weights;
    private double lastTime = 0;

    public NodeChannel() {
        this.translations = new ObjectArrayList<>();
        this.rotations = new ObjectArrayList<>();
        this.scales = new ObjectArrayList<>();
        this.weights = new ObjectArrayList<>();
    }

    public void addTranslation(double[] time, Vector4d[] translation, Interpolation interpolation) {
        for (int i=0; i<time.length; i++) {
            if (time[i] > lastTime) lastTime = time[i];
            translations.add(new Vector3Frame(time[i], new Vector3d(translation[i].x, translation[i].y, translation[i].z), interpolation));
        }
    }

    public void addRotation(double[] time, Vector4d[] rotation, Interpolation interpolation) {
        for (int i=0; i<time.length; i++) {
            if (time[i] > lastTime) lastTime = time[i];
            rotations.add(new QuaternionFrame(time[i], new Quaterniond(rotation[i].x, rotation[i].y, rotation[i].z, rotation[i].w), interpolation));
        }
    }

    public void addScale(double[] time, Vector4d[] scale, Interpolation interpolation) {
        for (int i=0; i<time.length; i++) {
            if (time[i] > lastTime) lastTime = time[i];
            scales.add(new Vector3Frame(time[i], new Vector3d(scale[i].x, scale[i].y, scale[i].z), interpolation));
        }
    }

    public void addWeight(double[] time, Vector4d[] weight, Interpolation interpolation) {
        for (int i=0; i<time.length; i++) {
            if (time[i] > lastTime) lastTime = time[i];
            weights.add(new Vector4Frame(time[i], new Vector4d(weight[i].x, weight[i].y, weight[i].z, weight[i].w), interpolation));
        }
    }

    public void sortAll() {
        translations.sort(Comparator.comparing(Vector3Frame::timestamp));
        rotations.sort(Comparator.comparing(QuaternionFrame::timestamp));
        scales.sort(Comparator.comparing(Vector3Frame::timestamp));
        weights.sort(Comparator.comparing(Vector4Frame::timestamp));
    }

    public ObjectList<Vector3Frame> getTranslations() {
        return translations;
    }

    public ObjectList<QuaternionFrame> getRotations() {
        return rotations;
    }

    public ObjectList<Vector3Frame> getScales() {
        return scales;
    }

    public ObjectList<Vector4Frame> getWeights() {
        return weights;
    }

    public double getLastTime() {
        return lastTime;
    }
}

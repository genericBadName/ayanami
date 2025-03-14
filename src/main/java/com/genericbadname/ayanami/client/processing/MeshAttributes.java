package com.genericbadname.ayanami.client.processing;

import com.genericbadname.ayanami.client.processing.processed.TexCoord;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.joml.Vector4i;

import java.util.ArrayList;
import java.util.List;

// TODO: add multi-attribute support (TEXCOORD_#, etc.)
public record MeshAttributes(
        List<Vector3d> positions,
        List<Vector3d> normals,
        List<TexCoord> texcoords,
        Int2ObjectMap<Vector4i> joints,
        Int2ObjectMap<Vector4d> weights
) {
    public static MeshAttributes create() {
        return new MeshAttributes(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new Int2ObjectArrayMap<>(), new Int2ObjectArrayMap<>());
    }

    public void add(String attributeName, double[] components, int vertexIndex) {
        if (attributeName.startsWith("POSITION")) {
            positions.add(new Vector3d(components));
        } else if (attributeName.startsWith("NORMAL")) {
            normals.add(new Vector3d(components));
        } else if (attributeName.startsWith("TEXCOORD_")) {
            texcoords.add(new TexCoord(Integer.parseInt(Character.toString(attributeName.charAt(attributeName.length()-1))), new Vector2d(components)));
        } else if (attributeName.startsWith("JOINTS_")) {
            joints.put(vertexIndex, new Vector4i((int)components[0], (int)components[1], (int)components[2], (int)components[3]));
        } else if (attributeName.startsWith("WEIGHTS_")) {
            weights.put(vertexIndex, new Vector4d(components));
        }
    }
}

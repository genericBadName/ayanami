package com.genericbadname.ayanami.client.processing;

import com.genericbadname.ayanami.client.processing.processed.TexCoord;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public record MeshAttributes(
        List<Vector3d> positions,
        List<Vector3d> normals,
        List<TexCoord> texcoords
) {
    public static MeshAttributes create() {
        return new MeshAttributes(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public void add(String attributeName, double[] components) {
        if (attributeName.startsWith("POSITION")) {
            positions.add(new Vector3d(components));
        } else if (attributeName.startsWith("NORMAL")) {
            normals.add(new Vector3d(components));
        } else if (attributeName.startsWith("TEXCOORD_")) {
            texcoords.add(new TexCoord(Integer.parseInt(Character.toString(attributeName.charAt(attributeName.length()-1))), new Vector2d(components)));
        }
    }
}

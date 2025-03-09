package org.zbinfinn.wecode.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class VectorUtil {
    public static Vec3i vec3iFromVec3d(Vec3d vec3d) {
        return new Vec3i((int) vec3d.getX(), (int) vec3d.getY(), (int) vec3d.getZ());
    }
}

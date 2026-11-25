package com.example.waterbucketrelease;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/** Desenha um "esguicho" azul translúcido alongado na direção do movimento (aproxima os block_display do original). */
@SideOnly(Side.CLIENT)
public class RenderWaterShot extends Render<EntityWaterShot> {

    // {centroX, centroY, centroZ, tamX, tamY, tamZ} — Z = direção do movimento
    private static final double[][] BOXES = {
            {0.0, 0.0,  0.00, 0.34, 0.30, 0.70},
            {0.0, 0.0, -0.45, 0.26, 0.22, 0.35},
            {0.0, 0.0,  0.42, 0.22, 0.20, 0.25},
            {0.0, 0.0, -0.80, 0.16, 0.14, 0.30},
    };

    public RenderWaterShot(RenderManager rm) {
        super(rm);
    }

    @Override
    public void doRender(EntityWaterShot e, double x, double y, double z, float entityYaw, float pt) {
        float yaw = e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * pt;
        float pitch = e.prevRotationPitch + (e.rotationPitch - e.prevRotationPitch) * pt;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + e.height / 2.0F, (float) z);
        GlStateManager.rotate(yaw, 0, 1, 0);
        GlStateManager.rotate(-pitch, 1, 0, 0);

        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        Tessellator t = Tessellator.getInstance();
        BufferBuilder b = t.getBuffer();
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (double[] bx : BOXES) box(b, bx[0], bx[1], bx[2], bx[3], bx[4], bx[5]);
        t.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private static void box(BufferBuilder b, double cx, double cy, double cz, double sx, double sy, double sz) {
        double x0 = cx - sx / 2, x1 = cx + sx / 2;
        double y0 = cy - sy / 2, y1 = cy + sy / 2;
        double z0 = cz - sz / 2, z1 = cz + sz / 2;
        quad(b, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1); // baixo
        quad(b, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0); // cima
        quad(b, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0); // trás
        quad(b, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1); // frente
        quad(b, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0); // lado -X
        quad(b, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1); // lado +X
    }

    private static void quad(BufferBuilder b,
                             double ax, double ay, double az, double bx, double by, double bz,
                             double cx, double cy, double cz, double dx, double dy, double dz) {
        b.pos(ax, ay, az).color(0.25F, 0.45F, 1.0F, 0.6F).endVertex();
        b.pos(bx, by, bz).color(0.25F, 0.45F, 1.0F, 0.6F).endVertex();
        b.pos(cx, cy, cz).color(0.25F, 0.45F, 1.0F, 0.6F).endVertex();
        b.pos(dx, dy, dz).color(0.25F, 0.45F, 1.0F, 0.6F).endVertex();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityWaterShot entity) {
        return null;
    }
}

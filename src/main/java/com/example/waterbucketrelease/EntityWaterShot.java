package com.example.waterbucketrelease;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityWaterShot extends EntityThrowable {
    public EntityWaterShot(World w) { super(w); }
    public EntityWaterShot(World w, EntityLivingBase t) { super(w, t); }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            for (int i = 0; i < 3; i++) {
                // Assinatura exata da 1.12.2: Enum, x, y, z, count(int), xOff(double), yOff(double), zOff(double)
                world.spawnParticle(EnumParticleTypes.DRIP_WATER,
                    posX + (rand.nextDouble() - .5) * .4, 
                    posY + (rand.nextDouble() - .5) * .4,
                    posZ + (rand.nextDouble() - .5) * .4, 
                    0, 0.0D, 0.0D, 0.0D);
            }
            return;
        }
        Material m = world.getBlockState(getPosition()).getMaterial();
        if (m == Material.WATER || m == Material.LAVA) splash(posX, posY, posZ);
    }

    @Override
    protected void onImpact(RayTraceResult r) {
        if (world.isRemote) return;
        Vec3d at = r.hitVec;
        if (r.typeOfHit == RayTraceResult.Type.ENTITY && r.entityHit != null) {
            r.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0F);
        } else if (r.sideHit != null) {
            // CORREÇÃO 1.12.2: usa-se 'add' em vez de 'addVector'
            at = at.add(r.sideHit.getXOffset() * 0.01, r.sideHit.getYOffset() * 0.01, r.sideHit.getZOffset() * 0.01);
        }
        splash(at.x, at.y, at.z);
    }

    private void splash(double x, double y, double z) {
        for (double dy = -0.5; dy <= 0.7; dy += 0.2) {
            BlockPos bp = new BlockPos(x, y + dy, z);
            IBlockState st = world.getBlockState(bp);
            if (st.getCollisionBoundingBox(world, bp) == Block.NULL_AABB || st.getBlock().isReplaceable(world, bp)) {
                world.setBlockState(bp, Blocks.FLOWING_WATER.getDefaultState());
                break;
            }
        }
        world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.PLAYERS, 2F, 1F);
        setDead();
    }
}

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

    public EntityWaterShot(World w, EntityLivingBase thrower) { super(w, thrower); }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            // original: particle falling_water ~ ~ ~ 0.2 0.2 0.2 0 3
            for (int i = 0; i < 3; i++) {
                world.spawnParticle(EnumParticleTypes.DRIP_WATER,
                        posX + (rand.nextDouble() - 0.5) * 0.4,
                        posY + (rand.nextDouble() - 0.5) * 0.4,
                        posZ + (rand.nextDouble() - 0.5) * 0.4, 0, 0, 0);
            }
            return;
        }
        // original: se o projétil estiver dentro de água/lava, ele morre
        if (!isDead) {
            Material m = world.getBlockState(getPosition()).getMaterial();
            if (m == Material.WATER || m == Material.LAVA) splash(posX, posY, posZ);
        }
    }

    @Override
    protected void onImpact(RayTraceResult r) {
        if (world.isRemote || isDead) return;
        Vec3d at = r.hitVec;
        if (r.typeOfHit == RayTraceResult.Type.ENTITY && r.entityHit != null) {
            // como a bola de neve: dano 0, só empurra/irrita
            r.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);
        } else if (r.sideHit != null) {
            // adaptação: afasta um pouco da face atingida para cair no bloco certo
            at = at.addVector(r.sideHit.getFrontOffsetX() * 0.01,
                              r.sideHit.getFrontOffsetY() * 0.01,
                              r.sideHit.getFrontOffsetZ() * 0.01);
        }
        splash(at.x, at.y, at.z);
    }

    /** Varredura vertical idêntica ao original: y-0.5 até y+0.7 em passos de 0.2. */
    private void splash(double x, double y, double z) {
        if (world.provider.doesWaterVaporize()) { // adaptação: Nether evapora a água
            world.playSound(null, x, y, z, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0F, 1.0F);
        } else {
            for (double dy = -0.5; dy <= 0.7001; dy += 0.2) {
                BlockPos bp = new BlockPos(x, y + dy, z);
                if (canReplace(world.getBlockState(bp), bp)) {
                    world.setBlockState(bp, Blocks.FLOWING_WATER.getDefaultState()); // level 0 = bloco fonte
                    break;
                }
            }
        }
        world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.PLAYERS, 2.0F, 1.0F);
        world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.PLAYERS, 2.0F, 0.5F);
        setDead();
    }

    /** Equivalente aproximado da tag #phasethrough do datapack. */
    private boolean canReplace(IBlockState st, BlockPos pos) {
        Material m = st.getMaterial();
        if (m == Material.LAVA || m == Material.PORTAL) return false; // lava e portais ficam intactos
        Block b = st.getBlock();
        return m == Material.AIR
                || m == Material.WATER
                || b.isReplaceable(world, pos)
                || st.getCollisionBoundingBox(world, pos) == Block.NULL_AABB;
    }
}

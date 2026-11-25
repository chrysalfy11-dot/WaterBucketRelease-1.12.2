package com.example.waterbucketrelease;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReleaseHandler {

    /** Velocidade inicial do projétil (blocos/tick) — valor do datapack original (0.0302 * 100). */
    private static final float SPEED = 3.02F;

    @SubscribeEvent
    public void onToss(ItemTossEvent e) {
        EntityPlayer p = e.getPlayer();
        if (p.world.isRemote) return;

        EntityItem drop = e.getEntityItem();
        ItemStack stack = drop.getItem();
        if (stack.getItem() != Items.WATER_BUCKET || stack.getCount() != 1) return;
        if (!p.isSneaking() || p.onGround) return;

        // remove o balde dropado e deixa um balde vazio (original: mainhand vira bucket)
        drop.setDead();
        ItemStack empty = new ItemStack(Items.BUCKET);
        if (p.getHeldItemMainhand().isEmpty()) p.setHeldItem(EnumHand.MAIN_HAND, empty);
        else if (!p.inventory.addItemStackToInventory(empty)) p.dropItem(empty, false);

        // original: positioned ~ ~1.5 ~ → summon snowball ^ ^ ^3
        Vec3d look = p.getLookVec();
        EntityWaterShot shot = new EntityWaterShot(p.world, p);
        shot.setPosition(p.posX + look.x * 3.0, p.posY + 1.5 + look.y * 3.0, p.posZ + look.z * 3.0);
        shot.shoot(look.x, look.y, look.z, SPEED, 0.0F); // versão de 5 args: não soma a velocidade do jogador
        p.world.spawnEntity(shot);

        p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 2.0F, 2.0F);
        p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 2.0F, 1.0F);
    }
}

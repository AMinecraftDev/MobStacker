package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class EntityExplodeListener implements Listener {

    @EventHandler
    public void entityExplodeListener(EntityExplodeEvent event) {

        /*
        If there is a LivingEntity exploding and we have kill full stack set to false, then execute this block.
         */
        if (event.getEntity() instanceof LivingEntity && !MobStacker.plugin.getConfig().getBoolean("exploding-creeper-kills-stack")) {

            LivingEntity entity = ((LivingEntity) event.getEntity());

            /*
            Get the quantity of mobs in the stack.
             */
            if (entity.hasMetadata("quantity")) {

                /*
                Removes one for the chap that just blew up.
                 */
                int newQuantity = StackUtils.getStackSize(entity) - 1;

                /*
                If a stack still remains then follow.
                 */
                if (newQuantity > 0) {

                    /*
                    Spawn in a new entity to replace the old stack.
                     */
                    LivingEntity newEntity = (LivingEntity) entity.getLocation().getWorld().spawnEntity(entity.getLocation(), entity.getType());

                    /*
                    Set the stacks new size.
                     */
                    StackUtils.setStackSize(newEntity, newQuantity);

                    /*
                    If a stack is larger than one, then give it the appropriate name.
                     */
                    if (newQuantity > 1) {

                        StackUtils.renameStack(newEntity, newQuantity);

                    }

                }

            }

            /*
            If config is set to kill the full stack AND amplify explosions, then follow.
             */
        } else if (event.getEntity() instanceof LivingEntity && MobStacker.plugin.getConfig().getBoolean("exploding-creeper-kills-stack") &&
                MobStacker.plugin.getConfig().getBoolean("magnify-stack-explosion.enable")) {

            /*
            Get the bastard exploding.
             */
            LivingEntity entity = ((LivingEntity) event.getEntity());

            /*
            And how many of them there is
             */
            int quantity = StackUtils.getStackSize(entity);

            /*
            Set there to be only one mob in the stack so when it blows up, it dies, giving the illusion that all have died.
             */
            StackUtils.setStackSize(entity, 1);

            /*
            If the number of creepers in the stack is greater than the max explosion size, set the explosion size to the max.
             */
            if (quantity > MobStacker.plugin.getConfig().getInt("magnify-stack-explosion.max-creeper-explosion-size")) {
                quantity = MobStacker.plugin.getConfig().getInt("magnify-stack-explosion.max-creeper-explosion-size");
            }

            /*
            Create this bad ass explosion where the creeper was.
             */
            event.getLocation().getWorld().createExplosion(event.getLocation(), quantity + 1);

        }

    }

}

package io.github.rfmineguy.modulartools.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;

import static net.minecraft.server.command.CommandManager.literal;

public class RepairCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        try {
            dispatcher.register(literal("repair")
                    .executes(context -> {
                        try {
                            if (!(context.getSource().getEntity() instanceof PlayerEntity player)) return 0;
                            if (!player.getMainHandStack().contains(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT)) return 0;

                            ModularToolComponentRecord componentRecord = player.getMainHandStack().get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
                            assert componentRecord != null;
                            componentRecord = componentRecord.setBroken(false);
                            player.getMainHandStack().setDamage(0);
                            player.getMainHandStack().set(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT, componentRecord);
                            return 1;
                        } catch (Exception e) {
                            context.getSource().sendMessage(Text.literal("Error occured"));
                            e.printStackTrace();
                        }
                        return 1;
                    })
            );
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}

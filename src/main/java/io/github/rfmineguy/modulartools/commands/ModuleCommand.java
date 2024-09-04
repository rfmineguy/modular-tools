package io.github.rfmineguy.modulartools.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.ModularToolsMod;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import io.github.rfmineguy.modulartools.items.modulartools.ModularPickaxeItem;
import io.github.rfmineguy.modulartools.items.modulartools.ModularTool;
import io.github.rfmineguy.modulartools.modules.Module;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ModuleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        try {
            dispatcher.register(literal("module")
                    .then(literal("add")
                            .then(argument("module_name", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, ModRegistration.ModRegistries.MODULE_REG_KEY))
                                    .suggests(ModRegistration.ModCommands.MODULE_SUGGESTION_PROVIDER)
                                    .executes(context -> { // module add <module_name>
                                        try {
                                            if (!(context.getSource().getEntity() instanceof PlayerEntity player)) return 0;
                                            if (!player.getMainHandStack().contains(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT))
                                                return 0;
                                            RegistryEntry.Reference<Module> moduleEntry = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "module_name", ModRegistration.ModRegistries.MODULE_REG_KEY);
                                            System.out.println(moduleEntry);
                                            Module module = ModRegistration.ModRegistries.MODULE_REGISTRY.get(moduleEntry.registryKey());
                                            assert module != null;

                                            System.out.println(module);
                                            ModularTool.addModule(player.getMainHandStack(), module);
                                        } catch (Exception e) {
                                            context.getSource().sendMessage(Text.literal("Error occured"));
                                            e.printStackTrace();
                                        }
                                        return 1;
                                    })
                            )
                    )
                    .then(literal("remove")
                            .then(argument("module_name", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, ModRegistration.ModRegistries.MODULE_REG_KEY))
                                    .suggests(ModRegistration.ModCommands.MODULE_SUGGESTION_PROVIDER)
                                    .executes(context -> {
                                        if (!(context.getSource().getEntity() instanceof PlayerEntity player)) return 0;
                                        if (!player.getMainHandStack().contains(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT))
                                            return 0;

                                        RegistryEntry.Reference<Module> moduleEntry = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "module_name", ModRegistration.ModRegistries.MODULE_REG_KEY);
                                        Module module = ModRegistration.ModRegistries.MODULE_REGISTRY.get(moduleEntry.registryKey());
                                        assert module != null;

                                        ModularTool.removeModule(player.getMainHandStack(), module);
                                        return 1;
                                    })
                            )
                    )
                    .then(literal("list")
                            .executes(context -> {
                                if (!(context.getSource().getEntity() instanceof PlayerEntity player)) return 0;
                                if (!player.getMainHandStack().contains(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT))
                                    return 0;

                                ModularToolComponentRecord componentRecord = player.getMainHandStack().get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
                                assert componentRecord != null;
                                player.sendMessage(Text.literal("" + componentRecord.modules()));
                                return 1;
                            }))
            );
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}

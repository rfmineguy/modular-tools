package io.github.rfmineguy.modulartools.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.items.ModularPickaxeItem;
import io.github.rfmineguy.modulartools.modules.Module;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ModuleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(literal("module")
                .then(literal("add")
                        .then(argument("module_name", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, ModRegistration.ModRegistries.MODULE_REG_KEY))
                                .suggests(ModRegistration.ModCommands.MODULE_SUGGESTION_PROVIDER)
                                .executes(context -> { // module add <module_name>
                                    if (!(context.getSource().getEntity() instanceof PlayerEntity player)) return 0;
                                    if (!player.getMainHandStack().contains(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT)) return 0;

                                    RegistryEntry.Reference<Module> moduleEntry = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "module_name", ModRegistration.ModRegistries.MODULE_REG_KEY);
                                    Module module = ModRegistration.ModRegistries.MODULE_REGISTRY.get(moduleEntry.registryKey());
                                    assert module != null;

                                    ModularPickaxeItem.addModule(player.getMainHandStack(), module);
                                    return 1;
                                })
                        )
                )
                .then(literal("remove")
                        .then(argument("module_name", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, ModRegistration.ModRegistries.MODULE_REG_KEY))
                                .suggests(ModRegistration.ModCommands.MODULE_SUGGESTION_PROVIDER)
                                .executes(context -> {
                                    if (!(context.getSource().getEntity() instanceof PlayerEntity player)) return 0;
                                    if (!player.getMainHandStack().contains(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT)) return 0;

                                    RegistryEntry.Reference<Module> moduleEntry = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "module_name", ModRegistration.ModRegistries.MODULE_REG_KEY);
                                    Module module = ModRegistration.ModRegistries.MODULE_REGISTRY.get(moduleEntry.registryKey());
                                    assert module != null;

                                    ModularPickaxeItem.removeModule(player.getMainHandStack(), module);
                                    return 1;
                                })
                        )
                )
        );
    }
}

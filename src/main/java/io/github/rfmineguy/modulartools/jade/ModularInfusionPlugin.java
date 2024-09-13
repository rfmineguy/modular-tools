package io.github.rfmineguy.modulartools.jade;

import io.github.rfmineguy.modulartools.blocks.infusion2.modular_infusion_drone2.ModularInfusionDrone2Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class ModularInfusionPlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        IWailaPlugin.super.register(registration);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        IWailaPlugin.super.registerClient(registration);
        registration.registerBlockComponent(ModularInfusionDroneComponentProvider.INSTANCE, ModularInfusionDrone2Block.class);
    }
}

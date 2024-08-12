package io.github.rfmineguy.modulartools.client;

import io.github.rfmineguy.modulartools.ExtendedKeyboard;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Mutable
    @Shadow @Final public Keyboard keyboard;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Keyboard;setup(J)V"))
    public void constructor(RunArgs args, CallbackInfo callbackInfo) {
        this.keyboard = new ExtendedKeyboard((MinecraftClient) (Object) this);
        if (this.keyboard instanceof ExtendedKeyboard) {
            System.out.println("keyboard is extended");
        }
    }
}

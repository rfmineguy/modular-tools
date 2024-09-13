package io.github.rfmineguy.modulartools.blocks.infusion3.drone;

import com.leakyabstractions.result.api.Result;
import com.leakyabstractions.result.core.Results;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.blocks.infusion3.controller.InfusionControllerBlockEntity;
import io.github.rfmineguy.modulartools.components.ActivatorComponentRecord;
import io.github.rfmineguy.modulartools.components.LevelBlockComponentRecord;
import io.github.rfmineguy.modulartools.util.VecUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InfusionDroneBlockEntity extends BlockEntity implements BlockEntityTicker<InfusionDroneBlockEntity> {

    public class ToolStackRotationData {
        public float nextToolStackTurningSpeed;
        public float toolStackTurningSpeed;
        public float toolStackRotation;
        public float lastToolStackRotation;
        public float targetToolStackRotation;
    }
    float angle = 0, angleOffset = 0;
    double subX, subY, subZ;
    Vec3d subPos;
    InfusionControllerBlockEntity controllerBlockEntity = null;

    ItemStack activeModuleStack = ItemStack.EMPTY;
    ItemStack activeLevelStack = ItemStack.EMPTY;
    ItemStack activatorStack = ItemStack.EMPTY;

    private final ToolStackRotationData toolStackRotationData;

    public InfusionDroneBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistration.ModBlockEntities.INFUSION_DRONE_BE, pos, state);
        subPos = new Vec3d(0, 0, 0);
        toolStackRotationData = new ToolStackRotationData();
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, InfusionDroneBlockEntity blockEntity) {
        if (controllerBlockEntity == null) return;
        angle += 0.1f;
        if (angle > Math.PI * 2) angle = 0;
        subY = 0.1 + MathHelper.sin(controllerBlockEntity.getAngle() + this.angleOffset) * 0.05;

        blockEntity.updateRotationData(world, pos, state);
    }
    private void updateRotationData(World world, BlockPos pos, BlockState state) {
        toolStackRotationData.toolStackTurningSpeed = toolStackRotationData.nextToolStackTurningSpeed;
        toolStackRotationData.lastToolStackRotation = toolStackRotationData.toolStackRotation;
        PlayerEntity player = world.getClosestPlayer((double) pos.getX() + 0.5, (double)pos.getY() + 0.5, (double) pos.getZ() + 0.5, 4, false);
        if (player != null) {
            double dirx = player.getX() - ((double) pos.getX() + 0.5);
            double dirz = player.getZ() - ((double) pos.getZ() + 0.5);
            double angle = MathHelper.atan2(dirz, dirx);
            toolStackRotationData.targetToolStackRotation = (float) angle;
            toolStackRotationData.nextToolStackTurningSpeed += 0.1F;
            toolStackRotationData.toolStackRotation = MathHelper.lerp(0.2f, toolStackRotationData.toolStackRotation, toolStackRotationData.targetToolStackRotation);
        }
        else {
            toolStackRotationData.targetToolStackRotation += 0.05f;
            if (toolStackRotationData.targetToolStackRotation > Math.PI * 2)
                toolStackRotationData.targetToolStackRotation = 0;
            else
                toolStackRotationData.toolStackRotation = MathHelper.lerp(0.2f, toolStackRotationData.toolStackRotation, toolStackRotationData.targetToolStackRotation);
        }
    }

    public Result<ItemStack, String> tryRemoveItem() {
        if (getActivatorStack() == ItemStack.EMPTY && getActiveLevelStack() == ItemStack.EMPTY) return Results.failure("No item to remove");
        // first try removing the activator stack
        if (getActivatorStack() != ItemStack.EMPTY) {
            ItemStack itemStack = activatorStack.copy();
            activatorStack = ItemStack.EMPTY;
            return Results.success(itemStack);
        }
        // then try removing the level stack
        if (getActiveLevelStack() != ItemStack.EMPTY) {
            ItemStack itemStack = activeLevelStack.copy();
            activeLevelStack = ItemStack.EMPTY;
            return Results.success(itemStack);
        }
        return null;
    }
    public Result<ItemStack, String> tryPutActivator(ItemStack stack) {
        ItemStack d = activatorStack.copy();
        if (getActiveLevelStack() == ItemStack.EMPTY) return Results.failure("No level block installed");
        if (stack == ItemStack.EMPTY) return Results.success(d);
        LevelBlockComponentRecord levelComponent = getActiveLevelStack().get(ModRegistration.ModComponents.LEVEL_BLOCK_COMPONENT);
        ActivatorComponentRecord activatorComponent = stack.get(ModRegistration.ModComponents.ACTIVATOR_COMPONENT);
        assert activatorComponent != null;
        assert levelComponent != null;
        if (levelComponent.level() != activatorComponent.level()) return Results.failure("Level block and activator levels don't match");
        activatorStack = stack;
        return Results.success(d);
    }
    public Result<ItemStack, String> tryPutModule(ItemStack stack) {
        ItemStack d = activeModuleStack.copy();
        activeModuleStack = stack;
        return Results.success(d);
    }
    public Result<ItemStack, String> tryPutLevelBlock(ItemStack stack) {
        ItemStack d = activeLevelStack.copy();
        activeLevelStack = stack;
        return Results.success(d);
    }

    boolean isCardinalToController(BlockPos controllerPos) {
        return this.pos.getManhattanDistance(controllerPos) <= 1;
    }
    public void animateTowardController(float t, BlockPos controllerPos) {
        Vec3d dir = this.pos.toCenterPos().subtract(controllerPos.toCenterPos()).normalize();
        Vec3d target;
        double distance = 1;
        if (!isCardinalToController(controllerPos)) distance = 0.7;
        else distance = 0.9;
        target = controllerPos.toCenterPos().add(dir.multiply(distance));

        subX = VecUtil.Lerp1d(this.pos.toCenterPos().x, target.x, t) - this.pos.toCenterPos().x;
        subZ = VecUtil.Lerp1d(this.pos.toCenterPos().z, target.z, t) - this.pos.toCenterPos().z;
    }

    public void setController(InfusionControllerBlockEntity controllerBlockEntity) {
        this.controllerBlockEntity = controllerBlockEntity;
    }
    public void clearController() {
        this.controllerBlockEntity = null;
    }
    public boolean hasController() {
        return this.controllerBlockEntity != null;
    }

    public Vec3d getSubPosition() {
        if (controllerBlockEntity == null) return Vec3d.ZERO;
        return new Vec3d(subX, subY, subZ);
    }
    public void setAngleOffset(float angleOffset) {
        this.angleOffset = angleOffset;
    }

    public ItemStack getActiveLevelStack() {
        return activeLevelStack;
    }
    public ItemStack getActiveModuleStack() {
        return activeModuleStack;
    }
    public ItemStack getActivatorStack() {
        return activatorStack;
    }

    public float getModuleRotation() {
        return toolStackRotationData.toolStackRotation;
    }
}

package io.github.rfmineguy.modulartools.blocks.modular_infusion_drone2;

import com.leakyabstractions.result.api.Result;
import com.leakyabstractions.result.core.Results;
import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.ModularLevel;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller_2.ModularInfusionController2BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ModularInfusionDrone2BlockEntity extends BlockEntity implements BlockEntityTicker<ModularInfusionDrone2BlockEntity> {
    @Override
    public void tick(World world, BlockPos pos, BlockState state, ModularInfusionDrone2BlockEntity blockEntity) {
        blockEntity.age += 0.01f;
    }

    public static class InsertLevelItemError {
        public static InsertLevelItemError NOT_BLOCK(ItemStack stack) {
            return new InsertLevelItemError("Not a block");
        }
        public static InsertLevelItemError NOT_LEVEL_BLOCK(ItemStack stack) {
            return new InsertLevelItemError("Not level block");
        }
        public static InsertLevelItemError LEVEL_BLOCK_TOO_LOW_OF_LEVEL(ItemStack stack) {
            return new InsertLevelItemError("Level block is too low of level");
        }
        public static InsertLevelItemError NOT_PART_OF_MULTIBLOCK() {
            return new InsertLevelItemError("Drone isn't part of a multiblock");
        }
        public static InsertLevelItemError CONTROLLER_LEVEL_TOO_LOW() {
            return new InsertLevelItemError("Controller level too low");
        }
        public static InsertLevelItemError MULTIBLOCK_INCOMPLETE() {
            return new InsertLevelItemError("Multiblock incomplete");
        }

        private final String formatted;
        public InsertLevelItemError(String formatted) {
            this.formatted = formatted;
        }

        @Override
        public String toString() {
            return formatted;
        }
    }
    public static class InsertModuleItemError {
        public static InsertModuleItemError NOT_MODULE_ITEM(ItemStack stack) {
            return new InsertModuleItemError("Not module item");
        }
        public static InsertModuleItemError PEDESTAL_LEVEL_ZERO() {
            return new InsertModuleItemError("Pedestal must be level 1 or higher");
        }
        public static InsertModuleItemError MODULE_LEVEL_TOO_HIGH(ItemStack stack) {
            return new InsertModuleItemError("Module level too high");
        }
        public static InsertModuleItemError NOT_PART_OF_MULTIBLOCK() {
            return new InsertModuleItemError("Drone isn't part of a multiblock");
        }
        private final String formatted;
        public InsertModuleItemError(String formatted) {
            this.formatted = formatted;
        }

        @Override
        public String toString() {
            return formatted;
        }

    }
    public static class ExtractLevelItemError {
        public static ExtractLevelItemError MODULE_PRESENT(ItemStack stack) {
            return new ExtractLevelItemError("Module item still present");
        }
        public static ExtractLevelItemError MUST_EXTRACT_IN_ORDER() {
            return new ExtractLevelItemError("Must extract level items in order");
        }
        public static ExtractLevelItemError NOT_PART_OF_MULTIBLOCK() {
            return new ExtractLevelItemError("Drone isn't part of a multiblock");
        }

        private final String formatted;
        public ExtractLevelItemError(String formatted) {
            this.formatted = formatted;
        }

        @Override
        public String toString() {
            return formatted;
        }
    }

    private ItemStack moduleItem;
    private ModularLevel modularLevel;
    private float age;
    private final double angleOffset;

    @Nullable
    private ModularInfusionController2BlockEntity connectedController;

    public ModularInfusionDrone2BlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistration.ModBlockEntities.MODULAR_INFUSION_DRONE2_BLOCK_ENTITY, pos, state);
        this.moduleItem = ItemStack.EMPTY;
        this.modularLevel = ModularLevel.ZERO;
        this.age = 0;
        this.angleOffset = new Random().nextDouble(0, Math.PI * 2);
    }

    public Result<ItemStack, InsertLevelItemError> tryInsertModularLevelBlock(ItemStack stack) {
        // ======================= OnFailure ====================== //
        if (connectedController == null) return Results.failure(InsertLevelItemError.NOT_PART_OF_MULTIBLOCK());
        // if (!connectedController.isMultiblockComplete()) return Results.failure(InsertLevelItemError.MULTIBLOCK_INCOMPLETE());
        if (stack == ItemStack.EMPTY) return Results.failure(InsertLevelItemError.NOT_BLOCK(stack));
        if (!(stack.getItem() instanceof BlockItem)) return Results.failure(InsertLevelItemError.NOT_BLOCK(stack));
        if (!stack.contains(ModRegistration.ModComponents.LEVEL_BLOCK_COMPONENT)) return Results.failure(InsertLevelItemError.NOT_LEVEL_BLOCK(stack));
        {
            ModularLevel insertLevel = stack.get(ModRegistration.ModComponents.LEVEL_BLOCK_COMPONENT).getLevel();
            ModularLevel activeStackLevel = getModuleStack() != ItemStack.EMPTY ? getModuleStack().get(ModRegistration.ModComponents.MODULE_COMPONENT).getLevel() : ModularLevel.ZERO;
            if (insertLevel.ordinal() < activeStackLevel.ordinal()) return Results.failure(InsertLevelItemError.LEVEL_BLOCK_TOO_LOW_OF_LEVEL(stack));
        }

        // ======================= OnSuccess ====================== //
        {
            // Check if the controller level is high enough (aka check if there are enough level blocks in the drones)
            ModularLevel insertLevel = stack.get(ModRegistration.ModComponents.LEVEL_BLOCK_COMPONENT).getLevel();
            ModularLevel controllerLevel = connectedController.getMaxLevelDrone();
            if (insertLevel.level - 1 != controllerLevel.level) return Results.failure(InsertLevelItemError.CONTROLLER_LEVEL_TOO_LOW());
        }
        ItemStack old = getModuleStack().copy();
        setModularLevel(stack.get(ModRegistration.ModComponents.LEVEL_BLOCK_COMPONENT).getLevel());
        return Results.success(old);
    }
    public Result<ItemStack, InsertModuleItemError> tryInsertModuleItem(ItemStack stack) {
        // ======================= OnFailure ====================== //
        if (connectedController == null) return Results.failure(InsertModuleItemError.NOT_PART_OF_MULTIBLOCK());
        if (getModularLevel() == ModularLevel.ZERO) return Results.failure(InsertModuleItemError.PEDESTAL_LEVEL_ZERO());
        if (!stack.contains(ModRegistration.ModComponents.MODULE_COMPONENT)) return Results.failure(InsertModuleItemError.NOT_MODULE_ITEM(stack));
        ModularLevel stackLevel = stack.get(ModRegistration.ModComponents.MODULE_COMPONENT).getLevel();
        if (stackLevel.ordinal() > getModularLevel().ordinal()) return Results.failure(InsertModuleItemError.MODULE_LEVEL_TOO_HIGH(stack));

        // ======================= OnSuccess ====================== //
        ItemStack old = getModuleStack().copy();
        setModuleStack(stack);
        return Results.success(old);
    }
    public Result<ItemStack, ExtractLevelItemError> tryExtractModularLevelItem() {
        if (getConnectedController() == null) return Results.failure(ExtractLevelItemError.NOT_PART_OF_MULTIBLOCK());
        if (getModuleStack() != ItemStack.EMPTY) return Results.failure(ExtractLevelItemError.MODULE_PRESENT(getModuleStack()));
        if (getModularLevel().level < getConnectedController().getModularLevel().level) return Results.failure(ExtractLevelItemError.MUST_EXTRACT_IN_ORDER());
        ModularLevel level1 = getModularLevel();
        setModularLevel(ModularLevel.ZERO);
        return Results.success(level1.getLevelStack());
    }

    public void setConnectedController(ModularInfusionController2BlockEntity connectedController) {
        this.connectedController = connectedController;
    }
    public @Nullable ModularInfusionController2BlockEntity getConnectedController() {
        return connectedController;
    }
    public boolean hasConnectedController() {
        return connectedController != null;
    }
    public void clearConnectedController() {
        this.connectedController = null;
    }

    public ItemStack extractLevelBlock() {
        ItemStack old = getModularLevel().getLevelStack().copy();
        setModularLevel(ModularLevel.ZERO);
        return old;
    }
    public ItemStack extractModule() {
        ItemStack old = getModuleStack().copy();
        setModuleStack(ItemStack.EMPTY);
        return old;
    }

    @NotNull
    public ModularLevel getModularLevel() {
        return modularLevel;
    }
    public void setModularLevel(ModularLevel level) {
        this.modularLevel = level;
    }
    public boolean hasLevelStack() {
        return this.modularLevel != ModularLevel.NONE && this.modularLevel != ModularLevel.ZERO && this.modularLevel != ModularLevel.ERROR;
    }

    public void setModuleStack(ItemStack stack) {
        this.moduleItem = stack;
    }
    public ItemStack getModuleStack() {
        return moduleItem;
    }
    public boolean hasModuleStack() {
        return this.moduleItem != ItemStack.EMPTY;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public float getAge() {
        return age;
    }
}

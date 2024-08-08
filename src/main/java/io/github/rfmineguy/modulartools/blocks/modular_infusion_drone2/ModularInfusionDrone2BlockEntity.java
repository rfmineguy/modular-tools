package io.github.rfmineguy.modulartools.blocks.modular_infusion_drone2;

import com.leakyabstractions.result.api.Result;
import com.leakyabstractions.result.core.Results;
import io.github.rfmineguy.modulartools.ModularLevel;
import io.github.rfmineguy.modulartools.Registration;
import io.github.rfmineguy.modulartools.blocks.modular_infusion_controller.ModularInfusionControllerBlockEntity;
import io.github.rfmineguy.modulartools.components.LevelBlockComponentRecord;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        private final String formatted;
        public ExtractLevelItemError(String formatted) {
            this.formatted = formatted;
        }

        @Override
        public String toString() {
            return formatted;
        }
    }
    private ItemStack activeStack, levelItem;
    private float age;
    private double angleOffset;
    private ModularInfusionControllerBlockEntity connectedController;

    public ModularInfusionDrone2BlockEntity(BlockPos pos, BlockState state) {
        super(Registration.MODULAR_INFUSION_DRONE2_BLOCK_ENTITY, pos, state);
        this.activeStack = ItemStack.EMPTY;
        this.levelItem = ItemStack.EMPTY;
        this.age = 0;
        this.angleOffset = new Random().nextDouble(0, Math.PI * 2);
    }

    public Result<ItemStack, InsertLevelItemError> tryInsertLevelBlock(ItemStack stack) {
        if (stack == ItemStack.EMPTY) return Results.failure(InsertLevelItemError.NOT_BLOCK(stack));
        if (!(stack.getItem() instanceof BlockItem)) return Results.failure(InsertLevelItemError.NOT_BLOCK(stack));
        if (!stack.contains(Registration.LEVEL_BLOCK_COMPONENT)) return Results.failure(InsertLevelItemError.NOT_LEVEL_BLOCK(stack));
        ModularLevel insertLevel = stack.get(Registration.LEVEL_BLOCK_COMPONENT).getLevel();
        ModularLevel activeStackLevel = getActiveStack() != ItemStack.EMPTY ?
                getActiveStack().get(Registration.MODULE_COMPONENT).getLevel() :
                ModularLevel.ZERO;
        if (insertLevel.ordinal() < activeStackLevel.ordinal()) return Results.failure(InsertLevelItemError.LEVEL_BLOCK_TOO_LOW_OF_LEVEL(stack));
        ItemStack old = getLevelStack().copy();
        setLevelStack(stack);
        return Results.success(old);
    }

    public Result<ItemStack, InsertModuleItemError> tryInsertModuleItem(ItemStack stack) {
        if (getModularLevel() == ModularLevel.ZERO) return Results.failure(InsertModuleItemError.PEDESTAL_LEVEL_ZERO());
        if (!stack.contains(Registration.MODULE_COMPONENT)) return Results.failure(InsertModuleItemError.NOT_MODULE_ITEM(stack));
        ModularLevel stackLevel = stack.get(Registration.MODULE_COMPONENT).getLevel();
        if (stackLevel.ordinal() > getModularLevel().ordinal()) return Results.failure(InsertModuleItemError.MODULE_LEVEL_TOO_HIGH(stack));
        ItemStack old = getActiveStack().copy();
        setActiveStack(stack);
        return Results.success(old);
    }

    public Result<ItemStack, ExtractLevelItemError> tryExtractLevelItem() {
        if (getActiveStack() != ItemStack.EMPTY) return Results.failure(ExtractLevelItemError.MODULE_PRESENT(getActiveStack()));

        ItemStack old = getLevelStack().copy();
        setLevelStack(ItemStack.EMPTY);
        return Results.success(old);
    }

    public ItemStack extractLevelBlock() {
        ItemStack old = getLevelStack().copy();
        setLevelStack(ItemStack.EMPTY);
        return old;
    }

    public ItemStack extractModule() {
        ItemStack old = getActiveStack().copy();
        setActiveStack(ItemStack.EMPTY);
        return old;
    }

    public ModularLevel getModularLevel() {
        if (levelItem.isEmpty()) return ModularLevel.ZERO;
        if (!levelItem.contains(Registration.LEVEL_BLOCK_COMPONENT)) return ModularLevel.ZERO;
        LevelBlockComponentRecord componentRecord = levelItem.get(Registration.LEVEL_BLOCK_COMPONENT);
        if (componentRecord == null) return ModularLevel.ZERO;
        return componentRecord.getLevel();
    }

    public void setActiveStack(ItemStack stack) {
        this.activeStack = stack;
    }

    public void setLevelStack(ItemStack stack) {
        this.levelItem = stack;
    }

    public ItemStack getActiveStack() {
        return activeStack;
    }

    public ItemStack getLevelStack() {
        return levelItem;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public float getAge() {
        return age;
    }
}

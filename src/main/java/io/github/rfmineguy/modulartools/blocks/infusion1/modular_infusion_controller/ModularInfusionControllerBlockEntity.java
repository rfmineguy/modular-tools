package io.github.rfmineguy.modulartools.blocks.infusion1.modular_infusion_controller;

import com.leakyabstractions.result.core.Results;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import io.github.rfmineguy.modulartools.ModularLevel;
import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.blocks.PedestalBlockEntity;
import io.github.rfmineguy.modulartools.blocks.infusion1.modular_infusion_drone.ModularInfusionDroneBlockEntity;
import io.github.rfmineguy.modulartools.components.ModularToolComponentRecord;
import io.github.rfmineguy.modulartools.items.modulartools.ModularPickaxeItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.leakyabstractions.result.api.Result;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ModularInfusionControllerBlockEntity extends PedestalBlockEntity {
    public static class InsertToolError {
        public static InsertToolError NOT_MODULAR_TOOL(ItemStack stack) {
            return new InsertToolError("Not modular tool");
        }

        public static InsertToolError TOO_MANY_MODULES(ItemStack stack) {
            return new InsertToolError("Too many modules: " + stack.getCount());
        }

        private String formatted;

        public InsertToolError(String formatted) {
            this.formatted = formatted;
        }

        @Override
        public String toString() {
            return formatted;
        }
    }
    public static class InfuseToolError {
        public static InfuseToolError DUPLICATE_MODULES(ItemStack stack) {
            return new InfuseToolError("Duplicate module: " + stack);
        }

        public static InfuseToolError NO_TOOL() {
            return new InfuseToolError("No tool to infuse");
        }

        private String formatted;
        public InfuseToolError(String formatted) {
            this.formatted = formatted;
        }

        @Override
        public String toString() {
            return this.formatted;
        }
    }
    private final HashSet<BlockPos> drones = new HashSet<>();
    private ModularLevel modularLevel;

    public ModularInfusionControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistration.ModBlockEntities.MODULAR_INFUSION_CONTROLLER_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected boolean isItemAllowed(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ToolItem;
    }

    private void clearDrones() {
        assert world != null;
        for (BlockPos pos : drones) {
            if (world.getBlockEntity(pos) instanceof ModularInfusionDroneBlockEntity mbe) {
                mbe.clearController();
            }
        }
    }

    public void setDrones(HashSet<BlockPos> drones) {
        clearDrones();
        for (BlockPos pos : drones) {
            addDrone(pos);
        }
    }

    public HashSet<BlockPos> getDrones() {
        return drones;
    }

    public void setModularLevel(ModularLevel modularLevel) {
        this.modularLevel = modularLevel;
    }

    public ModularLevel getModularLevel() {
        return modularLevel;
    }

    public List<BlockPos> getDroneAsList() {
        return drones.stream().toList();
    }

    public void addDrone(BlockPos drone) {
        this.drones.add(drone);
        if (world.getBlockEntity(drone) instanceof ModularInfusionDroneBlockEntity droneBlockEntity) {
            droneBlockEntity.setController(this);
        }
    }

    public void removeDrone(BlockPos drone) {
        this.drones.remove(drone);
    }

    public void notifyChanged() {

    }

    private void populateDrones(ModularToolComponentRecord componentRecord) {
        Iterator<BlockPos> droneIterator = drones.iterator();
        // for (Identifier id : componentRecord.modules()) {
        //     if (droneIterator.hasNext()) {
        //         BlockPos pos = droneIterator.next();
        //         assert world != null;
        //         if (world.getBlockEntity(pos) instanceof ModularInfusionDroneBlockEntity drone) {
        //             drone.setActiveStack(Registries.ITEM.get(id).getDefaultStack());
        //         }
        //     }
        // }
    }

    private boolean hasDuplicates(List<ModularInfusionDroneBlockEntity> drones) {
        List<ItemStack> stacks = drones.stream().map(PedestalBlockEntity::getActiveStack).filter(activeStack -> activeStack != ItemStack.EMPTY).toList();
        HashSet<ItemStack> stacksSet = new HashSet<>(stacks);
        return stacksSet.size() != stacks.size();
    }

    /**
     * @title tryInsertModularTool
     * @description determines whether the tool item stack is fit to be inserted into the current modular setup
     * @example FAILS: If the item doesn't have the modular tool component
     * @example FAILS: If the controller has 3 drones, and the tool has 4 modifiers
     * @param stack the ItemStack to attempt inserting
     * @return result either containing the stack that was taken off, or an error specifying the reason the tool could
     * not be inserted
     */
    public Result<ItemStack, InsertToolError> tryInsertModularTool(ItemStack stack) {
        // Stack must have component
        if (!stack.contains(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT)) return Results.failure(InsertToolError.NOT_MODULAR_TOOL(stack));
        ModularToolComponentRecord componentRecord = stack.get(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT);
        if (componentRecord == null) return Results.failure(InsertToolError.NOT_MODULAR_TOOL(stack));

        // Module list must not be null
        // if (componentRecord.getModuleIds() == null) return Results.failure(InsertToolError.NOT_MODULAR_TOOL(stack));

        // Module list must not be larger than the number of drones
        // if (componentRecord.getModuleIds().size() > drones.size()) return Results.failure(InsertToolError.TOO_MANY_MODULES(stack));

        ItemStack old = getActiveStack();
        setActiveStack(stack);
        populateDrones(componentRecord);
        return Results.success(old);
    }

    List<ModularInfusionDroneBlockEntity> collectNonEmptyDrones() {
        return drones.stream()
                .map(pos -> world.getBlockEntity(pos))
                .filter(Objects::nonNull)
                .map(entity -> (ModularInfusionDroneBlockEntity) entity)
                .toList();
    }

    public Result<ItemStack, InfuseToolError> tryInfuseModularTool(ItemStack stack) {
        List<ModularInfusionDroneBlockEntity> drones = collectNonEmptyDrones();
        if (getActiveStack() == ItemStack.EMPTY) return Results.failure(InfuseToolError.NO_TOOL());
        if (hasDuplicates(drones)) return Results.failure(InfuseToolError.DUPLICATE_MODULES(stack));

        ItemStack controllerStack = getActiveStack().copy();
        ModularToolComponentRecord record = new ModularToolComponentRecord();
        for (ModularInfusionDroneBlockEntity drone : collectNonEmptyDrones()) {
            ItemStack droneItemStack = drone.getActiveStack();
            Identifier droneStackId = Registries.ITEM.getId(droneItemStack.getItem());
            if (!(stack.getItem() instanceof ModularPickaxeItem)) {
                setActiveStack(ModRegistration.ModItems.MODULAR_PICKAXE.getDefaultStack());
            }
            // record = record.appendModule(droneStackId);
            controllerStack.set(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT, record);
            drone.setActiveStack(ItemStack.EMPTY);
        }
        setActiveStack(ItemStack.EMPTY);
        return Results.success(controllerStack);
    }

    public boolean isCraftValid() {
        if (getActiveStack() == ItemStack.EMPTY) return false;
        if (world == null) return false;
        return true;
    }

    public boolean performCraft() {
        if (!isCraftValid()) return false;
        if (world == null) return false;
        System.out.println("isCraftValid: " + isCraftValid());
        List<ModularInfusionDroneBlockEntity> drones = collectNonEmptyDrones();
        ItemStack original = getActiveStack();
        ModularToolComponentRecord componentRecord = new ModularToolComponentRecord();
        for (ModularInfusionDroneBlockEntity drone : drones) {
            ItemStack s = drone.getActiveStack();
            Identifier sId = Registries.ITEM.getId(s.getItem());
            if (!ModRegistration.ModRegistries.MODULE_REGISTRY.containsId(sId)) {
                System.out.println("Skipping item: " + s);
                continue;
            }
            if (!(original.getItem() instanceof ModularPickaxeItem)) {
                setActiveStack(ModRegistration.ModItems.MODULAR_PICKAXE.getDefaultStack());
            }
            // componentRecord = componentRecord.appendModule(sId);
            drone.setActiveStack(ItemStack.EMPTY);
        }
        getActiveStack().set(ModRegistration.ModComponents.MODULAR_TOOL_COMPONENT, componentRecord);
        return true;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        NbtList dronesNbt = new NbtList();
        for (BlockPos pos : drones) {
            DataResult<NbtElement> el = BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, pos);
            el.ifSuccess(dronesNbt::add);
        }
        nbt.put("drones", dronesNbt);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        NbtElement dronesNbt = nbt.get("drones");
        if (dronesNbt instanceof NbtList list) {
            for (NbtElement element : list) {
                DataResult<Pair<BlockPos, NbtElement>> pos = BlockPos.CODEC.decode(NbtOps.INSTANCE, element);
                pos.ifSuccess(pair -> {
                    drones.add(pair.getFirst());
                });
            }
        }
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, T t) {
        ModularInfusionControllerBlockEntity mbe = (ModularInfusionControllerBlockEntity) world.getBlockEntity(blockPos);
    }
}

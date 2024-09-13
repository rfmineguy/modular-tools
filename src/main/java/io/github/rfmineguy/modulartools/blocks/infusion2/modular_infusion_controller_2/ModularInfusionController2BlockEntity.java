package io.github.rfmineguy.modulartools.blocks.infusion2.modular_infusion_controller_2;

import com.mojang.serialization.Codec;
import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.ModularLevel;
import io.github.rfmineguy.modulartools.blocks.infusion2.modular_infusion_drone2.ModularInfusionDrone2BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

// Inspiration from EnchantingTableBlockEntity
public class ModularInfusionController2BlockEntity extends BlockEntity {
    public class ToolStackRotationData {
        public float nextToolStackTurningSpeed;
        public float toolStackTurningSpeed;
        public float toolStackRotation;
        public float lastToolStackRotation;
        public float targetToolStackRotation;
    }
    public static class DroneData {
        public static final Codec<DroneData> CODEC = Codec.INT_STREAM
                .<DroneData>comapFlatMap(
                        stream -> Util.decodeFixedLengthArray(stream, 3).map(values -> new DroneData(new BlockPos(values[0], values[1], values[2]))),
                        droneData1 -> IntStream.of(new int[]{droneData1.pos.getX(), droneData1.pos.getY(), droneData1.pos.getZ()})
                ).stable();

        public enum DroneStatus {
            NEVER_PLACED,
            PRESENT,
            PRESENT_WITH_LEVEL_ITEM,
            PRESENT_WITH_NEEDED_LEVEL_ITEM,
            MISSING,
        }
        public ModularInfusionDrone2BlockEntity drone;
        public DroneStatus status;
        public BlockPos pos;

        public DroneData(BlockPos pos) {
            this.drone = null;
            this.pos = pos;
            this.status = DroneStatus.NEVER_PLACED;
        }

        NbtCompound write() {
            NbtCompound data = new NbtCompound();
            data.putInt("posX", this.pos.getX());
            data.putInt("posY", this.pos.getY());
            data.putInt("posZ", this.pos.getZ());
            data.putInt("status", this.status.ordinal());
            return data;
        }

        void read(NbtCompound nbt) {
            int x = nbt.getInt("posX");
            int y = nbt.getInt("posY");
            int z = nbt.getInt("posZ");
            DroneStatus status = DroneStatus.values()[nbt.getInt("status")];
            this.pos = new BlockPos(x, y, z);
            this.status = status;
        }
    }

    private ItemStack toolStack;
    private ModularLevel modularLevel;
    private final ToolStackRotationData toolStackRotationData;
    private final HashMap<BlockPos, DroneData> droneData; // 8 max drones

    public float age;
    public float droneParticleLerp;
    public int ticks;

    public ModularInfusionController2BlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistration.ModBlockEntities.MODULAR_INFUSION_CONTROLLER2_BLOCK_ENTITY, pos, state);
        toolStack = Items.STONE_SWORD.getDefaultStack();
        toolStackRotationData = new ToolStackRotationData();
        this.age = 0;
        this.modularLevel = ModularLevel.NONE;
        this.droneData = new HashMap<>(8);

        // Populate default drone positions
        this.putDroneData(pos.west(3));
        this.putDroneData(pos.east(3));
        this.putDroneData(pos.north(3));
        this.putDroneData(pos.south(3));
        this.putDroneData(pos.west(2).north(2));
        this.putDroneData(pos.west(2).south(2));
        this.putDroneData(pos.east(2).north(2));
        this.putDroneData(pos.east(2).south(2));
    }

    // Helpers
    private void putDroneData(BlockPos pos) {
        this.droneData.put(pos, new DroneData(pos));
    }

    // Entity data
    public ItemStack getToolStack() {
        return toolStack;
    }
    public void setToolStack(ItemStack toolStack) {
        this.toolStack = toolStack;
    }
    public ItemStack getModularLevelStack() {
        return switch (modularLevel) {
            case ZERO  -> ItemStack.EMPTY;
            case ONE   -> ModRegistration.ModBlocks.LEVEL1_BLOCK.asItem().getDefaultStack();
            case TWO   -> ModRegistration.ModBlocks.LEVEL2_BLOCK.asItem().getDefaultStack();
            case THREE -> ModRegistration.ModBlocks.LEVEL3_BLOCK.asItem().getDefaultStack();
            case FOUR  -> ModRegistration.ModBlocks.LEVEL4_BLOCK.asItem().getDefaultStack();
            case FIVE  -> ModRegistration.ModBlocks.LEVEL5_BLOCK.asItem().getDefaultStack();
            case SIX   -> ModRegistration.ModBlocks.LEVEL6_BLOCK.asItem().getDefaultStack();
            case SEVEN -> ModRegistration.ModBlocks.LEVEL7_BLOCK.asItem().getDefaultStack();
            case EIGHT -> ModRegistration.ModBlocks.LEVEL8_BLOCK.asItem().getDefaultStack();
            default -> ItemStack.EMPTY;
        };
    }
    public void addDrone(ModularInfusionDrone2BlockEntity drone) {
        if (!this.droneData.containsKey(drone.getPos())) return;
        this.droneData.get(drone.getPos()).drone = drone;
        this.droneData.get(drone.getPos()).status = DroneData.DroneStatus.PRESENT;
        drone.setConnectedController(this);
        this.updateModularLevel();
        this.markDirty();
    }
    public void removeDrone(ModularInfusionDrone2BlockEntity drone) {
        if (!this.droneData.containsKey(drone.getPos())) return;
        this.droneData.get(drone.getPos()).drone = null;
        this.droneData.get(drone.getPos()).status = DroneData.DroneStatus.MISSING;
        this.updateModularLevel();
        this.markDirty();
    }

    public boolean revalidateMultiBlock() {
        boolean isComplete = true;
        if (world == null) return false;
        for (DroneData data : droneData.values()) {
            if (world.getBlockEntity(data.pos) instanceof ModularInfusionDrone2BlockEntity drone) {
                data.drone = drone;
                data.status = DroneData.DroneStatus.PRESENT;
                System.out.println("Validated: " + data.pos + ", Status: " + data.status);
                continue;
            }
            isComplete = false;
            break;
        }
        return isComplete;
    }
    public List<DroneData> getDrones() {
        return droneData.values().stream().toList();
    }

    public ModularLevel getMaxLevelDrone() {
        return droneData.values().stream().filter(data -> data.drone != null).map(data -> data.drone).map(ModularInfusionDrone2BlockEntity::getModularLevel).max(ModularLevel.comparator).orElse(ModularLevel.NONE);
    }
    public void updateModularLevel() {
        this.modularLevel = getMaxLevelDrone();
        this.markDirty();
    }
    public ModularLevel getModularLevel() {
        return modularLevel;
    }

    public boolean isMultiblockComplete() {
        boolean complete = true;
        for (DroneData data : droneData.values()) {
            if (data.drone == null) {
                complete = false;
                break;
            }
        }
        return complete;
    }

    // NBT
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
    }
    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        revalidateMultiBlock();
    }

    // Rendering data
    public float getAge() {
        return age;
    }
    public ToolStackRotationData getToolStackRotationData() {
        return toolStackRotationData;
    }

    // Entity logic
    private void updateRotationData(World world, BlockPos pos, BlockState state) {
        age += 0.01f;
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
    private void updateParticleLerp() {
        if (ticks % 2 == 0) {
            droneParticleLerp += 0.05f;
            if (droneParticleLerp > 1) droneParticleLerp = 0;
        }
    }
    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T t) {
        if (!(t instanceof ModularInfusionController2BlockEntity blockEntity)) return;
        blockEntity.updateRotationData(world, pos, state);
        blockEntity.updateParticleLerp();

        blockEntity.ticks ++;
    }
}

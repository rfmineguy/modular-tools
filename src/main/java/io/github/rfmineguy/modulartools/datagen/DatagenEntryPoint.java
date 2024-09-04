package io.github.rfmineguy.modulartools.datagen;

import com.google.gson.JsonObject;
import io.github.rfmineguy.modulartools.ModRegistration;
import io.github.rfmineguy.modulartools.ModularToolsMod;
import io.github.rfmineguy.modulartools.items.ActivatorItem;
import io.github.rfmineguy.modulartools.modules.Module;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class DatagenEntryPoint implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(RecipeProvider::new);
        pack.addProvider(ModelProvider::new);
        pack.addProvider(LangProvider::new);
    }

    private static class RecipeProvider extends FabricRecipeProvider {

        public RecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public void generate(RecipeExporter exporter) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModRegistration.ModBlocks.MODULAR_INFUSION_CONTROLLER)
                    .pattern("mmm")
                    .pattern("mcm")
                    .pattern("mmm")
                    .input('m', ModRegistration.ModItems.MODULARIUM)
                    .input('c', Items.CRAFTING_TABLE)
                    .criterion(FabricRecipeProvider.hasItem(ModRegistration.ModItems.MODULARIUM), FabricRecipeProvider.conditionsFromItem(ModRegistration.ModItems.MODULARIUM))
                    .criterion(FabricRecipeProvider.hasItem(Items.CRAFTING_TABLE), FabricRecipeProvider.conditionsFromItem(Items.CRAFTING_TABLE))
                    .offerTo(exporter);
        }
    }
    private static class ModelProvider extends FabricModelProvider {
        public ModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            blockStateModelGenerator.registerSimpleCubeAll(ModRegistration.ModBlocks.DEEPSLATE_MODULARIUM_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(ModRegistration.ModBlocks.MODULARIUM_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(ModRegistration.ModBlocks.ERROR_BLOCK);
            blockStateModelGenerator.registerSimpleCubeAll(ModRegistration.ModBlocks.LEVEL1_BLOCK);
            blockStateModelGenerator.registerSimpleCubeAll(ModRegistration.ModBlocks.LEVEL2_BLOCK);
            blockStateModelGenerator.registerSimpleCubeAll(ModRegistration.ModBlocks.LEVEL3_BLOCK);
            blockStateModelGenerator.registerSimpleCubeAll(ModRegistration.ModBlocks.LEVEL4_BLOCK);
            blockStateModelGenerator.registerSimpleCubeAll(ModRegistration.ModBlocks.LEVEL5_BLOCK);
            blockStateModelGenerator.registerSimpleCubeAll(ModRegistration.ModBlocks.LEVEL6_BLOCK);
            blockStateModelGenerator.registerSimpleCubeAll(ModRegistration.ModBlocks.LEVEL7_BLOCK);
            blockStateModelGenerator.registerSimpleCubeAll(ModRegistration.ModBlocks.LEVEL8_BLOCK);
            // blockStateModelGenerator.registerSimpleCubeAll(Registration.TEST_CONTROLLER);

            blockStateModelGenerator.registerSimpleState(ModRegistration.ModBlocks.MODULAR_INFUSION_DRONE);
            blockStateModelGenerator.registerSimpleState(ModRegistration.ModBlocks.MODULAR_INFUSION_CONTROLLER);
            blockStateModelGenerator.registerSimpleState(ModRegistration.ModBlocks.MODULAR_INFUSION_DRONE2);
            blockStateModelGenerator.registerSimpleState(ModRegistration.ModBlocks.MODULAR_INFUSION_CONTROLLER2);
            blockStateModelGenerator.registerSimpleState(ModRegistration.ModBlocks.TEST_CONTROLLER);
        }

        Identifier idOf(Item item) {
            return Registries.ITEM.getId(item);
        }
        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            itemModelGenerator.register(ModRegistration.ModItems.MODULARIUM, Models.GENERATED);
            itemModelGenerator.register(ModRegistration.ModItems.MODULAR_PICKAXE, Models.GENERATED);
            itemModelGenerator.register(ModRegistration.ModItems.MODULAR_SHOVEL, Models.GENERATED);

            itemModelGenerator.writer.accept(Registries.ITEM.getId(ModRegistration.ModItems.LEVEL1_ACTIVATOR).withPrefixedPath("item/"), () -> {
                return generateActivatorItemModel(ModRegistration.ModItems.LEVEL1_ACTIVATOR);
            });
            ModRegistration.ModRegistries.MODULE_REGISTRY.forEach(module -> {
                Identifier id = ModRegistration.ModRegistries.MODULE_REGISTRY.getId(module);
                assert id != null;
                itemModelGenerator.writer.accept(id.withPrefixedPath("item/"), () -> {
                    return moduleItem(module);
                });
            });
        }

        private JsonObject generateActivatorItemModel(ActivatorItem item) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("parent", "minecraft:builtin/entity");
            jsonObject.add("textures", new JsonObject());
            return jsonObject;
        }

        private JsonObject moduleItem(Module module) {
            String[] module_split = module.id().getPath().toString().split("\\.");
            if (module_split.length != 2) {
                System.err.printf("Module id not proper format %s%n", module.id().toString());
                return new JsonObject();
            }
            String module_type = module_split[0];
            String module_tier = module_split[1];
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("parent", "minecraft:item/generated");
            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", Identifier.of(ModularToolsMod.MODID, "item/upgrade/background_tiers/"+module_tier).toString());
            textures.addProperty("layer1", Identifier.of(ModularToolsMod.MODID, "item/upgrade/overlay/"+module_type).toString());
            jsonObject.add("textures", textures);
            return jsonObject;
        }
    }
    private static class LangProvider extends FabricLanguageProvider {
        protected LangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        private String upperFirst(String s) {
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }

        private String getRoman(int level) {
            return switch (level) {
                case 1 -> "I";
                case 2 -> "II";
                case 3 -> "III";
                case 4 -> "IV";
                default -> "Unreachable";
            };
        }

        @Override
        public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
            translationBuilder.add(ModRegistration.ModItems.MODULARIUM, "Modularium");
            translationBuilder.add(ModRegistration.ModBlocks.MODULARIUM_ORE, "Modularium Ore");
            translationBuilder.add(ModRegistration.ModBlocks.DEEPSLATE_MODULARIUM_ORE, "Deepslate Modularium Ore");

            translationBuilder.add(ModRegistration.ModBlocks.MODULAR_INFUSION_CONTROLLER, "Modular Infusion Controller");
            translationBuilder.add(ModRegistration.ModBlocks.MODULAR_INFUSION_DRONE, "Modular Infusion Drone");

            translationBuilder.add(ModRegistration.ModItems.MODULAR_PICKAXE, "Modular Pickaxe");

            translationBuilder.add(ModRegistration.ModBlocks.LEVEL1_BLOCK, "Level I Block");
            translationBuilder.add(ModRegistration.ModBlocks.LEVEL2_BLOCK, "Level II Block");
            translationBuilder.add(ModRegistration.ModBlocks.LEVEL3_BLOCK, "Level III Block");
            translationBuilder.add(ModRegistration.ModBlocks.LEVEL4_BLOCK, "Level IV Block");

            ModRegistration.ModRegistries.MODULE_REGISTRY.forEach(module -> {
                Identifier moduleName = module.id();
                System.out.println(moduleName.getPath());
                String[] split = moduleName.getPath().split("\\.");
                if (split.length != 2) {
                    System.err.println("Module: " + moduleName + " has incorrect format");
                    return;
                }
                String itemName = "%s %s Module".formatted(upperFirst(split[0]), upperFirst(split[1]));
                translationBuilder.add(Registries.ITEM.get(moduleName), itemName);
            });
        }
    }
}

package io.github.rfmineguy.modulartools.datagen;

import io.github.rfmineguy.modulartools.Registration;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class DatagenEntryPoint implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(RecipeProvider::new);
        pack.addProvider(ModelProvider::new);
    }

    private static class RecipeProvider extends FabricRecipeProvider {

        public RecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public void generate(RecipeExporter exporter) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Registration.MODULAR_INFUSION_CONTROLLER)
                    .pattern("mmm")
                    .pattern("mcm")
                    .pattern("mmm")
                    .input('m', Registration.MODULARIUM)
                    .input('c', Items.CRAFTING_TABLE)
                    .criterion(FabricRecipeProvider.hasItem(Registration.MODULARIUM), FabricRecipeProvider.conditionsFromItem(Registration.MODULARIUM))
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
            blockStateModelGenerator.registerSimpleCubeAll(Registration.DEEPSLATE_MODULARIUM_ORE);
            blockStateModelGenerator.registerSimpleCubeAll(Registration.MODULARIUM_ORE);
            blockStateModelGenerator.registerSimpleState(Registration.MODULAR_INFUSION_DRONE);
            blockStateModelGenerator.registerSimpleState(Registration.MODULAR_INFUSION_CONTROLLER);
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            itemModelGenerator.register(Registration.MODULARIUM, Models.GENERATED);
        }
    }
}

package net.neganote.gtutilities.saveddata;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PTERBSavedData extends SavedData {

    public Map<Integer, List<Pair<ResourceLocation, BlockPos>>> energyInputs;
    public Map<Integer, List<Pair<ResourceLocation, BlockPos>>> energyOutputs;

    public static String DATA_NAME = "pterb_energy";
    public static String ENERGY_INPUT_FREQUENCIES = "pterb_energy_input_frequencies";
    public static String ENERGY_OUTPUT_FREQUENCIES = "pterb_energy_output_frequencies";

    private PTERBSavedData() {
        energyInputs = new HashMap<>();
        energyOutputs = new HashMap<>();
    }

    private PTERBSavedData(CompoundTag tag) {
        this();
        var energyInputsTag = tag.getCompound("energy_inputs");
        var energyOutputsTag = tag.getCompound("energy_outputs");

        var energyInputFrequencies = energyInputsTag.getList(ENERGY_INPUT_FREQUENCIES, CompoundTag.TAG_COMPOUND);
        for (Tag t1 : energyInputFrequencies) {
            CompoundTag freqCompoundTag = (CompoundTag) t1;
            int freq = freqCompoundTag.getInt("frequency");
            var inputs = freqCompoundTag.getList("inputs", CompoundTag.TAG_COMPOUND);
            List<Pair<ResourceLocation, BlockPos>> energyInputPairs = new ArrayList<>();
            for (Tag t2 : inputs) {
                CompoundTag energyInputTag = (CompoundTag) t2;
                String dimensionNamespace = energyInputTag.getString("dimension_namespace");
                String dimensionPath = energyInputTag.getString("dimension_path");
                ResourceLocation dimension = new ResourceLocation(dimensionNamespace, dimensionPath);
                int x = energyInputTag.getInt("block_pos_x");
                int y = energyInputTag.getInt("block_pos_y");
                int z = energyInputTag.getInt("block_pos_z");
                BlockPos pos = new BlockPos(x, y, z);
                energyInputPairs.add(new Pair<>(dimension, pos));
            }
            energyInputs.put(freq, energyInputPairs);
        }

        var energyOutputFrequencies = energyOutputsTag.getList(ENERGY_OUTPUT_FREQUENCIES, CompoundTag.TAG_COMPOUND);
        for (Tag t1 : energyOutputFrequencies) {
            CompoundTag freqCompoundTag = (CompoundTag) t1;
            int freq = freqCompoundTag.getInt("frequency");
            var outputs = freqCompoundTag.getList("outputs", CompoundTag.TAG_COMPOUND);
            List<Pair<ResourceLocation, BlockPos>> energyOutputPairs = new ArrayList<>();
            for (Tag t2 : outputs) {
                CompoundTag energyOutputTag = (CompoundTag) t2;
                String dimensionNamespace = energyOutputTag.getString("dimension_namespace");
                String dimensionPath = energyOutputTag.getString("dimension_path");
                ResourceLocation dimension = new ResourceLocation(dimensionNamespace, dimensionPath);
                int x = energyOutputTag.getInt("block_pos_x");
                int y = energyOutputTag.getInt("block_pos_y");
                int z = energyOutputTag.getInt("block_pos_z");
                BlockPos pos = new BlockPos(x, y, z);
                energyOutputPairs.add(new Pair<>(dimension, pos));
            }
            energyOutputs.put(freq, energyOutputPairs);
        }
    }

    public static PTERBSavedData getOrCreate(ServerLevel serverLevel) {
        return serverLevel.getDataStorage().computeIfAbsent(PTERBSavedData::new,
                PTERBSavedData::new, DATA_NAME);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        return nbt;
    }
}

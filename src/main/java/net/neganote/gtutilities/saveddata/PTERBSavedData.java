package net.neganote.gtutilities.saveddata;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import com.mojang.datafixers.util.Pair;
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
            List<Pair<ResourceLocation, BlockPos>> energyInputPairs = getEnergyPairs(inputs);
            energyInputs.put(freq, energyInputPairs);
        }

        var energyOutputFrequencies = energyOutputsTag.getList(ENERGY_OUTPUT_FREQUENCIES, CompoundTag.TAG_COMPOUND);
        for (Tag t1 : energyOutputFrequencies) {
            CompoundTag freqCompoundTag = (CompoundTag) t1;
            int freq = freqCompoundTag.getInt("frequency");
            var outputs = freqCompoundTag.getList("outputs", CompoundTag.TAG_COMPOUND);
            List<Pair<ResourceLocation, BlockPos>> energyOutputPairs = getEnergyPairs(outputs);
            energyOutputs.put(freq, energyOutputPairs);
        }
    }

    private static @NotNull List<Pair<ResourceLocation, BlockPos>> getEnergyPairs(ListTag tags) {
        List<Pair<ResourceLocation, BlockPos>> energyPairs = new ArrayList<>();
        for (Tag t2 : tags) {
            CompoundTag energyTag = (CompoundTag) t2;
            String dimensionNamespace = energyTag.getString("dimension_namespace");
            String dimensionPath = energyTag.getString("dimension_path");
            ResourceLocation dimension = new ResourceLocation(dimensionNamespace, dimensionPath);
            int x = energyTag.getInt("block_pos_x");
            int y = energyTag.getInt("block_pos_y");
            int z = energyTag.getInt("block_pos_z");
            BlockPos pos = new BlockPos(x, y, z);
            energyPairs.add(new Pair<>(dimension, pos));
        }
        return energyPairs;
    }

    public static PTERBSavedData getOrCreate(ServerLevel serverLevel) {
        return serverLevel.getDataStorage().computeIfAbsent(PTERBSavedData::new,
                PTERBSavedData::new, DATA_NAME);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        CompoundTag energyInputsTag = new CompoundTag();
        CompoundTag energyOutputsTag = new CompoundTag();

        ListTag energyInputFrequencies = new ListTag();
        for (Integer freq : energyInputs.keySet()) {
            CompoundTag freqCompoundTag = new CompoundTag();
            freqCompoundTag.putInt("frequency", freq);

            List<Pair<ResourceLocation, BlockPos>> energyInputPairs = energyInputs.get(freq);
            ListTag inputs = getEnergyTags(energyInputPairs);
            freqCompoundTag.put("inputs", inputs);
            energyInputFrequencies.add(freqCompoundTag);
        }
        energyInputsTag.put(ENERGY_INPUT_FREQUENCIES, energyInputFrequencies);
        nbt.put("energy_inputs", energyInputsTag);

        ListTag energyOutputFrequencies = new ListTag();
        for (Integer freq : energyInputs.keySet()) {
            CompoundTag freqCompoundTag = new CompoundTag();
            freqCompoundTag.putInt("frequency", freq);

            List<Pair<ResourceLocation, BlockPos>> energyInputPairs = energyInputs.get(freq);
            ListTag inputs = getEnergyTags(energyInputPairs);
            freqCompoundTag.put("outputs", inputs);
            energyOutputFrequencies.add(freqCompoundTag);
        }
        energyOutputsTag.put(ENERGY_OUTPUT_FREQUENCIES, energyOutputFrequencies);
        nbt.put("energy_outputs", energyOutputsTag);

        return nbt;
    }

    private static @NotNull ListTag getEnergyTags(List<Pair<ResourceLocation, BlockPos>> pairs) {
        ListTag tags = new ListTag();
        for (Pair<ResourceLocation, BlockPos> pair : pairs) {
            CompoundTag energyTag = new CompoundTag();
            ResourceLocation dimension = pair.getFirst();
            energyTag.putString("dimension_namespace", dimension.getNamespace());
            energyTag.putString("dimension_path", dimension.getPath());
            BlockPos pos = pair.getSecond();
            energyTag.putInt("block_pos_x", pos.getX());
            energyTag.putInt("block_pos_y", pos.getY());
            energyTag.putInt("block_pos_z", pos.getZ());
            tags.add(energyTag);
        }
        return tags;
    }
}

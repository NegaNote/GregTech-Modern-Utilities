package net.neganote.gtutilities.saveddata;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.LaserHatchPartMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
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

    private final ServerLevel serverLevel;

    private PTERBSavedData(ServerLevel serverLevel) {
        energyInputs = new HashMap<>();
        energyOutputs = new HashMap<>();
        this.serverLevel = serverLevel;
    }

    private PTERBSavedData(ServerLevel serverLevel, CompoundTag tag) {
        this(serverLevel);
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
        return serverLevel.getDataStorage().computeIfAbsent((tag) -> new PTERBSavedData(serverLevel, tag),
                () -> new PTERBSavedData(serverLevel), DATA_NAME);
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

    public void addEnergyInputs(int freq, List<IMultiPart> parts) {
        List<Pair<ResourceLocation, BlockPos>> inputPairs = energyInputs.computeIfAbsent(freq,
                (f) -> new ArrayList<>());
        boolean changed = false;
        for (IMultiPart part : parts) {
            if (part instanceof MetaMachine machine) {
                ServerLevel level = (ServerLevel) machine.getLevel();
                assert level != null;
                ResourceLocation dimension = level.dimension().location();
                BlockPos pos = machine.getPos();
                Pair<ResourceLocation, BlockPos> pair = new Pair<>(dimension, pos);
                if (!inputPairs.contains(pair)) {
                    inputPairs.add(pair);
                    changed = true;
                }
            }
        }
        if (changed) {
            setDirty();
        }
        energyInputs.put(freq, inputPairs);
    }

    public void removeEnergyInputs(int freq, List<IMultiPart> parts) {
        List<Pair<ResourceLocation, BlockPos>> inputPairs = energyInputs.computeIfAbsent(freq,
                (f) -> new ArrayList<>());
        boolean changed = false;
        for (IMultiPart part : parts) {
            if (part instanceof MetaMachine machine) {
                ServerLevel level = (ServerLevel) machine.getLevel();
                assert level != null;
                ResourceLocation dimension = level.dimension().location();
                BlockPos pos = machine.getPos();
                Pair<ResourceLocation, BlockPos> pair = new Pair<>(dimension, pos);
                if (inputPairs.remove(pair)) {
                    changed = true;
                }
            }
        }
        if (changed) {
            setDirty();
        }
        energyInputs.put(freq, inputPairs);
    }

    public void addEnergyOutputs(int freq, List<IMultiPart> parts) {
        List<Pair<ResourceLocation, BlockPos>> outputPairs = energyOutputs.computeIfAbsent(freq,
                (f) -> new ArrayList<>());
        boolean changed = false;
        for (IMultiPart part : parts) {
            if (part instanceof MetaMachine machine) {
                ServerLevel level = (ServerLevel) machine.getLevel();
                assert level != null;
                ResourceLocation dimension = level.dimension().location();
                BlockPos pos = machine.getPos();
                Pair<ResourceLocation, BlockPos> pair = new Pair<>(dimension, pos);
                if (!outputPairs.contains(pair)) {
                    outputPairs.add(pair);
                    changed = true;
                }
            }
        }
        if (changed) {
            setDirty();
        }
        energyOutputs.put(freq, outputPairs);
    }

    public void removeEnergyOutputs(int freq, List<IMultiPart> parts) {
        List<Pair<ResourceLocation, BlockPos>> outputPairs = energyOutputs.computeIfAbsent(freq,
                (f) -> new ArrayList<>());
        boolean changed = false;
        for (IMultiPart part : parts) {
            if (part instanceof MetaMachine machine) {
                ServerLevel level = (ServerLevel) machine.getLevel();
                assert level != null;
                ResourceLocation dimension = level.dimension().location();
                BlockPos pos = machine.getPos();
                Pair<ResourceLocation, BlockPos> pair = new Pair<>(dimension, pos);
                if (outputPairs.remove(pair)) {
                    changed = true;
                }
            }
        }
        if (changed) {
            setDirty();
        }
        energyOutputs.put(freq, outputPairs);
    }

    public void saveDataToCache() {
        if (isDirty()) {
            serverLevel.getDataStorage().set(DATA_NAME, this);
            serverLevel.getDataStorage().save();
            setDirty(false);
        }
    }

    public EnergyContainerList getWirelessEnergyInputs(int freq) {
        List<Pair<ResourceLocation, BlockPos>> inputPairs = energyInputs.computeIfAbsent(freq,
                (f) -> new ArrayList<>());
        List<IEnergyContainer> energyContainerList = new ArrayList<>();
        for (Pair<ResourceLocation, BlockPos> pair : inputPairs) {
            ServerLevel dimension = serverLevel.getServer()
                    .getLevel(ResourceKey.create(Registries.DIMENSION, pair.getFirst()));
            if (dimension != null) {
                MetaMachine machine = MetaMachine.getMachine(dimension, pair.getSecond());
                if (machine instanceof EnergyHatchPartMachine hatch) {
                    energyContainerList.add(hatch.energyContainer);
                }
                if (machine instanceof LaserHatchPartMachine hatch) {
                    // unfortunately the laser hatch's buffer is private, so I have to do this instead
                    for (var handler : hatch.getRecipeHandlers()) {
                        if (handler.getCapability() == EURecipeCapability.CAP &&
                                handler instanceof IEnergyContainer container) {
                            energyContainerList.add(container);
                        }
                    }
                }
            }
        }
        return new EnergyContainerList(energyContainerList);
    }

    public EnergyContainerList getWirelessEnergyOutputs(int freq) {
        List<Pair<ResourceLocation, BlockPos>> outputPairs = energyOutputs.computeIfAbsent(freq,
                (f) -> new ArrayList<>());
        List<IEnergyContainer> energyContainerList = new ArrayList<>();
        for (Pair<ResourceLocation, BlockPos> pair : outputPairs) {
            ServerLevel dimension = serverLevel.getServer()
                    .getLevel(ResourceKey.create(Registries.DIMENSION, pair.getFirst()));
            if (dimension != null) {
                MetaMachine machine = MetaMachine.getMachine(dimension, pair.getSecond());

                if (machine instanceof EnergyHatchPartMachine hatch) {
                    energyContainerList.add(hatch.energyContainer);
                }
                if (machine instanceof LaserHatchPartMachine hatch) {
                    // unfortunately the laser hatch's buffer is private, so I have to do this instead
                    for (var handler : hatch.getRecipeHandlers()) {
                        if (handler.getCapability() == EURecipeCapability.CAP &&
                                handler instanceof IEnergyContainer container) {
                            energyContainerList.add(container);
                        }
                    }
                }
            }
        }
        return new EnergyContainerList(energyContainerList);
    }
}

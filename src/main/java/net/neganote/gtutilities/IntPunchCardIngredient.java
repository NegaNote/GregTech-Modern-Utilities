package net.neganote.gtutilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import com.gregtechceu.gtceu.core.mixins.StrictNBTIngredientAccessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.neganote.gtutilities.common.item.UtilItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IntPunchCardIngredient extends StrictNBTIngredient {

    public static final ResourceLocation TYPE = GregTechModernUtilities.id("punch_card");

    public static final int CIRCUIT_MIN = 0;
    public static final int CIRCUIT_MAX = 32;

    private static final IntPunchCardIngredient[] INGREDIENTS = new IntPunchCardIngredient[CIRCUIT_MAX + 1];

    public static IntPunchCardIngredient circuitInput(int configuration) {
        if (configuration < CIRCUIT_MIN || configuration > CIRCUIT_MAX) {
            throw new IndexOutOfBoundsException("Circuit configuration " + configuration + " is out of range");
        }
        IntPunchCardIngredient ingredient = INGREDIENTS[configuration];
        if (ingredient == null) {
            INGREDIENTS[configuration] = ingredient = new IntPunchCardIngredient(configuration);
        }
        return ingredient;
    }

    private final int configuration;
    private ItemStack[] stacks;

    protected IntPunchCardIngredient(int configuration) {
        super(UtilItems.punchCard(1, configuration));
        this.configuration = configuration;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null) return false;
        return stack.is(UtilItems.PUNCH_CARD.get()) &&
                IntCircuitBehaviour.getCircuitConfiguration(stack) == this.configuration;
    }

    @Override
    public ItemStack[] getItems() {
        if (stacks == null) {
            stacks = new ItemStack[]{((StrictNBTIngredientAccessor) this).getStack()};
        }
        return stacks;
    }

    public IntPunchCardIngredient copy() {
        return new IntPunchCardIngredient(this.configuration);
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", TYPE.toString());
        json.addProperty("configuration", configuration);
        return json;
    }

    @Override
    @NotNull
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return SERIALIZER;
    }

    public static IntPunchCardIngredient fromJson(JsonObject json) {
        return SERIALIZER.parse(json);
    }

    public static final IIngredientSerializer<IntPunchCardIngredient> SERIALIZER = new IIngredientSerializer<>() {

        @Override
        public @NotNull IntPunchCardIngredient parse(FriendlyByteBuf buffer) {
            int configuration = buffer.readVarInt();
            return new IntPunchCardIngredient(configuration);
        }

        @Override
        public @NotNull IntPunchCardIngredient parse(JsonObject json) {
            int configuration = json.get("configuration").getAsInt();
            return new IntPunchCardIngredient(configuration);
        }

        @Override
        public void write(FriendlyByteBuf buffer, IntPunchCardIngredient ingredient) {
            buffer.writeVarInt(ingredient.configuration);
        }
    };
}
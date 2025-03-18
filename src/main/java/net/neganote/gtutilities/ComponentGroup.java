package net.neganote.gtutilities;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import net.minecraft.world.item.Item;

public class ComponentGroup extends ComponentItem {
    public String group;
    protected ComponentGroup(String group, Properties properties) {
        super(properties);
        this.group = group;
    }

    public static ComponentGroup create(String group, Item.Properties properties) {
        return new ComponentGroup(group, properties);
    }
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ComponentGroup && ((ComponentGroup) obj).group.equals(this.group);
    }
}

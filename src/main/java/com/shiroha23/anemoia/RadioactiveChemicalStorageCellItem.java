package com.shiroha23.anemoia;

import me.ramidzkh.mekae2.ae2.MekanismKey;
import me.ramidzkh.mekae2.ae2.MekanismKeyType;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import appeng.api.stacks.AEKey;
import appeng.items.storage.BasicStorageCell;
import appeng.items.storage.StorageTier;

public class RadioactiveChemicalStorageCellItem extends BasicStorageCell {

    public RadioactiveChemicalStorageCellItem(Properties properties, StorageTier tier, ItemLike housingItem) {
        super(properties, tier.componentSupplier().get(), housingItem, tier.idleDrain(), tier.bytes() / 1024,
            tier.bytes() / 128, 1, MekanismKeyType.TYPE);
    }

    @Override
    public boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
        if (requestedAddition instanceof MekanismKey key) {
            return ChemicalAttributeValidator.DEFAULT.process(key.getStack());
        }

        return true;
    }
}

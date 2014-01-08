package com.turt2live.dumbauction;

import com.flobi.WhatIsIt.WhatIsIt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class WhatIsItHook {

    public String getName(ItemStack name) {
        return WhatIsIt.itemName(name, true);
    }

    public String getName(Enchantment enchantment) {
        return WhatIsIt.enchantmentName(enchantment, true);
    }

}

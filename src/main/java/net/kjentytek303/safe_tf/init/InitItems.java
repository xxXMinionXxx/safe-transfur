package net.kjentytek303.safe_tf.init;

import net.kjentytek303.safe_tf.SafeTF;
import net.kjentytek303.safe_tf.item.ConsciousnessSyringeItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class InitItems {
	
	public static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SafeTF.MODID);

	public static final RegistryObject<Item> CONSCIOUSNESS_SYRINGE = ITEM_REGISTRY.register(
		"consciousness_syringe",
		()-> new ConsciousnessSyringeItem( new Item.Properties().stacksTo(1) )
	);
}

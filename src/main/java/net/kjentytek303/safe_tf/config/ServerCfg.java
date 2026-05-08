package net.kjentytek303.safe_tf.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerCfg {

	public static final ForgeConfigSpec SPEC;
	public static final ForgeConfigSpec.Builder BUILDER;

	public static final ForgeConfigSpec.ConfigValue<CAddonHandleMode> HANDLE_CADDON;


	static {
		BUILDER = new ForgeConfigSpec.Builder();
		BUILDER.comment("How should (Un)Safe Transfur handle CAddon interaction");
		BUILDER.comment("Valid Values: FORCE_SAFE_TF, PLAY_CADDON_MINIGAME");
		BUILDER.comment("Default: PLAY_CADDON_MINIGAME");
		BUILDER.comment("Note: CAddon must be installed in order for it to work");
		HANDLE_CADDON = BUILDER.defineEnum("SyringeHandle", CAddonHandleMode.PLAY_CADDON_MINIGAME);
		SPEC = BUILDER.build();
	}

	public enum CAddonHandleMode {
		FORCE_SAFE_TF,
		PLAY_CADDON_MINIGAME
	}
}
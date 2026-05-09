package net.kjentytek303.safe_tf.effect;

import net.foxyas.changedaddon.ChangedAddonMod;
import net.foxyas.changedaddon.init.ChangedAddonGameRules;
import net.foxyas.changedaddon.network.ChangedAddonVariables;
import net.foxyas.changedaddon.network.packet.ClientboundOpenFTKCScreenPacket;
import net.foxyas.changedaddon.qte.FightToKeepConsciousness.MinigameType;
import net.kjentytek303.safe_tf.config.ServerCfg;
import net.kjentytek303.safe_tf.init.InitEffects;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.foxyas.changedaddon.qte.FightToKeepConsciousness.failFTKC;
import static net.foxyas.changedaddon.qte.FightToKeepConsciousness.getStruggleNeed;
import static net.foxyas.changedaddon.qte.FightToKeepConsciousness.getStruggleTime;
import static net.foxyas.changedaddon.qte.FightToKeepConsciousness.successFTKC;


public class UnsafeSerum extends MobEffect {

	public UnsafeSerum() {
		super(MobEffectCategory.NEUTRAL, 0x9F9F9F);
	}

	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}

	//Ban milk from curing this shit
	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@Override
	public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
		//Deal damage similar to poison V
		if (pLivingEntity.getHealth() > 3.5F) {
			pLivingEntity.hurt(pLivingEntity.damageSources().wither(), 3.0F);
		}
		else if (pLivingEntity.getHealth() > 1.5f) {
			pLivingEntity.hurt(pLivingEntity.damageSources().wither(), 1.0F);
		}
	}

	@Mod.EventBusSubscriber
	public static class TransfurEventHandler {
		@SubscribeEvent
		public static void onTransfur(ProcessTransfur.KeepConsciousEvent event) {
			if (event.shouldKeepConscious || event.player == null) {return;}

			if (event.player.getEffect(InitEffects.UNSAFE_SERUM.get()) == null) {
				return;
			}

			//TODO: Possibly move this to a function array.
			//Player must have the effect.
			if (!ModList.get().isLoaded("changed_addon") || ServerCfg.HANDLE_CADDON.get() == ServerCfg.CAddonHandleMode.FORCE_SAFE_TF) {
				event.shouldKeepConscious = true;
				return;
			}

			//CAddon is loaded
			Level level = event.player.level();
			if (!(event.player instanceof ServerPlayer player)) {return;}

			ChangedAddonVariables.PlayerVariables vars = ChangedAddonVariables.ofOrDefault(event.player);
			if (!vars.isTransfuredBySafeMethod) {
				vars.isTransfuredBySafeMethod = true;
				vars.syncPlayerVariables(player);
			}
			if (!level.getGameRules().getBoolean(ChangedAddonGameRules.FIGHT_TO_KEEP_CONSCIOUSNESS)) {
				ChangedAddonVariables.PlayerVariables vars1 = ChangedAddonVariables.ofOrDefault(player);
				MinigameType minigameType = MinigameType.getRandom(player.getRandom());
				vars1.isTransfuredBySafeMethod = false;
				updatePlayerVariables(vars1, minigameType, 0, player);
				ChangedAddonMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundOpenFTKCScreenPacket(minigameType));
				event.shouldKeepConscious = true;
			}
		}

		@SubscribeEvent
		public static void onTick(TickEvent.PlayerTickEvent event) {
			if (ModList.get().isLoaded("changed_addon")) {
				if (event.phase != TickEvent.Phase.END) {return;}
				if (!(event.player instanceof ServerPlayer)) {return;}
				if (!event.player.isAlive()) {return;}

				ServerPlayer player = (ServerPlayer) event.player;
				ChangedAddonVariables.PlayerVariables vars = ChangedAddonVariables.ofOrDefault(player);

				if (vars.FTKCminigameType == null) {
					return;
				}

				TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);

				if (instance == null) {
					successFTKC(vars, player);
					return;
				}

				++vars.ticksFightingForConsciousness;
				vars.syncPlayerVariables(player);
				if (vars.ticksFightingForConsciousness >= getStruggleTime()) {
					if ((double) vars.consciousnessFightProgress >= getStruggleNeed()) {
						successFTKC(vars, player);
						return;
					}

					failFTKC(vars, player);
				}
			}
		}

	}

	private static void updatePlayerVariables(ChangedAddonVariables.PlayerVariables vars, MinigameType minigameType, int progress, Entity entity) {
		vars.FTKCminigameType = minigameType;
		vars.consciousnessFightProgress = progress;
		vars.syncPlayerVariables(entity);
	}

}

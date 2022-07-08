package carpettisaddition.mixins.logger.phantom;

import carpettisaddition.logging.loggers.phantom.PhantomLogger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.gen.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PhantomSpawner.class)
public abstract class PhantomSpawnerMixin
{
	private PlayerEntity currentPlayer$TISCM$phantomLogger = null;

	@ModifyVariable(
			method = "spawn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"
			)
	)
	private PlayerEntity recordsCurrentPlayer$phantomLogger(PlayerEntity player)
	{
		this.currentPlayer$TISCM$phantomLogger = player;
		return player;
	}

	@ModifyVariable(
			method = "spawn",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/entity/EntityType;PHANTOM:Lnet/minecraft/entity/EntityType;"
			),
			ordinal = 1
	)
	private int logPlayerSpawningPhantoms(int amount)
	{
		if (this.currentPlayer$TISCM$phantomLogger != null)
		{
			PhantomLogger.getInstance().onPhantomSpawn(this.currentPlayer$TISCM$phantomLogger, amount);
			this.currentPlayer$TISCM$phantomLogger = null;
		}
		return amount;
	}
}
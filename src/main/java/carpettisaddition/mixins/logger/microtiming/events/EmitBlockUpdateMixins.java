/*
 * This file is part of the Carpet TIS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * Carpet TIS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet TIS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet TIS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */

package carpettisaddition.mixins.logger.microtiming.events;

import carpettisaddition.logging.loggers.microtiming.MicroTimingLoggerManager;
import carpettisaddition.logging.loggers.microtiming.enums.EventType;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

//#if MC >= 12102
//$$ import net.minecraft.world.DefaultRedstoneController;
//#endif

//#if MC >= 11600
//$$ import java.util.Iterator;
//$$ import java.util.Set;
//#else
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
//#endif

public abstract class EmitBlockUpdateMixins
{
	@Mixin(AbstractRedstoneGateBlock.class)
	public static abstract class AbstractRedstoneGateBlockMixin
	{
		@Inject(method = "updateTarget", at = @At("HEAD"))
		private void startEmitBlockUpdate(World world, BlockPos pos, BlockState state, CallbackInfo ci)
		{
			MicroTimingLoggerManager.onEmitBlockUpdate(world, (AbstractRedstoneGateBlock)(Object)this, pos, EventType.ACTION_START, "updateTarget");
		}

		@Inject(method = "updateTarget", at = @At("RETURN"))
		private void endEmitBlockUpdate(World world, BlockPos pos, BlockState state, CallbackInfo ci)
		{
			MicroTimingLoggerManager.onEmitBlockUpdate(world, (AbstractRedstoneGateBlock)(Object)this, pos, EventType.ACTION_END, "updateTarget");
		}
	}

	@Mixin(ObserverBlock.class)
	public static abstract class ObserverBlockMixin
	{
		@Inject(method = "updateNeighbors", at = @At("HEAD"))
		private void startEmitBlockUpdate(World world, BlockPos pos, BlockState state, CallbackInfo ci)
		{
			MicroTimingLoggerManager.onEmitBlockUpdate(world, (ObserverBlock)(Object)this, pos, EventType.ACTION_START, "updateNeighbors");
		}

		@Inject(method = "updateNeighbors", at = @At("RETURN"))
		private void endEmitBlockUpdate(World world, BlockPos pos, BlockState state, CallbackInfo ci)
		{
			MicroTimingLoggerManager.onEmitBlockUpdate(world, (ObserverBlock)(Object)this, pos, EventType.ACTION_END, "updateNeighbors");
		}
	}

	@Mixin(PoweredRailBlock.class)
	public static abstract class PoweredRailBlockMixin
	{
		@Inject(
				method = "updateBlockState",
				at = @At(
						value = "INVOKE",
						shift = At.Shift.AFTER,
						target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
				)
		)
		private void startEmitBlockUpdate(BlockState state, World world, BlockPos pos, Block neighbor, CallbackInfo ci)
		{
			MicroTimingLoggerManager.onEmitBlockUpdate(world, (PoweredRailBlock)(Object)this, pos, EventType.ACTION_START, "updateBlockState");
		}

		@Inject(
				method = "updateBlockState",
				at = @At("RETURN"),
				locals = LocalCapture.CAPTURE_FAILHARD
		)
		private void endEmitBlockUpdate(BlockState state, World world, BlockPos pos, Block neighbor, CallbackInfo ci, boolean bl, boolean bl2)
		{
			if (bl2 != bl)  // vanilla copy, if power state should be changed
			{
				MicroTimingLoggerManager.onEmitBlockUpdate(world, (PoweredRailBlock) (Object) this, pos, EventType.ACTION_END, "updateBlockState");
			}
		}
	}

	@Mixin(LeverBlock.class)
	public static abstract class LeverBlockMixin
	{
		@Inject(method = "updateNeighbors", at = @At("HEAD"))
		private void startEmitBlockUpdate(BlockState state, World world, BlockPos pos, CallbackInfo ci)
		{
			MicroTimingLoggerManager.onEmitBlockUpdate(world, (LeverBlock)(Object)this, pos, EventType.ACTION_START, "updateNeighbors");
		}

		@Inject(method = "updateNeighbors", at = @At("RETURN"))
		private void endEmitBlockUpdate(BlockState state, World world, BlockPos pos, CallbackInfo ci)
		{
			MicroTimingLoggerManager.onEmitBlockUpdate(world, (LeverBlock)(Object)this, pos, EventType.ACTION_END, "updateNeighbors");
		}
	}

	@Mixin(AbstractButtonBlock.class)
	public static abstract class AbstractButtonBlockMixin
	{
		@Inject(method = "updateNeighbors", at = @At("HEAD"))
		private void startEmitBlockUpdate(BlockState state, World world, BlockPos pos, CallbackInfo ci)
		{
			MicroTimingLoggerManager.onEmitBlockUpdate(world, (AbstractButtonBlock)(Object)this, pos, EventType.ACTION_START, "updateNeighbors");
		}

		@Inject(method = "updateNeighbors", at = @At("RETURN"))
		private void endEmitBlockUpdate(BlockState state, World world, BlockPos pos, CallbackInfo ci)
		{
			MicroTimingLoggerManager.onEmitBlockUpdate(world, (AbstractButtonBlock)(Object)this, pos, EventType.ACTION_END, "updateNeighbors");
		}
	}

	@Mixin(AbstractPressurePlateBlock.class)
	public static abstract class AbstractPressurePlateBlockMixin
	{
		@Inject(method = "updateNeighbors", at = @At("HEAD"))
		private void startEmitBlockUpdate(World world, BlockPos pos, CallbackInfo ci)
		{
			MicroTimingLoggerManager.onEmitBlockUpdate(world, (AbstractPressurePlateBlock)(Object)this, pos, EventType.ACTION_START, "updateNeighbors");
		}

		@Inject(method = "updateNeighbors", at = @At("RETURN"))
		private void endEmitBlockUpdate(World world, BlockPos pos, CallbackInfo ci)
		{
			MicroTimingLoggerManager.onEmitBlockUpdate(world, (AbstractPressurePlateBlock)(Object)this, pos, EventType.ACTION_END, "updateNeighbors");
		}
	}

	@Mixin(TripwireHookBlock.class)
	public static abstract class TripwireHookBlockMixin
	{
		@Inject(method = "updateNeighborsOnAxis", at = @At("HEAD"))
		private
		//#if MC >= 12003
		//$$ static
		//#endif
		void startEmitBlockUpdate(
				//#if MC >= 12003
				//$$ Block block,
				//#endif
				World world, BlockPos pos, Direction direction, CallbackInfo ci
		)
		{
			//#if MC < 12003
			Block block = (TripwireHookBlock)(Object)this;
			//#endif
			MicroTimingLoggerManager.onEmitBlockUpdate(world, block, pos, EventType.ACTION_START, "updateNeighborsOnAxis");
		}

		@Inject(method = "updateNeighborsOnAxis", at = @At("RETURN"))
		private
		//#if MC >= 12003
		//$$ static
		//#endif
		void endEmitBlockUpdate(
				//#if MC >= 12003
				//$$ Block block,
				//#endif
				World world, BlockPos pos, Direction direction, CallbackInfo ci
		)
		{
			//#if MC < 12003
			Block block = (TripwireHookBlock)(Object)this;
			//#endif
			MicroTimingLoggerManager.onEmitBlockUpdate(world, block, pos, EventType.ACTION_END, "updateNeighborsOnAxis");
		}
	}

	/**
	 * Use larger priority, so it injects after the @ModifyVariable in
	 * {@link carpettisaddition.mixins.rule.redstoneDustRandomUpdateOrder.RedstoneWireBlockMixin}
	 */
	@Mixin(
			//#if MC >= 12102
			//$$ value = DefaultRedstoneController.class,
			//#else
			value = RedstoneWireBlock.class,
			//#endif
			priority = 2000
	)
	public static abstract class RedstoneWireBlockMixin
	{
		// TODO: support ExperimentalRedstoneController in mc 1.21.2+

		@Inject(
				method = "update",
				at = @At(
						value = "INVOKE",
						//#if MC >= 11600
						//$$ target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"
						//#else
						target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
						//#endif
				)
		)
		private void startEmitBlockUpdate(
				//#if MC >= 11600
				//$$ CallbackInfo ci, @Local Set<BlockPos> collection,
				//#else
				CallbackInfoReturnable<BlockState> cir, @Local List<BlockPos> collection,
				//#endif
				//#if MC >= 12102
				//$$ @Local(argsOnly = true) BlockState state,
				//#endif
				@Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos
		)
		{
			MicroTimingLoggerManager.onEmitBlockUpdateRedstoneDust(
					world,
					//#if MC >= 12102
					//$$ state.getBlock(),
					//#else
					(RedstoneWireBlock)(Object)this,
					//#endif
					pos, EventType.ACTION_START, "update", collection
			);
		}

		//#if MC >= 11600
		//$$ @Inject(
		//$$ 		method = "update",
		//$$ 		at = @At(
		//$$ 				value = "INVOKE",
		//$$ 				target = "Ljava/util/Iterator;hasNext()Z"
		//$$ 		)
		//$$ )
		//$$ private void endEmitBlockUpdate(
		//$$ 		CallbackInfo ci,
		//$$ 		@Local(argsOnly = true) World world,
		//$$ 		@Local(argsOnly = true) BlockPos pos,
		//$$ 		//#if MC >= 12102
		//$$ 		//$$ @Local(argsOnly = true) BlockState state,
		//$$ 		//#endif
		//$$ 		@Local Iterator<BlockPos> iterator
		//$$ )
		//$$ {
		//$$ 	if (!iterator.hasNext())
		//$$ 	{
		//$$ 		MicroTimingLoggerManager.onEmitBlockUpdateRedstoneDust(
		//$$ 				world,
		//$$ 				//#if MC >= 12102
		//$$ 				//$$ state.getBlock(),
		//$$ 				//#else
		//$$ 				(RedstoneWireBlock)(Object)this,
		//$$ 				//#endif
		//$$ 				pos, EventType.ACTION_END, "update", null
		//$$ 		);
		//$$ 	}
		//$$ }
		//#else
		@Inject(method = "update", at = @At("RETURN"))
		private void endEmitBlockUpdate(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<BlockState> cir)
		{
			MicroTimingLoggerManager.onEmitBlockUpdateRedstoneDust(world, (RedstoneWireBlock)(Object)this, pos, EventType.ACTION_END, "update", null);
		}
		//#endif
	}
}

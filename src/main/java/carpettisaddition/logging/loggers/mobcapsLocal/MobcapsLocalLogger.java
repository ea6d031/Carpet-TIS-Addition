package carpettisaddition.logging.loggers.mobcapsLocal;

import carpet.logging.HUDLogger;
import carpet.logging.Logger;
import carpet.utils.SpawnReporter;
import carpettisaddition.CarpetTISAdditionServer;
import carpettisaddition.logging.TISAdditionLoggerRegistry;
import carpettisaddition.logging.loggers.AbstractHUDLogger;
import carpettisaddition.mixins.logger.mobcapsLocal.SpawnDensityCapperAccessor;
import carpettisaddition.mixins.logger.mobcapsLocal.SpawnDensityCapperDensityCapAccessor;
import carpettisaddition.utils.Messenger;
import carpettisaddition.utils.compat.DimensionWrapper;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.SpawnDensityCapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class MobcapsLocalLogger extends AbstractHUDLogger
{
	public static final String NAME = "mobcapsLocal";
	private static final MobcapsLocalLogger INSTANCE = new MobcapsLocalLogger();

	private final Map<DimensionWrapper, SpawnDensityCapper> capperMap = Maps.newHashMap();
	private final ThreadLocal<@Nullable Object2IntMap<SpawnGroup>> mobcapsMap = ThreadLocal.withInitial(() -> null);

	private MobcapsLocalLogger()
	{
		super(NAME);
	}

	public static MobcapsLocalLogger getInstance()
	{
		return INSTANCE;
	}

	public void setCapper(DimensionWrapper dim, SpawnDensityCapper capper)
	{
		this.capperMap.put(dim, capper);
	}

	@Nullable
	public Object2IntMap<SpawnGroup> getMobcapsMap()
	{
		return mobcapsMap.get();
	}

	@Override
	public MutableText[] onHudUpdate(String option, PlayerEntity playerEntity)
	{
		if (option != null)
		{
			ServerPlayerEntity specifiedPlayer = CarpetTISAdditionServer.minecraft_server.getPlayerManager().getPlayer(option);
			if (specifiedPlayer != null)
			{
				playerEntity = specifiedPlayer;
			}
			else
			{
				return new MutableText[]{Messenger.formatting(tr("player_not_found", option), Formatting.GRAY)};
			}
		}
		if (playerEntity instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
			final MutableText result = Messenger.c("g [", Messenger.formatting(tr("local"), "g"), "g ] ");
			this.withLocalMobcapContext(
					serverPlayerEntity,
					() -> {
						List<Text> lines = SpawnReporter.printMobcapsForDimension(serverPlayerEntity.getWorld(), false);
						result.append(lines.get(0));
						if (option != null)
						{
							result.append(Messenger.s(String.format(" (%s)", option), "g"));
						}
					},
					() -> {
						result.append("-- Not available --");
					}
			);
			return new MutableText[]{result};
		}
		return new MutableText[]{Messenger.s("-- Not ServerPlayerEntity --")};
	}

	public void withLocalMobcapContext(ServerPlayerEntity player, Runnable runnable, Runnable failureCallback)
	{
		SpawnDensityCapper capper = this.capperMap.get(DimensionWrapper.of(player));
		if (capper != null)
		{
			SpawnDensityCapper.DensityCap cap = ((SpawnDensityCapperAccessor)capper).getPlayersToDensityCap().getOrDefault(player, SpawnDensityCapperDensityCapAccessor.invokeConstructor());
			this.mobcapsMap.set(((SpawnDensityCapperDensityCapAccessor) cap).getSpawnGroupsToDensity());
			try
			{
				runnable.run();
			}
			finally
			{
				this.mobcapsMap.set(null);
			}
		}
		else
		{
			failureCallback.run();
		}
	}

	public Logger getHUDLogger()
	{
		return new HUDLogger(TISAdditionLoggerRegistry.getLoggerField(NAME), NAME, null, null, false) {
			@Override
			public String[] getOptions()
			{
				return CarpetTISAdditionServer.minecraft_server.getPlayerNames();
			}
		};
	}
}

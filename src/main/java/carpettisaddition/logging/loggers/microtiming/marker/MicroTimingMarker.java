package carpettisaddition.logging.loggers.microtiming.marker;

import carpet.script.utils.ShapeDispatcher;
import carpet.script.value.NumericValue;
import carpettisaddition.helpers.carpet.shape.ShapeHolder;
import carpettisaddition.helpers.carpet.shape.ShapeUtil;
import carpettisaddition.logging.loggers.microtiming.utils.MicroTimingUtil;
import carpettisaddition.mixins.logger.microtiming.marker.DyeColorAccessor;
import carpettisaddition.utils.Messenger;
import carpettisaddition.utils.TextUtil;
import carpettisaddition.utils.compat.DimensionWrapper;
import com.google.common.collect.Lists;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MicroTimingMarker
{
	public static final int MARKER_SYNC_INTERVAL = 5 * 20;  // 5sec
	public static final int MARKER_RENDER_DURATION = 10 * 60 * 20;  // 5min
	// 5min / 5sec = 60, so it should works fine unless the server tps is below 20 / 60 = 0.333

	private final ServerWorld serverWorld;
	private final BlockPos blockPos;
	public final DyeColor color;
	private final ShapeHolder<ShapeDispatcher.Box> box;
	@Nullable
	private final ShapeHolder<ShapeDispatcher.DisplayedText> text;
	@Nullable
	private final MutableText markerName;
	private MicroTimingMarkerType markerType;
	private boolean movable;

	public long tickCounter;

	@SuppressWarnings("ConstantConditions")
	private MicroTimingMarker(ServerWorld serverWorld, BlockPos blockPos, DyeColor color, @Nullable MutableText markerName, MicroTimingMarkerType markerType, boolean movable)
	{
		this.serverWorld = serverWorld;
		this.blockPos = blockPos;
		this.color = color;
		this.markerName = markerName;
		this.markerType = markerType;
		this.movable = movable;
		this.box = ShapeUtil.createBox(Vec3d.of(blockPos), Vec3d.of(blockPos.add(1, 1, 1)), DimensionWrapper.of(serverWorld), ((long)((DyeColorAccessor)(Object)this.color).getTextColor() << 8) | 0xAF);
		if (this.markerName != null)
		{
			MutableText text = Messenger.c(MicroTimingUtil.getColorStyle(this.color) + " # ", Messenger.copy(this.markerName));
			this.text = ShapeUtil.createLabel(text, new Vec3d(blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D), DimensionWrapper.of(serverWorld), null);
		}
		else
		{
			this.text = null;
		}
		this.updateLineWidth();
	}

	public MicroTimingMarker(ServerWorld serverWorld, BlockPos blockPos, DyeColor color, @Nullable MutableText markerName)
	{
		this(serverWorld, blockPos, color, markerName, MicroTimingMarkerType.REGULAR, false);
	}

	public boolean isMovable()
	{
		return this.movable;
	}

	public void setMovable(boolean movable)
	{
		this.movable = movable;
	}

	@Nullable
	public String getMarkerNameString()
	{
		return this.markerName != null ? this.markerName.getString() : null;
	}

	public MicroTimingMarkerType getMarkerType()
	{
		return this.markerType;
	}

	private void updateLineWidth()
	{
		this.box.setValue("line", new NumericValue(this.markerType.getLineWidth()));
	}

	public boolean rollMarkerType()
	{
		boolean hasNext;
		switch (this.markerType)
		{
			case REGULAR:
				this.markerType = MicroTimingMarkerType.END_ROD;
				hasNext = true;
				break;
			case END_ROD:
			default:
				hasNext = false;
				break;
		}
		if (hasNext)
		{
			this.cleanShapeToAll();
			this.updateLineWidth();
			this.sendShapeToAll();
		}
		return hasNext;
	}

	public List<ShapeDispatcher.ShapeWithConfig> getShapeDataList(boolean display)
	{
		List<ShapeDispatcher.ShapeWithConfig> result = Lists.newArrayList();
		result.add(this.box.toPair(display));
		if (this.text != null)
		{
			result.add(this.text.toPair(display));
		}
		return result;
	}

	public void sendShapeToAll()
	{
		ShapeDispatcher.sendShape(MicroTimingUtil.getSubscribedPlayers(), this.getShapeDataList(true));
	}

	public void cleanShapeToAll()
	{
		ShapeDispatcher.sendShape(MicroTimingUtil.getSubscribedPlayers(), this.getShapeDataList(false));
	}

	public StorageKey getStorageKey()
	{
		return new StorageKey(this.serverWorld, this.blockPos);
	}

	/**
	 * Create a copied marker at offset direction
	 */
	public MicroTimingMarker offsetCopy(Direction direction)
	{
		return new MicroTimingMarker(this.serverWorld, this.blockPos.offset(direction), this.color, this.markerName, this.markerType, this.movable);
	}

	// 1.15 client cannot response to text component color, so just use Formatting symbol here
	private MutableText withFormattingSymbol(String text)
	{
		return Messenger.s(text, MicroTimingUtil.getColorStyle(this.color));
	}

	// [1, 2, 3]
	public MutableText toShortText()
	{
		return this.withFormattingSymbol(TextUtil.coord(this.blockPos));
	}

	// [1, 2, 3] red
	public MutableText toFullText()
	{
		return Messenger.c(
				Messenger.s(TextUtil.coord(this.blockPos)),
				this.withFormattingSymbol(" " + this.color.toString())
		);
	}
}

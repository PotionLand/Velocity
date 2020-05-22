package com.velocitypowered.proxy.protocol.packet;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import net.kyori.nbt.CompoundTag;
import org.checkerframework.checker.nullness.qual.Nullable;

public class JoinGame implements MinecraftPacket {

  private int entityId;
  private short gamemode;
  private int dimension;
  private long partialHashedSeed; // 1.15+
  private short difficulty;
  private short maxPlayers;
  private @Nullable String levelType;
  private int viewDistance; //1.14+
  private boolean reducedDebugInfo;
  private boolean showRespawnScreen;
  private boolean shouldKeepPlayerData;
  private boolean isDebug;
  private boolean isFlat;
  private String dimensionRegistryName;
  private CompoundTag dimensionRegistry;

  public int getEntityId() {
    return entityId;
  }

  public void setEntityId(int entityId) {
    this.entityId = entityId;
  }

  public short getGamemode() {
    return gamemode;
  }

  public void setGamemode(short gamemode) {
    this.gamemode = gamemode;
  }

  public int getDimension() {
    return dimension;
  }

  public void setDimension(int dimension) {
    this.dimension = dimension;
  }

  public long getPartialHashedSeed() {
    return partialHashedSeed;
  }

  public short getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(short difficulty) {
    this.difficulty = difficulty;
  }

  public short getMaxPlayers() {
    return maxPlayers;
  }

  public void setMaxPlayers(short maxPlayers) {
    this.maxPlayers = maxPlayers;
  }

  public String getLevelType() {
    if (levelType == null) {
      throw new IllegalStateException("No level type specified.");
    }
    return levelType;
  }

  public void setLevelType(String levelType) {
    this.levelType = levelType;
  }

  public int getViewDistance() {
    return viewDistance;
  }

  public void setViewDistance(int viewDistance) {
    this.viewDistance = viewDistance;
  }

  public boolean isReducedDebugInfo() {
    return reducedDebugInfo;
  }

  public void setReducedDebugInfo(boolean reducedDebugInfo) {
    this.reducedDebugInfo = reducedDebugInfo;
  }

  public boolean getShouldKeepPlayerData() {
    return shouldKeepPlayerData;
  }

  public void setShouldKeepPlayerData(boolean shouldKeepPlayerData) {
    this.shouldKeepPlayerData = shouldKeepPlayerData;
  }

  public boolean getIsDebug() {
    return isDebug;
  }

  public void setIsDebug(boolean isDebug) {
    this.isDebug = isDebug;
  }

  public boolean getIsFlat() {
    return isFlat;
  }

  public void setIsFlat(boolean isFlat) {
    this.isFlat = isFlat;
  }

  public String getDimensionRegistryName() {
    return dimensionRegistryName;
  }

  public void setDimensionRegistryName(String dimensionRegistryName) {
    this.dimensionRegistryName = dimensionRegistryName;
  }

  public CompoundTag getDimensionRegistry() {
    return dimensionRegistry;
  }

  public void setDimensionRegistry(CompoundTag dimensionRegistry) {
    this.dimensionRegistry = dimensionRegistry;
  }

  @Override
  public String toString() {
    return "JoinGame{"
        + "entityId=" + entityId
        + ", gamemode=" + gamemode
        + ", dimension=" + dimension
        + ", partialHashedSeed=" + partialHashedSeed
        + ", difficulty=" + difficulty
        + ", maxPlayers=" + maxPlayers
        + ", levelType='" + levelType + '\''
        + ", viewDistance=" + viewDistance
        + ", reducedDebugInfo=" + reducedDebugInfo
        + '}';
  }

  @Override
  public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    this.entityId = buf.readInt();
    this.gamemode = buf.readUnsignedByte();
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_16) >= 0) {
      this.dimensionRegistry = ProtocolUtils.readCompoundTag(buf);
      this.dimensionRegistryName = ProtocolUtils.readString(buf);
    } else {
      if (version.compareTo(ProtocolVersion.MINECRAFT_1_9_1) >= 0) {
        this.dimension = buf.readInt();
      } else {
        this.dimension = buf.readByte();
      }
    }
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_13_2) <= 0) {
      this.difficulty = buf.readUnsignedByte();
    }
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_15) >= 0) {
      this.partialHashedSeed = buf.readLong();
    }
    this.maxPlayers = buf.readUnsignedByte();
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_16) < 0) {
      this.levelType = ProtocolUtils.readString(buf, 16);
    } else {
      this.levelType = "default"; // I didn't have the courage to rework this yet.
    }
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_14) >= 0) {
      this.viewDistance = ProtocolUtils.readVarInt(buf);
    }
    this.reducedDebugInfo = buf.readBoolean();
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_15) >= 0) {
      this.showRespawnScreen = buf.readBoolean();
    }
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_16) >= 0) {
      isDebug = buf.readBoolean();
      isFlat = buf.readBoolean();
    }
  }

  @Override
  public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    buf.writeInt(entityId);
    buf.writeByte(gamemode);
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_16) >= 0) {
      ProtocolUtils.writeCompoundTag(buf, dimensionRegistry);
      ProtocolUtils.writeString(buf, dimensionRegistryName);
    } else {
      if (version.compareTo(ProtocolVersion.MINECRAFT_1_9_1) >= 0) {
        buf.writeInt(dimension);
      } else {
        buf.writeByte(dimension);
      }
    }
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_13_2) <= 0) {
      buf.writeByte(difficulty);
    }
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_15) >= 0) {
      buf.writeLong(partialHashedSeed);
    }
    buf.writeByte(maxPlayers);
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_16) < 0) {
      if (levelType == null) {
        throw new IllegalStateException("No level type specified.");
      }
      ProtocolUtils.writeString(buf, levelType);
    }
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_14) >= 0) {
      ProtocolUtils.writeVarInt(buf,viewDistance);
    }
    buf.writeBoolean(reducedDebugInfo);
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_15) >= 0) {
      buf.writeBoolean(showRespawnScreen);
    }
    if (version.compareTo(ProtocolVersion.MINECRAFT_1_16) >= 0) {
      buf.writeBoolean(isDebug);
      buf.writeBoolean(isFlat);
    }
  }

  @Override
  public boolean handle(MinecraftSessionHandler handler) {
    return handler.handle(this);
  }
}

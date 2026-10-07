package com.winboss.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * 「微软」Boss —— 四色悬浮方块，半血变身「巨硬」。
 * 微软式中文全程提醒，整活为主，兼具可玩性。
 */
public class MicrosoftBossEntity extends HostileEntity {
    private final ServerBossBar bossBar;
    private int fireCooldown = 0;
    private int updateCooldown = 140;
    private int menuCooldown = 260;
    private int phase2SkillCooldown = 0;
    private boolean phase2 = false;

    public MicrosoftBossEntity(EntityType<? extends MicrosoftBossEntity> type, World world) {
        super(type, world);
        this.bossBar = new ServerBossBar(
            Text.literal("微软"), BossBar.Color.PURPLE, BossBar.Style.PROGRESS);
        this.bossBar.setDarkenSky(true);
        this.bossBar.setThickenFog(true);
        this.setNoGravity(true);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    public boolean isPhase2() { return phase2; }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 12.0F));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            // 客户端粒子随形态变化
            if (this.random.nextInt(4) == 0) {
                this.getWorld().addParticle(this.phase2 ? ParticleTypes.END_ROD : ParticleTypes.FLAME,
                    this.getX() + (this.random.nextDouble() - 0.5) * 1.2,
                    this.getY() + this.random.nextDouble() * 1.6,
                    this.getZ() + (this.random.nextDouble() - 0.5) * 1.2,
                    0, 0.02, 0);
            }
            return;
        }
        tickServer();
    }

    /** 悬浮移动：只在服务端执行 */
    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.getWorld().isClient) {
            LivingEntity target = this.getTarget();
            if (this.phase2 && target != null && target.isAlive()) {
                Vec3d to = target.getPos().add(0, 1.0, 0).subtract(this.getPos()).normalize();
                this.setVelocity(this.getVelocity().add(to.multiply(0.05)));
            }
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.96).add(0, 0.015, 0));
        }
    }

    private void tickServer() {
        ServerWorld world = (ServerWorld) this.getWorld();
        // BossBar 同步
        this.bossBar.setName(Text.literal(phase2 ? "巨硬" : "微软"));
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        this.bossBar.setColor(phase2 ? BossBar.Color.RED : BossBar.Color.PURPLE);

        // 强制更新蓄力中（无敌窗口）
        tickUpdateCharging(world);

        // 半血变身 → 巨硬
        if (!phase2 && this.getHealth() <= this.getMaxHealth() * 0.5f) {
            setPhase2();
        }

        // 阶段技能
        if (phase2) {
            phase2Tick(world);
        } else {
            phase1Tick(world);
        }
    }

    // ---------- 第一形态「微软」 ----------
    private void phase1Tick(ServerWorld world) {
        if (--updateCooldown <= 0) {
            updateCooldown = 240;
            forceUpdate(world);
        }
        if (--fireCooldown <= 0) {
            fireCooldown = phase2 ? 40 : 56;
            shootFireball(this.getTarget(), 1);
        }
        if (--menuCooldown <= 0) {
            menuCooldown = 300;
            openStartMenu(world);
        }
    }

    // ---------- 第二形态「巨硬」 ----------
    private void phase2Tick(ServerWorld world) {
        if (--fireCooldown <= 0) {
            fireCooldown = 32;
            shootFireball(this.getTarget(), 3); // 三连扇射
        }
        if (--updateCooldown <= 0) {
            updateCooldown = 180;
            autoRepair(world);
        }
        if (--phase2SkillCooldown <= 0) {
            phase2SkillCooldown = 200;
            blueScreen(world);
        }
        if (--menuCooldown <= 0) {
            menuCooldown = 360;
            popupAd(world);
        }
        // 数据流粒子
        if (this.getWorld().random.nextInt(3) == 0) {
            this.getWorld().addParticle(ParticleTypes.END_ROD,
                this.getX() + (this.random.nextDouble() - 0.5) * 1.4,
                this.getY() + this.random.nextDouble() * 1.6,
                this.getZ() + (this.random.nextDouble() - 0.5) * 1.4,
                0, 0.02, 0);
        }
    }

    private void shootFireball(LivingEntity target, int count) {
        if (target == null || !target.isAlive()) return;
        Vec3d origin = this.getPos().add(0, 1.0, 0);
        Vec3d aim = target.getPos().add(0, 1.0, 0).subtract(origin).normalize();
        for (int i = 0; i < count; i++) {
            double spread = (i - (count - 1) / 2.0) * 0.25;
            Vec3d vel = aim.rotateY((float) spread).multiply(0.7);
            SmallFireballEntity fb = new SmallFireballEntity(this.getWorld(), this,
                vel.x, vel.y, vel.z);
            fb.setPosition(origin.x, origin.y, origin.z);
            fb.setNoGravity(true);
            this.getWorld().spawnEntity(fb);
        }
        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT,
            this.getSoundCategory(), 0.8f, 1.0f);
    }

    /** 「强制更新」：蓄力 2.5 秒（无敌）→ 爆发圈 */
    private int updateCharging = 0;
    private void forceUpdate(ServerWorld world) {
        sayToAll(world, "【微软】正在安装更新（1%…99%）… 请稍候。");
        world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ANVIL_PLACE,
            this.getSoundCategory(), 1.0f, 0.8f);
        this.updateCharging = 50; // 50 tick = 2.5 秒无敌蓄力
    }

    private void tickUpdateCharging(ServerWorld world) {
        if (updateCharging <= 0) return;
        this.setInvulnerable(true);
        if (this.getWorld().random.nextInt(2) == 0) {
            this.getWorld().addParticle(ParticleTypes.SMOKE,
                this.getX() + (this.random.nextDouble() - 0.5) * 2,
                this.getY() + this.random.nextDouble() * 1.6,
                this.getZ() + (this.random.nextDouble() - 0.5) * 2,
                0, 0.05, 0);
        }
        updateCharging--;
        if (updateCharging <= 0) {
            // 更新完成 → 爆发
            this.setInvulnerable(false);
            sayToAll(world, "【微软】更新完成。");
            List<ServerPlayerEntity> players = world.getPlayers();
            for (PlayerEntity p : players) {
                if (p.distanceTo(this) < 6.0) {
                    p.damage(world.getDamageSources().mobAttack(this), 6.0f);
                    p.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 2));
                }
            }
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE,
                this.getSoundCategory(), 1.0f, 1.0f);
        }
    }

    /** 「开始菜单」：召唤图标小弟（Vex） */
    private void openStartMenu(ServerWorld world) {
        int count = 0;
        for (Entity e : this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(16))) {
            if (e instanceof VexEntity) count++;
        }
        if (count >= 4) return;
        sayToAll(world, "【微软】开始菜单已弹出。");
        for (int i = 0; i < 2; i++) {
            VexEntity vex = net.minecraft.entity.EntityType.VEX.create(this.getWorld());
            if (vex == null) continue;
            vex.refreshPositionAndAngles(
                this.getX() + (this.random.nextDouble() - 0.5) * 3,
                this.getY() + 1.2,
                this.getZ() + (this.random.nextDouble() - 0.5) * 3, 0, 0);
            vex.setCustomName(Text.literal("④ 图标"));
            vex.setCustomNameVisible(true);
            if (this.getTarget() != null) vex.setTarget(this.getTarget());
            this.getWorld().spawnEntity(vex);
        }
    }

    /** 「自动修复」：巨硬专属 */
    private void autoRepair(ServerWorld world) {
        sayToAll(world, "【巨硬】正在自动修复系统问题。请不要中断。");
        this.heal(18.0f);
        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT,
            this.getSoundCategory(), 1.0f, 0.6f);
        for (int i = 0; i < 30; i++) {
            this.getWorld().addParticle(ParticleTypes.COMPOSTER,
                this.getX() + (this.random.nextDouble() - 0.5) * 3,
                this.getY() + 2 + this.random.nextDouble(),
                this.getZ() + (this.random.nextDouble() - 0.5) * 3,
                0, 0.04, 0);
        }
    }

    /** 「蓝屏死机」：全屏 AOE + 挖矿疲劳 */
    private void blueScreen(ServerWorld world) {
        sayToAll(world, "【巨硬】发生了 STOP 错误。正在收集错误信息…");
        world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
            this.getSoundCategory(), 1.0f, 1.2f);
        for (PlayerEntity p : world.getPlayers()) {
            if (p.distanceTo(this) < 10.0) {
                p.damage(world.getDamageSources().mobAttack(this), 8.0f);
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 200, 3));
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 1));
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0));
            }
        }
    }

    /** 「弹窗广告」：致盲 + 飘浮 */
    private void popupAd(ServerWorld world) {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        sayToAll(world, "【巨硬】警告：您的计算机存在风险！点击此处免费修复。");
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 40, 1));
    }

    private void setPhase2() {
        this.phase2 = true;
        this.bossBar.setColor(BossBar.Color.RED);
        sayToAll((ServerWorld) this.getWorld(),
            "【微软】检测到可用更新。正在将『微软』升级为『巨硬』… 请勿关闭计算机。");
        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_WITHER_SPAWN,
            this.getSoundCategory(), 1.0f, 1.0f);
        // 变身冲击波
        List<ServerPlayerEntity> players = ((ServerWorld) this.getWorld()).getPlayers();
        for (PlayerEntity p : players) {
            if (p.distanceTo(this) < 8.0) {
                p.damage(this.getWorld().getDamageSources().mobAttack(this), 10.0f);
            }
        }
        // 视觉：变大粒子
        for (int i = 0; i < 60; i++) {
            this.getWorld().addParticle(ParticleTypes.EXPLOSION_EMITTER,
                this.getX(), this.getY() + 0.8, this.getZ(), 0, 0.08, 0);
        }
    }

    private static void sayToAll(ServerWorld world, String msg) {
        for (ServerPlayerEntity p : world.getPlayers()) {
            p.sendMessage(Text.literal(msg), false);
        }
    }

    // ---------- 移动 / 形态 ----------
    @Override
    public boolean isPushedByFluids() { return false; }

    @Override
    public void takeKnockback(double strength, double x, double z) {} // 免疫击退

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean dmg = super.damage(source, amount);
        if (dmg && this.getWorld() instanceof ServerWorld sw) {
            sayToAll(sw, "【" + (phase2 ? "巨硬" : "微软") + "】系统正在响应您的操作…");
        }
        return dmg;
    }

    @Override
    public void onDeath(DamageSource source) {
        sayToAll((ServerWorld) this.getWorld(), "【" + (phase2 ? "巨硬" : "微软") + "】系统已崩溃。正在启动恢复模式…");
        super.onDeath(source);
    }

    // ---------- BossBar 挂接 ----------
    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Phase2", phase2);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.phase2 = nbt.getBoolean("Phase2");
    }

    // 掉落
    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        ServerWorld world = (ServerWorld) this.getWorld();
        int medals = phase2 ? 3 : 1;
        this.dropStack(world, new ItemStack(com.winboss.ModItems.HARD_MEDAL, medals));
        if (phase2) {
            this.dropStack(world, new ItemStack(com.winboss.ModItems.MICROSOFT_SWORD));
            this.dropStack(world, new ItemStack(com.winboss.ModItems.WINDOWS_RELIC));
        }
    }

    private void dropStack(ServerWorld world, ItemStack stack) {
        net.minecraft.entity.ItemEntity entity = new net.minecraft.entity.ItemEntity(world,
            this.getX(), this.getY() + 0.5, this.getZ(), stack);
        world.spawnEntity(entity);
    }
}

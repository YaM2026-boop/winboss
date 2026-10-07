<div align="center">

# WinBoss

**把 Windows 的每一次崩溃，做成 Boss 的一招。**

用四色火把摆出 Windows 徽标的四个方块，召唤 Boss「微软」。
打掉它一半血，它会说 ——「*正在将『微软』升级为『巨硬』… 请勿关闭计算机。*」

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-3C8527?style=flat-square)
![Fabric](https://img.shields.io/badge/Fabric-0.15.11+-DBB69B?style=flat-square)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

</div>

---

## 这是什么

一个 Minecraft **整活 Boss 模组**。

它把每个 Windows 用户的共同创伤，逐条做成战斗机制 ——

| 现实里 | 游戏里 |
|---|---|
| 自动更新总在你干活时弹出来 | Boss 进入**无敌蓄力**，你只能干等着看它"更新到 99%" |
| 更新完了还要重启 | 更新完成的瞬间，脚下炸开一圈伤害 |
| 蓝屏锁死整台电脑 | 全屏 AOE + 挖掘疲劳 III + 缓慢 + 反胃 |
| 弹窗广告糊你一脸 | 失明 + 把你**吸上天** |
| 系统"自动修复"越修越坏 | 它会**回血 18 点**，你得抢在它修完前打死 |
| 桌面堆满图标 | 召唤一群叫「④ 图标」的小飞怪咬你 |

血条掉到一半，它会**升级**成第二形态 —— **巨硬**。

## 怎么召唤

用四色火把在地面摆一个 2×2：

| | |
|:---:|:---:|
| 红石火把 | 铜火把 |
| 灵魂火把 | 火把 |

摆好后**右键任意一把火把** —— 四把火把消失，Boss「微软」出现在正上方，
伴随信标激活音、雷声和爆炸粒子，聊天框提示：

> `Microsoft 微软 正在启动…`

> **配色不强制位置顺序**（宽容匹配），但必须是**四种不同颜色各一把**。
> 少一把、或者有重复颜色，右键都不会触发，也不会消耗火把。

## Boss 技能表

### 第一形态 · 微软（300 HP，紫色血条，压暗天空）

| 技能 | 冷却 | 台词 | 效果 |
|---|---:|---|---|
| 火球 | 2.8s | — | 追踪玩家发射火球 |
| **强制更新** | 12s | `正在安装更新（1%…99%）… 请稍候。` | **无敌蓄力 2.5 秒** → 爆炸，6 格内 6 点伤害 + 缓慢 II |
| **开始菜单** | 15s | `开始菜单已弹出。` | 召唤 2 只「④ 图标」小飞怪，场上上限 4 只 |

> 「强制更新」期间它**免疫一切伤害**。这个设计是故意的 —— 你必须站着等它更新完，
> 就像现实中那样。

### 半血变身 · 巨硬

> `【微软】检测到可用更新。正在将『微软』升级为『巨硬』… 请勿关闭计算机。`

血条变红、名字变「巨硬」、8 格内造成 10 点冲击波伤害、凋灵生成音。

### 第二形态 · 巨硬（血条变红，开始主动追击）

| 技能 | 冷却 | 台词 | 效果 |
|---|---:|---|---|
| 三连火球 | 1.6s | — | 扇形三发齐射 |
| **自动修复** | 9s | `正在自动修复系统问题。请不要中断。` | **回复 18 点生命** |
| **蓝屏死机** | 10s | `发生了 STOP 错误。正在收集错误信息…` | 10 格内 8 点伤害 + 挖掘疲劳 III + 缓慢 + 反胃 |
| **弹窗广告** | 18s | `警告：您的计算机存在风险！点击此处免费修复。` | 失明 + **飘浮 II**（把你吸起来） |
| 追击 | — | — | 主动飞向玩家 |

### 其他

- 每次被攻击 → `系统正在响应您的操作…`
- 被击杀 → `系统已崩溃。正在启动恢复模式…`
- **免疫击退**、不受流体推动、悬空飞行
- **两阶段状态写入 NBT** —— 存档退出重进，它还是「巨硬」

## 掉落

| 击杀形态 | 掉落 |
|---|---|
| 微软 | 硬核徽章 ×1 |
| 巨硬 | 硬核徽章 ×3 + 微软剑 + 微软遗物 |

## 新增内容

**方块**

- **铜火把** —— 召唤仪式用的第四种颜色
- **微软遗物** —— 巨硬形态掉落

**物品**

- **微软剑** —— 巨硬的战利品
- **硬核徽章** —— 击杀凭证
- **微软刷怪蛋** / **旺牛刷怪蛋** —— 不想摆火把就直接刷

## 安装

1. 安装 [Fabric Loader](https://fabricmc.net/use/installer/) 0.15.11 或更高
2. 把 [Fabric API](https://modrinth.com/mod/fabric-api) 放进 `mods/` 文件夹
3. 把 `winboss-x.x.x.jar` 放进 `.minecraft/mods/`
4. 启动游戏

> 目前**没有预编译的 jar**，请按下面的方式自行构建（或等待 Release）。

## 从源码构建

需要 **JDK 17**。

```bash
git clone https://github.com/YaM2026-boop/winboss.git
cd winboss
./gradlew build          # Windows: gradlew.bat build
```

产物在 `build/libs/winboss-<version>.jar`。

在 IDE 里跑：`./gradlew runClient`。

## 项目结构

```
src/main/java/com/winboss/
├── WinBossMod.java              模组入口
├── ModItems.java / ModBlocks.java / ModEntities.java
├── TorchSummon.java             ★ 四色火把召唤仪式
├── item/WinSwordItem.java       微软剑
├── entity/
│   ├── MicrosoftBossEntity.java ★ Boss 本体（两形态 + 全部技能，377 行）
│   └── WinCowEntity.java        旺牛
└── client/
    ├── WinBossClient.java
    └── MicrosoftBossRenderer.java
```

## 兼容性

| 项 | 版本 |
|---|---|
| Minecraft | 1.20.1 |
| Fabric Loader | ≥ 0.15.11 |
| Fabric API | 0.92.2+1.20.1（其他 1.20.1 版本一般也行） |
| Java | 17 |

- 全部内容为**新增**，不修改任何原版物品、方块或实体
- 客户端/服务端均可用（`environment: "*"`）

## 免责声明

本项目是**恶搞作品（parody）**，与 Microsoft Corporation **无任何关联**，
未获得其授权、认可或赞助。「Microsoft」「Windows」是 Microsoft Corporation 的商标，
此处仅用于**评论与戏仿**目的。

## 许可

[MIT](LICENSE)

# WATER BUCKET. RELEASE! — backport para Minecraft 1.12.2 (Forge)

Solte um balde de água **agachado e no ar** (tecla Q) para arremessá-lo como projétil
super-rápido que vira um bloco de água no impacto.

Baseado no datapack/mod **WATER BUCKET. RELEEEEEASE!** de **theexiiedfeiiow_**
(https://www.curseforge.com/minecraft/mc-mods/water-bucket-release). Esta é uma
reimplementação em Java a partir do comportamento observado nas funções do datapack;
nenhum código foi copiado.
> Licença: a página do CurseForge diz MIT, mas os metadados do .jar dizem "All Rights Reserved".
> Se for publicar, confirme com o autor e mantenha os créditos.

## Compilar
1. Baixe o **MDK do Forge 1.12.2 (14.23.5.2860)** e copie `gradlew`, `gradlew.bat` e a pasta `gradle/` para esta pasta.
2. Use **Java 8** (JDK 8) e rode:
   - `./gradlew setupDecompWorkspace` (primeira vez)
   - `./gradlew build` → o jar sai em `build/libs/`
   - `./gradlew runClient` para testar
3. Importe no IntelliJ/Eclipse: `./gradlew idea` / `./gradlew eclipse`.

## Estrutura
- `WaterBucketRelease` — classe principal, registro da entidade
- `ReleaseHandler` — `ItemTossEvent` (gatilho: balde de água, agachado, `!onGround`)
- `EntityWaterShot` — projétil (`EntityThrowable`, 3,02 blocos/tick) + colocação da água
- `RenderWaterShot` — visual azul translúcido alongado (aproxima os block_display do original)

Antes de publicar, troque o pacote `com.example.waterbucketrelease` e o `SEU_NOME` no `mcmod.info`.

## Compilar sem instalar nada (GitHub Actions)
Suba esta pasta para um repositório no GitHub (conta gratuita basta). O workflow em
`.github/workflows/build.yml` compila com JDK 8 + Gradle 4.9 e deixa o jar em
**Actions → (último run) → Artifacts → waterbucketrelease-jar**. (Não testado.)

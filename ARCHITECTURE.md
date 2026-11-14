# Architecture - BOTC-Plugins

## 1. Vue d'ensemble & Point d'entrée
- **Classe principale** : `Botc.java` (étend `JavaPlugin`).
- **Dépendances externes** : Simple Voice Chat (via `BotcVoicechatPlugin.java`).
- **Cycle d'initialisation (`onEnable`)** : Chargement de `config.yml`, initialisation de `BotcCommandManager`, enregistrement des écouteurs d'événements, configuration de l'équipe scoreboard `botc_night` (visibilité des nametags) et enregistrement du plugin Voice Chat.

## 2. Inventaire des classes par domaine
- **Commandes (`fr.cesi.botc.commands.impl`)** :
  - `AddChairCommand` / `AddRoomCommand` / `DelChairsCommand` / `DelRoomsCommand` / `ShowChairsCommand` / `ShowRoomsCommand` : Gestion des positions physiques des chaises et salles dans l'arène.
  - `AssisCommand` / `DeboutCommand` : Installation/libération des joueurs sur des chevaux invisibles avec groupe vocal.
  - `JourCommand` / `NuitCommand` / `ConseilCommand` / `TempsLibreCommand` : Pilotage des phases de jeu, ambiance lumineuse, boussole GPS (particules) et têtes de joueurs.
  - `AdminCommand` / `HelpCommand` / `ResetCommand` / `PresetCommand` : Commandes utilitaires et de configuration.
  - `MortCommand` / `SetPlayerDeathCommand` / `ExecuteCommand` : Gestion de la mort, des votes fantômes et de l'exécution (foudre).
  - `SilenceCommand` / `ParoleAllCommand` / `GrantParoleCommand` / `VcMuteCommand` / `VcUnmuteCommand` : Gestion de la parole et des mutes vocaux.
  - `MapVoteCommand` / `VoteOuiCommand` : Systèmes de vote annexes.
- **Moteur de jeu & États** :
  - `Botc.java` : Singleton central, stockage global des états de jeu, des joueurs et des gestionnaires.
  - `BotcPlayer.java` : Modèle de données d'un joueur (rôle réel/affiché, statut vivant/mort, index de chaise, vote fantôme).
  - `VoteManager.java` : Gestionnaire des phases de nomination et de vote en conseil.
- **Placement & Presets** :
  - `PresetView.java` / `PresetListener.java` : Interfaces et gestion des presets de configuration de l'arène.
  - `SetTribunalCommand` / `SetLightningCommand` : Définition des points clés du tribunal et des effets.
- **Effets & Environnement personnalisable** :
  - `BotcVoicechatPlugin.java` : Intégration Simple Voice Chat (interception des paquets micro pour le mute global/individuel).
  - Événements de foudre, particules GPS (`Particle` API) et gestion des équipes scoreboard (`botc_night`).
- **Listeners & Persistance** :
  - `PlayerJoinListener.java` : Distribution des items de départ (Grimoire, Registre) et reconnexion/re-siége automatique.
  - `GrimoireView.java` / `GrimoireListener.java` : GUI du Conteur (grimoire des joueurs).
  - `ActionMenuView.java` / `ActionMenuListener.java` : GUI d'action individuel sur un joueur (rôle, mort, vote).
  - `ConteurMenuView.java` / `RoleSelectionView.java` : Interfaces de configuration et rôles.
  - `ItemInteractListener.java` / `GameChatListener.java` / `ChatBlockerListener.java` : Contrôle des interactions et du chat pendant les phases.
  - Stockage YAML persistant via `config.yml`.

## 3. Flux d'interactions critiques
- **Cycle de jeu** :
  `/nuit` -> `Botc.java` change d'état -> `NuitCommand` masque les nametags (`botc_night`), place les têtes dans les salles assignées et lance les particules GPS.
  `/jour` -> `JourCommand` lève les effets de nuit, active la boussole GPS vers le tribunal.
  `/conseil` -> `ConseilCommand` rassemble les joueurs au tribunal, ouvre les nominations (`VoteManager`).
- **Gestion de l'espace** :
  `AddChairCommand` / `AddRoomCommand` -> Enregistrement des coordonnées dans `config.yml` sous le preset actif.
  `/botc assis` (`AssisCommand`) -> Lecture des chaises -> TP des joueurs sur chevaux invisibles -> Création du groupe vocal Voice Chat.
- **Déclenchement d'effets** :
  Mort d'un joueur (`ExecuteCommand` ou `/mort`) -> `Botc.java` met à jour `isAlive = false` -> Foudre (`World.strikeLightning`) -> Activation du vote fantôme.

## 4. Données partagées & Stockage
- **En mémoire (`Botc.java`)** :
  - `playersMap` (`UUID -> BotcPlayer`)
  - `chairs` / `rooms` (Listes de localisations)
  - `voteManager` (`VoteManager`)
- **Stockage persistant** :
  - `config.yml` : Sauvegarde des presets d'arène (`presets.<name>.*`).

## 5. Points de vigilance (Audit rapide)
1. **Couplage fort** : Forte dépendance au singleton `Botc.getInstance()` dans les commandes et écouteurs.
2. **Duplication de code** : Logique de guidage par particules GPS répétée dans les commandes de phase (`Jour`, `Nuit`, `Conseil`).
3. **Synchronisation d'état** : Risque de désynchronisation entre les entités physiques (chevaux/chaises) et la liste logique `BotcPlayer` lors de déconnexions.
4. **Gestion des threads** : Utilisation de tâches asynchrones/synchrones pour les animations nécessitant une attention sur la concurrence.
5. **Complexité des GUIs** : Multiplicité des inventaires interactifs (`Grimoire`, `ActionMenu`) gérant des états modifiables en direct.

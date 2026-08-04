# 🩸 Blood on the Clocktower - Plugin Minecraft (BOTC)

![Modrinth Version](https://img.shields.io/badge/Modrinth-v2.0--stable-green?style=for-the-badge&logo=modrinth)
![PaperMC](https://img.shields.io/badge/PaperMC-1.20%20%7C%201.21-blue?style=for-the-badge&logo=papermc)
![Java Version](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)

Un plugin Minecraft complet et immersif pour jouer à **Blood on the Clocktower (BOTC)**, développé sur **PaperMC**. Conçu sur-mesure pour simplifier drastiquement la tâche du **Maître du Jeu (Conteur)** grâce à un système complet d'interfaces graphiques (GUI), d'automatismes jour/nuit et d'intégrations vocales avancées.

> 🛠️ **Développeurs** : [Neytabi](https://github.com/Neytabi) (Lead Developer) & [Pill0N_](https://github.com/Pill0N_) (Contributor)  
> 📜 **Licence** : Open-Source sous licence MIT

---

## 📌 Sommaire
- [📋 Prérequis & Versions](#-prérequis--versions)
- [🔊 Intégration Vocale (Simple Voice Chat & Sound Physics)](#-intégration-vocale-simple-voice-chat--sound-physics-remastered)
- [📖 Le Système de Grimoires & Registres (Interactivity Zero-Commandes)](#-le-système-de-grimoires--registres)
- [✨ Fonctionnalités Majeures](#-fonctionnalités-majeures)
- [🗺️ Support Multi-Map & Presets de Jeu](#-support-multi-map--presets-de-jeu)
- [⌨️ Guide des Commandes](#️-guide-des-commandes)
- [⚙️ Installation & Premier Démarrage](#️-installation--premier-démarrage)
- [🤝 Open Source & Contributions](#-open-source--contributions)

---

## 📋 Prérequis & Versions

| Élément | Version / Requis | Note |
| :--- | :--- | :--- |
| **Serveur Minecraft** | **PaperMC** `1.20.x` à `1.21.x` (Testé en `1.21.1`) | Spigot/CraftBukkit non recommandés (nécessite l'API Paper / Adventure) |
| **Java** | **Java 21** | Nécessaire pour la compilation et l'exécution du serveur |
| **Plugin Vocale (Serveur)** | **[Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat)** `v2.5.0+` | **Requis** pour la modération vocale, les salons automatiques et les mutes |
| **Mod Audio (Clients)** | **[Sound Physics Remastered](https://modrinth.com/mod/sound-physics-remastered)** | **FORTEMENT RECOMMANDÉ** (Bloque la traversée du son à travers les murs) |

---

## 🔊 Intégration Vocale (Simple Voice Chat & Sound Physics Remastered)

### 🎙️ Intégration Native avec Simple Voice Chat API
Le plugin intercepte directement les paquets audio au niveau du réseau (`MicrophonePacketEvent`) grâce à l'API de Simple Voice Chat :
- **Mute Global de Silence** (`/botc silence`) : Bloque l'émission du micro de tous les villageois instantanément lors des annonces du Conteur.
- **Gestion des Morts & Fantômes** : Mute/Unmute à la volée.
- **Salons de Tribunal Automatiques** : Lors de la phase assis (`/botc assis`), les joueurs sont automatiquement regroupés dans un canal vocal dédié tout en préservant le contrôle du Conteur.
- **Accès Prioritaire MJ (OP)** : Les Conteurs (OPs) contournent systématiquement les mutes et conservent une voix prioritaire.
- **Résilience aux Reloads (Fix PlugMan)** : Le plugin intègre un mécanisme de reconnexion par réflexion pour restaurer l'API vocale en cas de rechargement à chaud.

### 🔇 Pourquoi installer *Sound Physics Remastered* côté Client ?
Dans Blood on the Clocktower, la nuit est une phase cruciale où chaque joueur rejoint sa chambre individuelle. En vanilla avec Simple Voice Chat, la voix de proximité calcule les distances en ligne droite, ce qui signifie que **les joueurs peuvent s'entendre chuchoter à travers les cloisons et murs des chambres**.

> ⚠️ **IMPORTANT : La nécessité de Sound Physics Remastered**  
> En demandant aux joueurs d'installer le mod client **[Sound Physics Remastered](https://modrinth.com/mod/sound-physics-remastered)** (Fabric/NeoForge/Forge) :
> 1. Le son est calculé selon la géométrie des blocs et l'épaisseur des murs.
> 2. Les murs absorbent et bloquent l'atténuation sonore : **aucun chuchotement ne traverse les portes et cloisons fermées des chambres**.
> 3. L'ambiance acoustique devient totalement réaliste et garantit le secret absolu des réunions nocturnes et chuchotements.

---

## 📖 Le Système de Grimoires & Registres

Pour que la partie soit fluide, **ni le Conteur ni les Joueurs n'ont besoin de retenir des commandes complexes**. Tout est pilotable via des objets interactifs dans l'inventaire.

```
       CONTEUR (MJ)                                   JOUEURS
 🔮 Le Grimoire du Conteur                     📕 Registre du Tribunal
  ├── Têtes de tous les joueurs                 ├── Lever la main (Vote OUI)
  ├── Statuts (Vivant / Mort / Jetons)          ├── Nommer un suspect (Accusation)
  ├── Clic : Menu d'actions (Rôles/Exec)        ├── Rappel du Rôle Secret
  ├── Shift+Clic : TP sur le joueur             ├── Poser une question privée au MJ
  └── Outils : Flash-TP, Nametags, Setup        └── Demander la parole (Fantômes)
```

### 🔮 Pour le Conteur (MJ) : *Le Grimoire du Conteur*
À sa connexion ou lors d'un réarmement (`/botc reset`), le MJ reçoit un **Livre Enchanté** nommé **"📖 Le Grimoire du Conteur"**.
En faisant un clic droit avec le livre en main :
1. **Menu Principal GUI** : Accès en 1 clic au cycle Jour/Nuit, annonces des morts, rassemblement sur chaises, modération vocale, visibilité des pseudos et lancement de votes de map.
2. **Vue du Grimoire (`GrimoireView`)** : Un double coffre interactif affichant la tête de chaque joueur (dans l'ordre exact de leur placement autour de la table de jeu).
   - **Clic Gauche** : Ouvre le sous-menu d'action (`ActionMenuView`) pour assigner un rôle secret, tuer/ressusciter le joueur ou lui retirer/rendre son jeton de vote fantôme.
   - **Shift + Clic** : Téléporte instantanément le Conteur derrière le joueur sélectionné.
   - **Perle de l'Ender (Slot 45)** : Flash-TP le Conteur au centre du Tribunal.
   - **Nametag (Slot 46)** : Alterne l'anonymat/la visibilité des pseudos au-dessus des têtes.

### 📕 Pour les Joueurs : *Le Registre du Tribunal & Le Livre des Rôles*
Les joueurs reçoivent deux livres à leur arrivée :
1. **"Livre des rôles" (Written Book)** : Le résumé complet de tous les rôles du scénario (Demons, Sbires, Citadins, Étrangers) ainsi que l'ordre officiel de réveil pour la Première Nuit et les Nuits suivantes.
2. **"Registre du Tribunal" (Book)** : Une interface interactive à 9 slots (`PlayerRegistryView`) :
   - 🟢 **Lever la main (Voter OUI)** : Enregistre le vote sur l'accusation en cours (gère automatiquement la consommation de l'unique jeton de vote de fantôme).
   - 📜 **Nommer un suspect** : Ouvre un registre (`NominationView`) répertoriant les joueurs vivants pour lancer une accusation publique (inclut la gestion automatique des pouvoirs de la **Vierge** et de l'**Ivrogne**).
   - 📖 **Rappel du Rôle Secret** : Affiche le rôle attribué par le MJ et sa description (affiche le faux rôle si le joueur est l'Ivrogne !).
   - 🪶 **Poser une question** : Permet au joueur de taper une question dans le chat qui sera transmise **exclusivement en privé au Conteur**.
   - 📄 **Demander la parole** : Permet aux fantômes d'envoyer une alerte cliquable `[ACCORDER]` aux Conteurs pour prendre la parole au Conseil.

---

## ✨ Fonctionnalités Majeures

- 🌗 **Cycle Jour / Nuit Animé & Guidé** :
  - Transition fluide du ciel (mouvement du soleil accéléré à 60 FPS avec battements de tambour atmosphériques).
  - **Boussole / GPS 3D Particulaire** : À la tombée de la nuit, chaque joueur est guidé par une flèche de particules lumineuses vers sa chambre attribuée. Au lever du jour, la flèche guide tout le monde vers le Tribunal.
  - **Système de Têtes sur Portes** : La nuit, la tête de chaque joueur apparaît automatiquement sur la porte de sa chambre.
- 🪑 **Tribunal & Chaises Invisibles Securisées** :
  - Placement automatique des joueurs sur des chaises invisibles (`botc_chair`) ordonnées autour du cercle de discussion.
  - **Verrou anti-démontage** : Empêche les joueurs de se lever pendant les débats officiels.
  - **Reconnexion Sécurisée** : Si un joueur plante ou se déconnecte, il est automatiquement remis sur son siège dès sa re-connexion.
- 👑 **Système de Rôles & BossBar Réelle vs Fictive** :
  - Attribution des rôles en 1 clic via des menus thématiques (Laine Bleue = Citadins, Jaune = Étrangers, Orange = Sbires, Rouge = Démons).
  - **Gestion de l'Ivrogne (Drunk)** : Le Conteur peut attribuer un rôle en Clic Droit pour créer un Ivrogne (le Conteur voit son vrai rôle "Ivrogne" dans le Grimoire, mais le joueur voit un vrai rôle de Citadin sur son écran et dans sa BossBar !).
  - **Transfert Automatique de la Femme Écarlate** : Si le Diablotin meurt au vote alors qu'il reste 5 vivants ou plus, la Femme Écarlate hérite automatiquement du rôle de Diablotin avec alerte privée.
  - **BossBar Permanente** : Affichage d'un bandeau violet/or discret en haut de l'écran rappelant le rôle secret de chaque joueur.
- ⚡ **Animations d'Exécution & Éclair** :
  - Morts théâtrales avec sons de Wither, particules de fumée noire et tête avec casque Barrière.
  - Mode d'éclair configurable (`player` pour frapper le joueur, ou `local` pour frapper l'estrade d'exécution).
- 🏷️ **Gestion de l'Anonymat (Nametags)** :
  - Masquage global des pseudos via les équipes de Scoreboard pour empêcher de repérer des joueurs à travers les murs la nuit.

---

## 🗺️ Support Multi-Map & Presets de Jeu

Le plugin supporte un nombre illimité de cartes et d'arènes de jeu sauvegardées indépendamment dans la configuration.

- **Création Facile** : Créez des presets (`/botc preset create <nom>`) directement depuis le jeu ou via le chat interactif (Enclume du menu).
- **Sauvegarde Indépendante** : Chaque preset conserve ses propres positions de chaises, de chambres, du tribunal et de l'estrade d'exécution.
- **🗳️ Scrutin & Élection de Map (`/botc mapvote`)** :
  - Le Conteur peut lancer un vote interactif de 30 secondes dans le chat (`/botc mapvote start`).
  - Les joueurs cliquent sur l'arène de leur choix.
  - À la fin du décompte sonore, la map ayant récolté le plus de voix est automatiquement chargée par le plugin !

---

## ⌨️ Guide des Commandes

Toutes les commandes s'articulent autour de la commande racine `/botc` :

### 🧙‍♂️ Commandes Conteur (OP)

| Commande | Description |
| :--- | :--- |
| `/botc setup` / `/botc admin` | Ouvre le menu d'administration et de configuration principal |
| `/botc start` / `/botc reset` | Réinitialise la partie (ressuscite les joueurs, donne les livres, réinitialise les rôles et jetons) |
| `/botc jour` | Déclenche le matin (animation céleste + boussole vers le Tribunal) |
| `/botc nuit` | Déclenche la nuit (animation céleste + boussole vers les chambres + têtes sur portes) |
| `/botc tempslibre` | Libère les mouvements et la parole des joueurs |
| `/botc conseil` | Affiche la boussole vers la salle du conseil |
| `/botc assis` | Assied tous les joueurs sur leur siège attribué et crée le groupe vocal |
| `/botc debout` | Libère les joueurs de leurs sièges |
| `/botc silence` | Mute les micros de tous les villageois via l'API SVC |
| `/botc paroleall` | Rend la parole vocale à tous les joueurs |
| `/botc grantparole <joueur>` | Accorde la parole à un joueur spécifique |
| `/botc vcmute <joueur>` | Mute un joueur spécifique dans le vocal |
| `/botc vcunmute <joueur>` | Unmute un joueur spécifique dans le vocal |
| `/botc execute <joueur>` | Exécute officiellement un condamné (animations + estrade) |
| `/botc mort` | Annonce la liste des morts de la journée avec effet sonore |
| `/botc hidenames` | Cache les pseudos (Nametags) au-dessus des têtes |
| `/botc shownames` | Réaffiche les pseudos (Nametags) |
| `/botc mapvote start` | Lancer une élection de map de 30s dans le chat |
| `/botc mapvote stop` | Clôturer immédiatement l'élection de map et charger la gagnante |
| `/botc preset <select/create/delete/list>` | Gestion des presets de maps |
| `/botc settribunal` | Définit votre position actuelle comme le centre du Tribunal |
| `/botc addchair` | Enregistre une nouvelle chaise à votre position |
| `/botc delchairs` | Supprime toutes les chaises du preset actif |
| `/botc showchairs` | Affiche les chaises du preset actif avec des particules |
| `/botc addroom` | Enregistre une porte de chambre à votre position |
| `/botc delrooms` | Supprime toutes les chambres du preset actif |
| `/botc showrooms` | Affiche les chambres du preset actif avec des particules |
| `/botc setplayerdeath` | Enregistre votre position comme estrade d'exécution |
| `/botc setlightning <player\|local>` | Définit le mode d'éclair d'exécution |

### 🧑‍🌾 Commandes Joueurs (Tous)

| Commande | Description |
| :--- | :--- |
| `/botc help` | Affiche l'aide en jeu |
| `/botc voteoui` | Lève la main pour voter OUI à l'accusation en cours |
| `/botc mapvote choose <map>` | Vote pour une carte lors d'un scrutin ouvert |

---

## ⚙️ Installation & Premier Démarrage

1. Téléchargez le fichier `botc-plugin-2.0-stable.jar`.
2. Déposez le fichier `.jar` dans le dossier `plugins/` de votre serveur **PaperMC** (1.20 ou 1.21).
3. Assurez-vous d'avoir installé **[Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat)** dans le dossier `plugins/`.
4. Démarrez votre serveur. Le fichier de configuration `plugins/BotcPlugin/config.yml` sera généré automatiquement.
5. *(Optionnel mais fortement recommandé)* Incitez vos joueurs à installer le mod **[Sound Physics Remastered](https://modrinth.com/mod/sound-physics-remastered)** sur leur client Fabric/Forge pour un blocage parfait du son à travers les murs.

---

## 🤝 Open Source & Contributions

Ce projet est **100% open-source** distribué sous la licence **MIT**.  
Vous êtes libres de l'utiliser, de le modifier ou de contribuer au code source !

- 📦 **Code Source GitHub** : [https://github.com/Neytabi/BOTC-Plugin](https://github.com/Neytabi/BOTC-Plugin)
- 🐛 **Signaler un bug / Proposer une idée** : [GitHub Issues](https://github.com/Neytabi/BOTC-Plugin/issues)
- 🤝 **Pull Requests** : Les contributions sont les bienvenues !

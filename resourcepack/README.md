# Resource Pack - Blood on the Clocktower (BOTC)

Ce dossier contient la structure prête à l'emploi du Resource Pack pour Minecraft 1.21.1 (Paper).

## 1. Fonctionnalités
- **UI Grimoire Sans Coffre** : Utilise les glyphes Unicode (`\uE001`) combinés aux espaces négatifs (`\uF801` ... `\uF80D`) pour superposer une texture de livre ancien et de cuir par-dessus la fenêtre marron standard du coffre.
- **Jetons BOTC Officiels** : Remplacement des laines et objets vanilla par des jetons ronds en haute définition via `CustomModelData`.

## 2. Arborescence des assets
```
resourcepack/
├── pack.mcmeta
├── pack.png
└── assets/
    ├── minecraft/
    │   └── font/
    │       └── default.json             # Déclaration des glyphes et décalages d'espace négatifs
    └── botc/
        ├── textures/
        │   ├── gui/
        │   │   ├── grimoire_bg.png      # Fond plein écran du Grimoire du Conteur
        │   │   └── action_menu_bg.png   # Fond du sous-menu d'action joueur
        │   └── item/
        │       ├── tokens/              # Jetons des rôles (voyante, empathe, diablotin...)
        │       └── icons/               # Icônes de modération vocale et de cloche
        └── models/
            └── item/                    # Modèles JSON assignés aux CustomModelData
```

## 3. Déploiement sur le serveur
Pour appliquer automatiquement le pack aux joueurs :
1. Compresser le contenu du dossier `resourcepack` en un fichier `.zip`.
2. L'héberger sur un lien direct (ex: MCPacks, Dropbox en `dl=1` ou serveur web).
3. Renseigner l'URL dans `server.properties` (`resource-pack=...`) ou le distribuer via `player.setResourcePack(...)`.


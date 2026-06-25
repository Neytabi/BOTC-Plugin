# BOTC-Plugin

[![Modrinth](https://img.shields.io/badge/Modrinth-BOTC--Plugins-green)](https://modrinth.com)
[![CurseForge](https://img.shields.io/badge/CurseForge-BOTC--Plugins-orange)](https://curseforge.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Plugin Minecraft (Paper 1.20/1.21) pour simplifier la vie du Maître du Jeu (Conteur) et améliorer l'expérience utilisateur dans les parties de **Blood on the Clocktower (BOTC)**, avec une installation principalement côté serveur.

Développé par **Neytabi** et **Pill0N_**.

## ✨ Fonctionnalités
- **Interface Graphique via Livres (GUI)** : Le Conteur dispose d'un "Grimoire" en jeu (livre enchanté) lui permettant d'accéder à des menus cliquables pour gérer le tribunal, attribuer les rôles, gérer la nuit, mute/unmute les joueurs, sans avoir à taper de commandes fastidieuses !
- **Gestion du Cycle Jour/Nuit** : Commandes et menus pour isoler les joueurs, bloquer le chat, etc.
- **Rôles Secrets et BossBar** : Attribution aléatoire ou manuelle des rôles qui s'affichent via un bandeau discret.
- **Tribunal & Chaises** : Assignation automatique des sièges et gestion des votes via le chat.
- **Intégration Vocale** : Connecté automatiquement avec **Simple Voice Chat** pour mute/unmute les morts ou lors des phases de jeu.
- **Presets multi-maps** : Créez et sauvegardez la configuration de plusieurs maps.

## 🛠️ Prérequis
1. Un serveur **PaperMC** (version 1.20 ou supérieure, testé en 1.21.1).
2. Le plugin **[Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat)** installé sur le serveur (API requise pour la gestion des mutes vocaux).

## 📥 Installation
1. Téléchargez la dernière version du plugin dans l'onglet **Versions**.
2. Placez le fichier `.jar` dans le dossier `plugins/` de votre serveur.
3. Démarrez le serveur. Le dossier `plugins/BOTC-Plugins/` se créera avec sa configuration par défaut.

## 🎮 Comment ça marche ? (Pour le Conteur)
En tant qu'opérateur du serveur (OP), vous recevrez automatiquement un livre nommé **"📖 Le Grimoire du Conteur"** lors de la connexion (ou après un `/botc reset`). 
Il suffit d'ouvrir ce livre et de cliquer sur les liens dans le chat pour ouvrir les interfaces graphiques (inventaires interactifs). C'est le moyen le plus simple de gérer la partie !

### Commandes Principales
Pour ceux qui préfèrent les commandes, toutes s'effectuent via l'alias `/botc` :

- `/botc setup` : Lance l'interface d'installation (attribution des chaises, spawn, etc.)
- `/botc start` : Démarre la partie (assied tout le monde et donne le livre aux joueurs).
- `/botc nuit / jour / libre` : Gère le déroulement temporel et les mutes automatiques.
- `/botc role assign` : Ouvre le menu d'attribution des rôles.
- `/botc tribunal` : Gère les votes et les nominations.
- `/botc reset` : Réinitialise tout (rend la vie aux morts, efface les rôles).
- `/botc preset <nom>` : Crée ou charge une nouvelle map pré-configurée.

## 🤝 Contribuer
Ce projet est open-source sous licence MIT. N'hésitez pas à proposer des *Pull Requests* ou signaler des bugs sur le repository !

Introduction
Ce projet est en deux parties et se déroule sur trois séances de 3H30.

Vous devrez rendre les parties 1 et 2 , une semaine après la fin de la derniere séance. Le rendu se fait sous la forme d'une archive (zip, tar, ...) à déposer sur Chamilo, l'archive peut contenir les commits via le répertoire .git si vous l'avez utilisé.

Le projet est à réaliser en monôme.

Avant chaque partie, vous devez produire un document de conception présentant schématiquement les classes et l'architecure prévues. Ce document fera partie du rendu. Le format est libre, une photo d'un croquis papier convient.

De même chaque partie devra inclure une option "démo" permettant d'executer le programme en présentant des déplacements corrects, incorrects, des prises de pièces, ...

Partie 1 du projet
Dans cette partie 1 du projet, il est attendu un programme Java qui permettra de déplacer des pièces sur un échiquier, les pièces étant celles du jeu traditionnel.

Votre programme devra afficher au départ le plateau d'échec traditionnel dans un terminal. Votre programme demande ensuite à l'utilisateur de saisir le coup des blancs. Pour cela, le programme demande tout d'abord les coordonnées de la case de départ puis les coordonnées de la case d'arrivée.

Deux cas sont alors possibles :

votre programme indique que ce mouvement est invalide et propose au joueur de rejouer
votre programme affiche de nouveau le plateau avec le coup effectué, puis demande à l'utilisateur de saisir le coup des noirs.
Ainsi, il est possible de faire une partie complète.

Voici des informations complèmentaires :

1 / Pour savoir comment les pièces se déplacent ou réalisent des prises, voici un lien utile : https://atelier-canope-19.canoprof.fr/eleve/Formation%20initiale%20et%20continue/Des_s%C3%A9quences_de_classe/Les%20%C3%A9checs%20%C3%A0%20l'%C3%A9cole/res/Deplacements_echecs.pdf Pour le pion, vous noterez qu'il y a une différence entre le déplacement, la prise d'une pièce, le premier déplacement possible.

2 / Il est demandé de gérer uniquement les mouvements de base. Vous pouvez exclure :

la gestion de l'échec du roi
le roque
la prise en passant du pion
la promotion du pion sur la dernière ligne
3/ Vous devez par contre gérer la prise des pièces

4 / Pour réaliser l'affichage dans le terminal, nous vous proposons le code suivant à adapter comme bon vous semble :

Projet/Chess.java

Partie 2 du projet
La société Chess!$boring souhaite révolutionner le jeu d'échecs. Pour cela, ils font appel à vous pour pouvoir définir une révolution dans ce jeu.

Dans cette nouvelle version du jeu d'échecs, avant le lancement d'une partie, les joueurs peuvent charger un fichier qui permet d'ajouter des nouvelles pièces, qui ont leurs propres règles de déplacement et de prise, et de préciser leur localisation au départ.

Exemple : on indique qu'à la case A1 se trouvera désormais la pièce "LION", de code unicode U+1F981, appartenant au camp de blancs, et qui peut se deplacer uniquement de deux cases en diagonale.

Le fichier sera au format JSON, à définir par vos soins. Attention à bien gérer tous les cas possibles.

Pour lire et écrire le fichier Json, vous pouvez utiliser une librairie Java comme GSON. Le contenu du fichier JSON sera un paramètrage des caractèrisques de la pièce.

Bonus si vous avez tout fini
Vous pouvez * utiliser une bibliothèque graphique * gérer les mouvements plus complexes (gestion de l'échec du roi , le roque , la prise en passant du pion , la promotion du pion sur la dernière ligne ) * mettre un système de jeu automatique (IA) pour les noirs
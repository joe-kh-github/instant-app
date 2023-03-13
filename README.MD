## Description
L'application mobile appel l'endpoint getParkings en passant en paramètre l'addresse ou en passant en paramètre
latitude, longitude et distance, le service appellera la première dataset avec des filtres pour récupérer la liste des parkings puis
il appellera la deuxième dataset avec des filtres et ensuite il fusionnera les deux résultats en une seule liste selon le nom du parking 
et enfin il retournera le résultat final

## Les choix que j'ai fait
* Utilisation du mapstruct et lombok
* Gérer les exceptions dans l'advice controlleur
* Mettre tous les services externes dans le dossier api
* Définition des modèles côté API et côté Application
* Faire abstraction du service parking dans le cas où on a une autre implementation

## Les problèmes que je n'ai pas traités, mais que j'ai identifiés
* Le tri et la pagination des résultats
* Ajouter le contrat d'interface du GrandPoitiers et utiliser le plugin openapi afin de générer les modèles et les entités
* De prendre en considération aussi le tarif par heure si le client souhaite rechercher un parking moins cher que les autres parkings
* Implémenter un system event driven pour éviter que l'application mobile appel à chaque fois le service rest pour récupérer les parkings vacants les plus proches (Websocket, Kafka, ...)
* Ajouter l'authentification/Autorisation
* Paralléliser l'appel aux deux datasets pour que la deuxième dataset ne soit pas bloquée par la première
* Prendre en considération le quota limite de l'api qui retourne en temps réel les places disponibles dans un parking

## Temps consacré pour l'exercice
Une journée
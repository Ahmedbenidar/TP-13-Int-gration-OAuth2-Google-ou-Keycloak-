# TP 13 — Intégration OAuth2 / OpenID Connect avec Keycloak



## Lancer le projet

### 1. Démarrer le fournisseur d'identité

Copier le realm fourni dans le dossier d'import de Keycloak :

```bash
cp keycloak/usmba-realm.json <KEYCLOAK_HOME>/data/import/
```

Puis démarrer Keycloak sur le port **8280** :

```bash
<KEYCLOAK_HOME>/bin/kc.bat start-dev --http-port=8280 --import-realm
```

Le journal doit afficher `Realm 'usmba-realm' imported`, puis
`Listening on: http://0.0.0.0:8280`. La configuration du realm est vérifiable sur
`http://localhost:8280/realms/usmba-realm/.well-known/openid-configuration`.

### 2. Démarrer l'application

```bash
mvn spring-boot:run
```

L'application écoute sur **http://localhost:8100**. Aucune base de données n'est nécessaire.

### 3. Se connecter

Ouvrir `http://localhost:8100/espace` : le portail redirige vers la page de connexion
Keycloak du realm `usmba-realm`, puis revient sur `/espace`.

## Résultats obtenus

### Page d'accueil publique

![Page d'accueil](docs/screenshots/01-accueil.png)

La page racine est accessible sans authentification et n'affiche que le bandeau « visiteur ».
Elle explique en quatre points que le mot de passe n'est jamais saisi sur le portail : la
vérification a lieu chez le fournisseur, qui renvoie ensuite un jeton d'identité.

### Page de connexion du portail

![Page de connexion](docs/screenshots/02-page-connexion.png)

Demander `/espace` sans session renvoie un 302 vers `/connexion`. Cette page ne contient
volontairement aucun champ mot de passe : elle ne sert qu'à présenter le fournisseur et à
proposer le bouton « Continuer avec USMBA » qui pointe vers `/oauth2/authorization/usmba`.

### Formulaire du fournisseur d'identité

![Formulaire Keycloak](docs/screenshots/03-fournisseur-connexion.png)

Le clic sur le bouton précédent déclenche la redirection OpenID Connect. L'URL contient bien
`response_type=code`, `client_id=portail-identite-client`, `scope=openid profile email`,
un `state` et un `nonce` aléatoires, ainsi que
`redirect_uri=http://localhost:8100/login/oauth2/code/usmba`. Le formulaire affiché est celui
de Keycloak, en français, avec le nom du realm en en-tête : le portail ne voit jamais le mot de
passe.

### Espace personnel après authentification

![Espace membre](docs/screenshots/04-espace-membre.png)

Après l'échange du code d'autorisation contre un jeton, Spring Security ouvre une session
locale et redirige vers `/espace`. Le nom, l'email, l'identifiant fournisseur, le service et
les rôles sont lus dans le jeton. Seuls les dossiers du déposant connecté sont listés : ici les
trois dossiers de `s.idalen`, avec leurs statuts.

### Attributs du jeton et autorités Spring

![Profil et attributs](docs/screenshots/05-profil-attributs.png)

La page `/profil` détaille tous les claims de l'ID Token : `iss`, `sub`, `aud`, `exp`, `iat`,
`nonce`, `preferred_username`, `email`, `realm_access`, le claim métier `service`, etc.
En dessous sont listées les autorités effectivement chargées dans le contexte de sécurité,
dont `ROLE_MEMBRE` construite à partir du claim `realm_access.roles`.

### Contrôle d'accès refusé

![Accès refusé](docs/screenshots/06-acces-refuse-403.png)

Avec le compte `s.idalen`, qui n'a que le rôle `MEMBRE`, l'URL
`/administration/tableau-de-bord` est refusée. Spring Security renvoie un 403 et affiche la page
`/acces-refuse`, qui rappelle l'identité connectée, les rôles réellement présents dans le jeton
et le rôle exigé. L'utilisateur reste authentifié : seul l'accès à la ressource est bloqué.

### Tableau de bord de l'administration

![Administration](docs/screenshots/07-administration.png)

Après déconnexion puis connexion avec `a.benidar`, porteur du rôle `ADMIN`, la même URL répond
cette fois en 200. Le tableau de bord présente les compteurs par statut et l'intégralité des cinq
dossiers, tous déposants confondus, avec la ligne de rappel de l'administrateur connecté.

### Déconnexion

![Déconnexion](docs/screenshots/08-deconnexion.png)

`/deconnexion` invalide la session, efface le contexte de sécurité et supprime le cookie
`PORTAIL_SESSION`, puis redirige vers l'accueil avec un message de confirmation. Le bandeau
repasse en « visiteur » : l'accès à `/espace` redéclenchera une nouvelle authentification.

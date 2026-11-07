package ma.usmba.identite.service;

import ma.usmba.identite.modele.ProfilConnecte;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class ServiceProfil {

    private static final String PREFIXE_ROLE = "ROLE_";

    public ProfilConnecte construire(OAuth2User utilisateur) {
        Map<String, Object> attributs = utilisateur.getAttributes();

        String identifiant = texte(attributs, "preferred_username");
        if (identifiant == null) {
            identifiant = utilisateur.getName();
        }

        String prenom = texte(attributs, "given_name");
        String nom = texte(attributs, "family_name");
        String nomComplet = texte(attributs, "name");
        if (nomComplet == null) {
            nomComplet = ((prenom == null ? "" : prenom) + " " + (nom == null ? "" : nom)).trim();
        }

        List<String> roles = roles(utilisateur.getAuthorities());

        return new ProfilConnecte(
                identifiant,
                nomComplet,
                prenom,
                nom,
                texte(attributs, "email"),
                texte(attributs, "service"),
                roles,
                roles.contains("ADMIN")
        );
    }

    public boolean estAdministrateur(OAuth2User utilisateur) {
        return roles(utilisateur.getAuthorities()).contains("ADMIN");
    }

    /** Date d'expiration de l'ID Token, utile pour montrer que le jeton est temporaire. */
    public String expirationLisible(OAuth2User utilisateur) {
        if (utilisateur instanceof OidcUser oidc && oidc.getIdToken() != null) {
            return oidc.getIdToken().getExpiresAt() == null
                    ? "non communiquee"
                    : oidc.getIdToken().getExpiresAt().toString();
        }
        return "non communiquee";
    }

    private List<String> roles(Collection<? extends GrantedAuthority> autorites) {
        return autorites.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith(PREFIXE_ROLE))
                .map(a -> a.substring(PREFIXE_ROLE.length()))
                .sorted()
                .toList();
    }

    private String texte(Map<String, Object> attributs, String cle) {
        Object valeur = attributs.get(cle);
        if (valeur == null) {
            return null;
        }
        String texte = String.valueOf(valeur).trim();
        return texte.isEmpty() ? null : texte;
    }
}

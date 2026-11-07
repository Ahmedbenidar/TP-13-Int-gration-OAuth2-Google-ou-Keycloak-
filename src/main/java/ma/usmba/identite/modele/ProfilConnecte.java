package ma.usmba.identite.modele;

import java.util.List;

/**
 * Vue simplifiee de l'identite renvoyee par le fournisseur OpenID Connect.
 * Les attributs sont lus dans l'ID Token, jamais dans une base locale.
 */
public record ProfilConnecte(
        String identifiant,
        String nomComplet,
        String prenom,
        String nom,
        String email,
        String service,
        List<String> roles,
        boolean administrateur
) {

    public String initiales() {
        String p = prenom == null || prenom.isBlank() ? "" : prenom.substring(0, 1);
        String n = nom == null || nom.isBlank() ? "" : nom.substring(0, 1);
        String resultat = (p + n).toUpperCase();
        return resultat.isBlank() ? "?" : resultat;
    }
}

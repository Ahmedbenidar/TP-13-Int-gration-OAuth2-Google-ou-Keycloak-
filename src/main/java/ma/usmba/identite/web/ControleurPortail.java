package ma.usmba.identite.web;

import ma.usmba.identite.modele.ProfilConnecte;
import ma.usmba.identite.service.CatalogueDossiers;
import ma.usmba.identite.service.ServiceProfil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControleurPortail {

    private final ServiceProfil serviceProfil;
    private final CatalogueDossiers catalogue;

    public ControleurPortail(ServiceProfil serviceProfil, CatalogueDossiers catalogue) {
        this.serviceProfil = serviceProfil;
        this.catalogue = catalogue;
    }

    /** Page publique : elle reste accessible sans jeton. */
    @GetMapping({"/", "/accueil"})
    public String accueil() {
        return "accueil";
    }

    /** Page de connexion minimale : le bouton lance le flux OAuth2 gere par Spring. */
    @GetMapping("/connexion")
    public String connexion() {
        return "connexion";
    }

    /** Page protegee : Spring a deja redirige vers le fournisseur si necessaire. */
    @GetMapping("/espace")
    public String espace(Model modele, @AuthenticationPrincipal OidcUser utilisateur) {
        ProfilConnecte profil = serviceProfil.construire(utilisateur);
        modele.addAttribute("profil", profil);
        modele.addAttribute("mesDossiers", catalogue.parDeposant(profil.identifiant()));
        modele.addAttribute("totalDossiers", catalogue.tous().size());
        modele.addAttribute("expirationJeton", serviceProfil.expirationLisible(utilisateur));
        return "espace";
    }

    @GetMapping("/profil")
    public String profil(Model modele, @AuthenticationPrincipal OidcUser utilisateur) {
        modele.addAttribute("profil", serviceProfil.construire(utilisateur));
        modele.addAttribute("attributs", utilisateur.getClaims());
        modele.addAttribute("autorites", utilisateur.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .sorted()
                .toList());
        modele.addAttribute("expirationJeton", serviceProfil.expirationLisible(utilisateur));
        return "profil";
    }

    @GetMapping("/acces-refuse")
    public String accesRefuse(Model modele, @AuthenticationPrincipal OidcUser utilisateur) {
        if (utilisateur != null) {
            modele.addAttribute("profil", serviceProfil.construire(utilisateur));
        }
        return "acces-refuse";
    }
}

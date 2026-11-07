package ma.usmba.identite.web;

import ma.usmba.identite.service.CatalogueDossiers;
import ma.usmba.identite.service.ServiceProfil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControleurAdministration {

    private final CatalogueDossiers catalogue;
    private final ServiceProfil serviceProfil;

    public ControleurAdministration(CatalogueDossiers catalogue, ServiceProfil serviceProfil) {
        this.catalogue = catalogue;
        this.serviceProfil = serviceProfil;
    }

    /** Reserve au role ADMIN : la regle est declaree dans ConfigSecurite. */
    @GetMapping("/administration/tableau-de-bord")
    public String tableauDeBord(Model modele, @AuthenticationPrincipal OidcUser utilisateur) {
        modele.addAttribute("profil", serviceProfil.construire(utilisateur));
        modele.addAttribute("dossiers", catalogue.tous());
        modele.addAttribute("valides", catalogue.compter("VALIDE"));
        modele.addAttribute("enAttente", catalogue.compter("EN_ATTENTE"));
        modele.addAttribute("refuses", catalogue.compter("REFUSE"));
        return "administration";
    }
}

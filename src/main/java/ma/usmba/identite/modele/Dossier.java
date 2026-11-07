package ma.usmba.identite.modele;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Dossier(
        String reference,
        String intitule,
        String statut,
        BigDecimal montant,
        LocalDate dateDepot,
        String deposant
) {
}

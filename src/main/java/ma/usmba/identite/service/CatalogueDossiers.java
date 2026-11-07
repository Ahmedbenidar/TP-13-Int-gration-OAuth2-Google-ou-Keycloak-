package ma.usmba.identite.service;

import ma.usmba.identite.modele.Dossier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogueDossiers {

    private final List<Dossier> dossiers = List.of(
            new Dossier("DOS-2024-001", "Demande de bourse de recherche",
                    "VALIDE", new BigDecimal("15000.00"), LocalDate.of(2024, 2, 12), "s.idalen"),
            new Dossier("DOS-2024-002", "Attestation de scolarite",
                    "VALIDE", new BigDecimal("0.00"), LocalDate.of(2024, 2, 20), "s.idalen"),
            new Dossier("DOS-2024-003", "Remboursement de frais de stage",
                    "EN_ATTENTE", new BigDecimal("2400.50"), LocalDate.of(2024, 3, 4), "s.idalen"),
            new Dossier("DOS-2024-004", "Inscription au seminaire Jakarta EE",
                    "EN_ATTENTE", new BigDecimal("800.00"), LocalDate.of(2024, 3, 11), "k.ouazzani"),
            new Dossier("DOS-2024-005", "Materiel informatique",
                    "REFUSE", new BigDecimal("12500.00"), LocalDate.of(2024, 3, 18), "k.ouazzani")
    );

    public List<Dossier> tous() {
        return dossiers;
    }

    public List<Dossier> parDeposant(String identifiant) {
        return dossiers.stream()
                .filter(d -> d.deposant().equalsIgnoreCase(identifiant))
                .collect(Collectors.toList());
    }

    public long compter(String statut) {
        return dossiers.stream().filter(d -> d.statut().equals(statut)).count();
    }
}

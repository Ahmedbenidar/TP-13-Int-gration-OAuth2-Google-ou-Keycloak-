package ma.usmba.identite.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Enveloppe un OidcUser pour y ajouter les roles du realm (claim realm_access.roles)
 * que Spring ne convertit pas automatiquement en autorites.
 */
public class UtilisateurOidcRoles implements OidcUser {

    private final OidcUser delegate;
    private final List<GrantedAuthority> autorites;

    public UtilisateurOidcRoles(OidcUser delegate) {
        this.delegate = delegate;
        List<GrantedAuthority> fusion = new ArrayList<>(delegate.getAuthorities());
        for (Object role : rolesDuRealm(delegate)) {
            String autorite = "ROLE_" + role;
            if (fusion.stream().noneMatch(a -> a.getAuthority().equals(autorite))) {
                fusion.add(new SimpleGrantedAuthority(autorite));
            }
        }
        this.autorites = List.copyOf(fusion);
    }

    @SuppressWarnings("unchecked")
    private static List<Object> rolesDuRealm(OidcUser utilisateur) {
        Object realmAccess = utilisateur.getClaims().get("realm_access");
        if (realmAccess instanceof Map<?, ?> map) {
            Object roles = map.get("roles");
            if (roles instanceof Collection<?> collection) {
                return new ArrayList<>(collection);
            }
        }
        return List.of();
    }

    @Override
    public Map<String, Object> getClaims() {
        return delegate.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return delegate.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return delegate.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return autorites;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Instant getExpiresAt() {
        return delegate.getExpiresAt();
    }

    @Override
    public Instant getIssuedAt() {
        return delegate.getIssuedAt();
    }
}

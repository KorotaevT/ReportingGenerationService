package cs.vsu.ReportingGenerationService.util;

import cs.vsu.ReportingGenerationService.model.User;

public class AuthorityUtil {

    public static Boolean ihasRole(String role, User user){
        return user.getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals(role));
    }
}

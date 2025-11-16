package judgeoverflow.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommitterRole {
    ADMIN("ADMIN"),
    USER("USER");

    private final String role;
}

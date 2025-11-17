package judgeoverflow.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JasyptConfigTest {
    private PooledPBEStringEncryptor encryptor;

    private final String ENCRYPT_KEY = "password";

    @BeforeEach
    void setUp() {
        encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(ENCRYPT_KEY);//암호화 키
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");//암호화 알고리즘
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
    }

    @Test
    @DisplayName("설정 암호화 테스트")
    void encryptDatabaseConfig() {
        // given
        String googleId = "googleId";
        String googleSecret = "googleSecret";
        String githubId = "githubId";
        String githubSecret = "githubSecret";
//        String url = "jdbc:postgresql://localhost:5432/your_database_name";
        String username = "username";
        String password = "password";

        // when
        String encryptedGoogleId = encryptor.encrypt(googleId);
        String encryptedGoogleSecret = encryptor.encrypt(googleSecret);
        String encryptedGithubId = encryptor.encrypt(githubId);
        String encryptedGithubSecret = encryptor.encrypt(githubSecret);
//        String encryptedUrl = encryptor.encrypt(url);
        String encryptedUsername = encryptor.encrypt(username);
        String encryptedPassword = encryptor.encrypt(password);

        // then
        assertThat(encryptedGoogleId).isNotEqualTo(googleId);
        assertThat(encryptedGoogleSecret).isNotEqualTo(googleSecret);
        assertThat(encryptedGithubId).isNotEqualTo(githubId);
        assertThat(encryptedGithubSecret).isNotEqualTo(githubSecret);
//        assertThat(encryptor.decrypt(encryptedUrl)).isEqualTo(url);
        assertThat(encryptor.decrypt(encryptedUsername)).isEqualTo(username);
        assertThat(encryptor.decrypt(encryptedPassword)).isEqualTo(password);

        // 암호화된 설정값 출력
        System.out.println("====== Encrypted Database Configuration ======");
        System.out.println("Google ID: ENC(" + encryptedGoogleId + ")");
        System.out.println("Google Secret: ENC(" + encryptedGoogleSecret + ")");
        System.out.println("Github ID: ENC(" + encryptedGithubId + ")");
        System.out.println("Github Secret: ENC(" + encryptedGithubSecret + ")");
//        System.out.println("URL: ENC(" + encryptedUrl + ")");
        System.out.println("Username: ENC(" + encryptedUsername + ")");
        System.out.println("Password: ENC(" + encryptedPassword + ")");
    }

    @Test
    @DisplayName("문자열 암호화 및 복호화 테스트")
    @Deprecated
    void encryptAndDecrypt() {
        // given
        String plainText = "plainText";

        // when
        String encryptedText = encryptor.encrypt(plainText);
        String decryptedText = encryptor.decrypt(encryptedText);

        // then
        assertThat(decryptedText).isEqualTo(plainText);
        assertThat(encryptedText).isNotEqualTo(plainText);

        // 암호화된 설정값 출력
        System.out.println("plainText: ENC(" + encryptedText + ")");
    }
}

package pt.pauloortolan.plm_back.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pauloortolan.plm_back.dto.*;
import pt.pauloortolan.plm_back.model.GeneratedCode;
import pt.pauloortolan.plm_back.repository.GeneratedCodeRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodeServiceTest {

    @Mock
    private GeneratedCodeRepository repository;

    private CodeService codeService;
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        codeService = new CodeService(repository);
    }

    @Test
    void generateCode_success_returnsValidCode() {
        when(repository.findByCodeAndEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(repository.save(any(GeneratedCode.class))).thenAnswer(inv -> inv.getArgument(0));

        GenerateResponse response = codeService.generateCode(TEST_EMAIL);

        assertNotNull(response);
        assertNotNull(response.code());
        assertEquals(6, response.code().length());
        assertTrue(response.code().matches("\\d{6}"));
        assertEquals(TEST_EMAIL, response.email());
        verify(repository).save(any(GeneratedCode.class));
    }

    @Test
    void generateCode_duplicateCode_retriesAndSucceeds() {
        when(repository.findByCodeAndEmail(anyString(), anyString()))
                .thenReturn(Optional.of(new GeneratedCode("123456", TEST_EMAIL)))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty());
        when(repository.save(any(GeneratedCode.class))).thenAnswer(inv -> inv.getArgument(0));

        GenerateResponse response = codeService.generateCode(TEST_EMAIL);

        assertNotNull(response);
        verify(repository, atLeast(2)).findByCodeAndEmail(anyString(), anyString());
    }

    @Test
    void generateCode_tooManyCollisions_throwsException() {
        when(repository.findByCodeAndEmail(anyString(), anyString())).thenReturn(Optional.of(new GeneratedCode("123456", TEST_EMAIL)));

        assertThrows(IllegalStateException.class, () -> codeService.generateCode(TEST_EMAIL));
    }

    @Test
    void validateCode_codeExistsAndNotExpired_returnsValid() {
        GeneratedCode code = new GeneratedCode("123456", TEST_EMAIL);
        code.setCreatedAt(LocalDateTime.now());
        when(repository.findByCodeAndEmail("123456", TEST_EMAIL)).thenReturn(Optional.of(code));

        ValidateResponse response = codeService.validateCode("123456", TEST_EMAIL);

        assertTrue(response.valid());
        assertEquals("Code valid", response.message());
    }

    @Test
    void validateCode_codeNotFound_returnsInvalid() {
        when(repository.findByCodeAndEmail("999999", TEST_EMAIL)).thenReturn(Optional.empty());

        ValidateResponse response = codeService.validateCode("999999", TEST_EMAIL);

        assertFalse(response.valid());
        assertEquals("Code invalid", response.message());
    }

    @Test
    void validateCode_codeExpired_returnsExpired() {
        GeneratedCode code = new GeneratedCode("123456", TEST_EMAIL);
        code.setCreatedAt(LocalDateTime.now().minusMinutes(6));
        when(repository.findByCodeAndEmail("123456", TEST_EMAIL)).thenReturn(Optional.of(code));

        ValidateResponse response = codeService.validateCode("123456", TEST_EMAIL);

        assertFalse(response.valid());
        assertEquals("Code expired", response.message());
    }
}
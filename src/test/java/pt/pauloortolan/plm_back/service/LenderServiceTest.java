package pt.pauloortolan.plm_back.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.pauloortolan.plm_back.model.Lender;
import pt.pauloortolan.plm_back.repository.LenderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LenderServiceTest {

    @Mock
    private LenderRepository repository;

    private LenderService lenderService;

    @BeforeEach
    void setUp() {
        lenderService = new LenderService(repository);
    }

    @Test
    void create_success_returnsLenderResponse() {
        LenderService.CreateLenderRequest request = new LenderService.CreateLenderRequest(
                "John Doe", "+1234567890", "IBAN123", "123 Main St");
        
        Lender savedLender = Lender.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .phone("+1234567890")
                .bankData("IBAN123")
                .address("123 Main St")
                .build();
        savedLender.setCreatedAt(LocalDateTime.now());
        savedLender.setUpdatedAt(LocalDateTime.now());
        
        when(repository.save(any(Lender.class))).thenReturn(savedLender);

        LenderService.LenderResponse response = lenderService.create(request);

        assertNotNull(response);
        assertEquals("John Doe", response.name());
        assertEquals("+1234567890", response.phone());
        verify(repository).save(any(Lender.class));
    }

    @Test
    void create_withMinimalData_succeeds() {
        LenderService.CreateLenderRequest request = new LenderService.CreateLenderRequest(
                "Jane Doe", null, null, null);
        
        Lender savedLender = Lender.builder()
                .id(UUID.randomUUID())
                .name("Jane Doe")
                .build();
        savedLender.setCreatedAt(LocalDateTime.now());
        savedLender.setUpdatedAt(LocalDateTime.now());
        
        when(repository.save(any(Lender.class))).thenReturn(savedLender);

        LenderService.LenderResponse response = lenderService.create(request);

        assertNotNull(response);
        assertEquals("Jane Doe", response.name());
        assertNull(response.phone());
    }

    @Test
    void update_success_updatesLender() {
        UUID lenderId = UUID.randomUUID();
        Lender existingLender = Lender.builder()
                .id(lenderId)
                .name("Old Name")
                .phone("+1111111111")
                .build();
        existingLender.setCreatedAt(LocalDateTime.now());
        existingLender.setUpdatedAt(LocalDateTime.now());

        LenderService.UpdateLenderRequest request = new LenderService.UpdateLenderRequest(
                "New Name", "+2222222222", null, null);

        when(repository.findById(lenderId)).thenReturn(Optional.of(existingLender));
        when(repository.save(any(Lender.class))).thenAnswer(inv -> inv.getArgument(0));

        LenderService.LenderResponse response = lenderService.update(lenderId, request);

        assertNotNull(response);
        assertEquals("New Name", response.name());
        assertEquals("+2222222222", response.phone());
    }

    @Test
    void update_notFound_throwsException() {
        UUID lenderId = UUID.randomUUID();
        LenderService.UpdateLenderRequest request = new LenderService.UpdateLenderRequest(
                "New Name", null, null, null);

        when(repository.findById(lenderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> lenderService.update(lenderId, request));
    }

    @Test
    void query_byName_returnsMatchingLenders() {
        String nameFilter = "John";
        Lender lender = Lender.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .build();
        
        when(repository.findByNameContaining(nameFilter)).thenReturn(List.of(lender));

        List<LenderService.LenderResponse> results = lenderService.query(nameFilter, null);

        assertEquals(1, results.size());
        assertEquals("John Doe", results.get(0).name());
    }

    @Test
    void query_byPhone_returnsMatchingLenders() {
        String phoneFilter = "+1234567890";
        Lender lender = Lender.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .phone("+1234567890")
                .build();
        
        when(repository.findByPhoneContaining(phoneFilter)).thenReturn(List.of(lender));

        List<LenderService.LenderResponse> results = lenderService.query(null, phoneFilter);

        assertEquals(1, results.size());
        assertEquals("+1234567890", results.get(0).phone());
    }

    @Test
    void query_noFilters_returnsAllLenders() {
        Lender lender1 = Lender.builder().id(UUID.randomUUID()).name("John").build();
        Lender lender2 = Lender.builder().id(UUID.randomUUID()).name("Jane").build();
        
        when(repository.findAll()).thenReturn(List.of(lender1, lender2));

        List<LenderService.LenderResponse> results = lenderService.query(null, null);

        assertEquals(2, results.size());
    }

    @Test
    void getById_success_returnsLender() {
        UUID lenderId = UUID.randomUUID();
        Lender lender = Lender.builder()
                .id(lenderId)
                .name("John Doe")
                .phone("+1234567890")
                .build();
        lender.setCreatedAt(LocalDateTime.now());
        lender.setUpdatedAt(LocalDateTime.now());

        when(repository.findById(lenderId)).thenReturn(Optional.of(lender));

        LenderService.LenderResponse response = lenderService.getById(lenderId);

        assertNotNull(response);
        assertEquals(lenderId, response.id());
        assertEquals("John Doe", response.name());
    }

    @Test
    void getById_notFound_throwsException() {
        UUID lenderId = UUID.randomUUID();
        when(repository.findById(lenderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> lenderService.getById(lenderId));
    }

    @Test
    void query_byPhonePartial_returnsMatchingLenders() {
        String phoneFilter = "+1234";
        Lender lender1 = Lender.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .phone("+1234567890")
                .build();
        Lender lender2 = Lender.builder()
                .id(UUID.randomUUID())
                .name("Jane Doe")
                .phone("+1234987654")
                .build();
        
        when(repository.findByPhoneContaining(phoneFilter)).thenReturn(List.of(lender1, lender2));

        List<LenderService.LenderResponse> results = lenderService.query(null, phoneFilter);

        assertEquals(2, results.size());
    }

    @Test
    void query_withPhonePartialInFilter_combinesWithName() {
        String nameFilter = "John";
        String phoneFilter = "+1234";
        
        when(repository.findByFilters(nameFilter, phoneFilter)).thenReturn(List.of());

        lenderService.query(nameFilter, phoneFilter);

        verify(repository).findByFilters(nameFilter, phoneFilter);
    }
}
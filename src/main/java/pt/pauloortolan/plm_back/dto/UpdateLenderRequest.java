package pt.pauloortolan.plm_back.dto;

public record UpdateLenderRequest(String name, String phone, String bankData, String address) {
}
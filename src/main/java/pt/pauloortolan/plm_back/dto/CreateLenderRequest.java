package pt.pauloortolan.plm_back.dto;

public record CreateLenderRequest(String name, String phone, String bankData, String address) {}
package pt.pauloortolan.plm_back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pt.pauloortolan.plm_back.dto.GenerateRequest;
import pt.pauloortolan.plm_back.dto.GenerateResponse;
import pt.pauloortolan.plm_back.dto.ValidateRequest;
import pt.pauloortolan.plm_back.dto.ValidateResponse;
import pt.pauloortolan.plm_back.model.GeneratedCode;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CodeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    GeneratedCode toEntity(GenerateRequest request, String code);

    GenerateResponse toResponse(GeneratedCode entity);

    ValidateResponse toResponseValid(String message);

    ValidateResponse toResponseInvalid(String message);
}